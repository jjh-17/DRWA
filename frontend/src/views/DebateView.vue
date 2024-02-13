<template>
  <header>
    <DebateHeaderBar :headerBarTitle="headerBarTitle"/>
  </header>
  <div>
    12
  </div>
  <footer>
    <DebateBottomBar />
  </footer>
</template>

<script setup>
import DebateHeaderBar from '@/components/debate/DebateHeaderBar.vue'
import DebateBottomBar from '@/components/debate/DebateBottomBar.vue'

import { ref, reactive, toRefs } from 'vue'
import { useDebateStore } from '@/stores/useDebateStore'
import { useRoute } from 'vue-router'
import { OpenVidu } from 'openvidu-browser'
import UserVideo from '@/components/debate/UserVideo.vue'
import { team } from '@/components/common/Team.js'
import { onMounted } from "vue"
import { storeToRefs } from "pinia"

// === 변수 ===
// Debate 정보
const route = useRoute()
const debateId = route.params.debateId
const debateStore = useDebateStore()
const debate = debateStore.getDebate(debateId)
const headerBarTitle = ref('[임시]제목입니다.')
// headerBarTitle = debate.getTitle();

// 세션 정보
const sessionInfo = reactive({
  session: undefined,
  OV: undefined,
  debateId: route.params.debateId,
  publisher: undefined,  // 자기 자신
  subscriber: undefined,

  // index == 각 팀에서의 순서
  addTo : '',
  teamLeftList: [],   // 팀 A 리스트(자기 자신 포함 가능)
  teamRightList: [],  // 팀 B 리스트(자기 자신 포함 가능)
});

// 참가자 정보
const authStore = useAuthStore();
const { memberId,  } = storeToRefs(authStore);
const playerInfo = reactive({
  memberId: 'memberId' + Math.floor(Math.random() * 100),
  nickname: 'nickname'+Math.floor(Math.random() * 100),
  team: team[3].english,
  order: -1,

  // index == 각 팀에서의 순서
  // 각 팀 플레이어의 {memberId, nickname } 저장
  playerLeftList: [],
  playerRightList: [],
});

// 화상 정보
const communication = reactive({
  micList: [],
  cameraList: [],
  selectedMic: '',
  selectedCamera: [],

  isMicOn: false,
  isCameraOn: false,
  isShareOn: false,

  isMicHandleAvailable: false,
  isCameraHandleAvailable: false,
  isShareHandleAvailable: false,
});
  

// 채팅방
const chatting = reactive({
  targetTeam: team[4].english,
  messagesLeft: [],
  messagesRight: [],
  messagesAll: [],
});

// 자신의 팀 내 순서
const getOrder = computed(() => {
  if (sessionInfo.session) {
    if (playerInfo.team == team[0].english) {
      return sessionInfo.teamLeftList.indexOf(sessionInfo.publisher, 0)
    } else if (playerInfo.team == team[1].english) {
      return sessionInfo.teamRightList.indexOf(sessionInfo.publisher, 0)
    }
  }
  return -1;
})

// 세션에 팀별로 합류
function joinSession() {
  // 팀 세션 생성
  sessionInfo.OV = new OpenVidu()
  sessionInfo.session = sessionInfo.OV.initSession()

  // 다른 사용자의 stream(publisher) 생성 감지 이벤트
  sessionInfo.session.on('streamCreated', ({ stream }) => {
    // 새로운 subscriber
    const subscriber = sessionInfo.session.subscribe(stream)

    // 새로운 참가자의 클라이언트 정보(memberId, nickname)를 받아옴
    const clientDatas = subscriber.stream.connection.data.split('"');
    const datas = clientDatas[3].split(',');

    // addTo에 따라 팀A, 팀B에 데이터 저장
    if (sessionInfo.addTo == team[0].english) {
      sessionInfo.addTo = ''
      sessionInfo.teamLeftList.push(subscriber)
      playerInfo.playerLeftList.push({
        memberId: datas[0],
        nickname: datas[1],
      });
      playerInfo.order = getOrder();
    } else if (sessionInfo.addTo == team[1].english) {
      sessionInfo.addTo = ''
      sessionInfo.teamRightList.push(subscriber)
      playerInfo.playerRightList.push({
        memberId: datas[0],
        nickname: datas[1],
      });
      playerInfo.order = getOrder();
    } else {
      console.error(`잘못된 addTo(${sessionInfo.addTo}) 입니다.`)
    }
  });

  // 다른 사용자의 stream 종료 감지(juror, watcher는 해당 X)
  sessionInfo.session.on('streamDestroyed', ({ stream }) => {
    leaveTeam(stream.streamManager);
  })

  // 채팅 이벤트 수신 처리(닉네임, 목표 팀, 메시지 내용) => targetTeam에 따라 메시지 저장 공간 변화
  sessionInfo.session.on('signal:chat', (event) => {
    const messageData = JSON.parse(event.data)

    if (messageData.targetTeam == team[0].english) {
      chatting.messagesLeft.push(messageData)
    } else if (messageData.targetTeam == team[1].english) {
      chatting.messagesRight.push(messageData)
    } else if (messageData.targetTeam == team[4].english) {
      chatting.messagesAll.value.push(messageData)
    } else {
      console.error(`잘못된 채팅 이벤트 수신 : ${messageData}`)
    }
  })

  // 비동기 관련 오류 처리
  sessionInfo.session.on('exception', ({ exception }) => {
    console.warn(exception)
  })

  // 토큰이 유효하면 세션 연결
  getToken().then((token) => {
    console.log(`${playerInfo.memberId},${playerInfo.nickname}`);
    sessionInfo.session
      .connect(token, { clientData: `${playerInfo.memberId},${playerInfo.nickname}` })
      .then(() => {
        // 사용자 publisher 설정 및 publish
        // sessionInfo.publisher = getDefaultPublisher()
        // sessionInfo.session.publish(sessionInfo.publisher)
      })
      .catch((error) => {
        console.log('session 연결 실패 : ', error)
      })
  })

  // 윈도우 종료 시 세션 나가기 이벤트 등록
  window.addEventListener('beforeunload', leaveSession)
}

// 팀A, 팀B 리스트 내 데이터 제거
function leaveTeam(streamManager) {
  let idx;

  // 팀 A 내 제거 시도
  idx = sessionInfo.teamLeftList.indexOf(streamManager, 0);
  if (idx >= 0) {
    // 정보 제거
    sessionInfo.teamLeftList.splice(idx, 1)
    playerInfo.playerLeftList.splice(idx, 1)
    playerInfo.order = getOrder();
    return
  }

  // 팀 B 내 제거 시도
  idx = sessionInfo.teamRightList.indexOf(streamManager, 0);
  if (idx >= 0) {
    sessionInfo.teamRightList.splice(idx, 1)
    playerInfo.playerRightList.splice(idx, 1)
    playerInfo.order = getOrder();
  }
}

// 팀 변경 요청
function changeTeam(event, targetTeam) {
  // 새로고침 방지
  event.preventDefault()

  // 자리가 없다면 작업 취소
  if ((targetTeam == team[0].english && (sessionInfo.teamLeftList.length == roomInfo.playerNum / 2))
    || (targetTeam == team[1].english && (sessionInfo.teamRightList.length == roomInfo.playerNum / 2))) {
    console.error(`${targetTeam}에 더 이상 남은 자리가 없습니다!`);
    return;
  }

  // stream 정보가 남아있으면 제거
  if (sessionInfo.publisher) {
    leaveTeam(sessionInfo.publisher)
    sessionInfo.session.unpublish(sessionInfo.publisher)
    sessionInfo.publisher = undefined
  }

  // targetTeam으로 소속 변경
  const beforeTeam = playerInfo.team;
  playerInfo.team = targetTeam;
  initCommunication(playerInfo.team)

  // A, B팀 이동
  switch (targetTeam) {
    case team[0].english:
      // 새로운 stream 정보 생성 및 publish
      sessionInfo.publisher = getDefaultPublisher(team[0].english)
      sessionInfo.teamLeftList.push(sessionInfo.publisher)
      playerInfo.playerLeftList.push({
        memberId: playerInfo.memberId,
        nickname: playerInfo.nickname,
      })
      sessionInfo.addTo = team[0].english
      sessionInfo.session.publish()
      break;
    case team[1].english:
      // 새로운 stream 정보 생성 및 publish
      sessionInfo.publisher = getDefaultPublisher(team[1].english)
      sessionInfo.teamRightList.push(sessionInfo.publisher)
      playerInfo.playerRightList.push({
        memberId: playerInfo.memberId,
        nickname: playerInfo.nickname,
      })
      sessionInfo.addTo = team[1].english
      sessionInfo.session.publish()
      break;
  }
}

// 소속 팀에 따라 화상 기기 설정 초기화
function initCommunication(targetTeam) {
  if (targetTeam == team[0].english || targetTeam == team[1].english) {
    communication.isMicOn = false;
    communication.isCameraOn = false;
    communication.isShareOn = false;
    communication.isMicHandleAvailable = true;
    communication.isCameraHandleAvailable = true;
    communication.isShareHandleAvailable = false;
  } else {
    communication.isMicOn = false;
    communication.isCameraOn = false;
    communication.isShareOn = false;
    communication.isMicHandleAvailable = false;
    communication.isCameraHandleAvailable = false;
    communication.isShareHandleAvailable = false;
  }
}

// 비디오 기본 설정 반환
function getDefaultPublisher() {
  return sessionInfo.OV.initPublisher(undefined, {
    audioSource: undefined, // 오디오 => undefined면 microphone, false/null이면 사용 X
    videoSource: undefined, // 비디오 => 기본 webcam, false/null이면 사용 X
    publishAudio: true, // 시작 오디오 상태(ON/OFF)
    publishVideo: true, // 시작 비디오 상태(ON/OFF)
    resolution: '311x170', // 카메라 해상도
    frameRate: 30, // 카메라 프레임
    insertMode: 'APPEND', // 비디오가 추가되는 방식
    mirror: false // 비디오 거울 모드 X
  });
}

// 세션 나가기 메서드
function leaveSession() {
  // 세션 연결 종료
  if (sessionInfo.session) sessionInfo.session.disconnect()

  // Empty all properties...
  sessionInfo.OV = undefined
  sessionInfo.session = undefined
  sessionInfo.publisher = undefined
  sessionInfo.teamLeftList = []
  sessionInfo.teamRightList = []
  playerInfo.playerLeftList = []
  playerInfo.playerRightList = []

  // 윈도우 종료 시 세션 나가기 이벤트 삭제
  window.removeEventListener('beforeunload', leaveSession)
}

// API 호출 메서드를 이용하여 토큰 반환
async function getToken(category, team) {
  // API 호출 필요
  await createSession(`${debateId}_${category}_${team}`)
  const token = await createToken(`${debateId}_${category}_${team}`)
  return token
}

// === 채팅방 메서드 ===
// 채팅 보내기 메서드
function sendMessage(event, inputMessage) {
  event.preventDefault()
  if (inputMessage.trim()) {
    // 다른 참가원에게 메시지 전송하기
    sessionInfo.session.value.signal({
      // 메시지 데이터를 문자열로 변환해서 전송
      data: JSON.stringify({
        nickname: playerInfo.nickname,
        targetTeam: chatting.targetTeam,
        message: inputMessage
      }),
      type: 'chat' // 신호 타입을 'chat'으로 설정
    })
  }
}

// === 미디어 관련 메서드 ===
// 카메라, 오디오를 가져오는 메서드
async function getMedia() {
  try {
    // 현 기기에서 가용 가능한 카메라, 오디오 정보를 불러옴
    // deviceId, label
    const devices = await navigator.mediaDevices.enumerateDevices()
    communication.micList = devices.filter((device) => device.kind === 'videoinput')
    communication.cameraList = devices.filter((device) => device.kind === 'audioinput')
  } catch (error) {
    console.error('미디어 정보 불러오기 실패 : ', error)
  }
}

// 시스템이 사용자의 마이크 설정 제어
function handleMicBySystem(micStatus) {
  // 미디어 정보가 없으면 무시
  if (!sessionInfo.publisher) return

  // true면 마이크 제어권 부여
  // false면 마이크 제어권 박탈, 마이크 강제 OFF
  if (micStatus) {
    communication.isMicHandleAvailable = true
  } else {
    communication.isMicHandleAvailable = false
    communication.isMicOn = false
    sessionInfo.publisher.publishVideo = false
  }
}

// 시스템이 사용자의 카메라 설정 제어
function handleCameraBySystem(cameraStatus) {
  // 세션 미참가자이면 무시
  if (!sessionInfo.publisher) return

  // true면 마이크 제어권 부여
  // false면 마이크 제어권 박탈, 마이크 강제 OFF
  if (cameraStatus) {
    communication.isCameraHandleAvailable = true
  } else {
    communication.isCameraHandleAvailable = false
    communication.isCameraOn = false
    sessionInfo.publisher.publishAudio = false
  }
}

// 사용자가 카메라 제어
function handleCameraByUser() {
  // 세션에 참가하지 않았으면 무시
  if (!sessionInfo.publisher) return

  // 비디오 상태 변경
  communication.isCameraOn = !communication.isCameraOn
  sessionInfo.publisher.publishVideo(communication.isCameraOn)

  console.log('handleCameraByUser')
}

// 사용자가 마이크 제어
function handleMicByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!sessionInfo.publisher) return

  // 마이크 상태 변경
  communication.isMicOn = !communication.isMicOn
  sessionInfo.publisher.publishAudio(communication.isMicOn)

  console.log(`handleMicByUser : ${communication.isMicOn}`)
}

function handleShareByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!sessionInfo.publisher) return

  console.log('handleShareByUser')
}

// 비디오 리스트에서 사용할 기기 선택 시 이벤트
async function handleVideoChange(event) {
  communication.selectedCamera = event.target.value
  await replaceVideoTrack(communication.selectedCamera)
}

// 마이크 리스트에서 사용할 기기 선택 시 이벤트
async function handleAudioChange(event) {
  communication.selectedMic = event.target.value
  await replaceAudioTrack(communication.selectedMic)
}

// 비디오 변경
async function replaceVideoTrack(deviceId) {
  // 세션에 참가하지 않은 경우 넘어감
  if (!sessionInfo.publisher) return

  const newConstraints = {
    audio: false,
    video: {
      deviceId: { exact: deviceId }
    }
  }

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints)
    const newVideoTrack = newStream.getVideoTracks()[0]
    await sessionInfo.publisher.replaceTrack(newVideoTrack)
  } catch (error) {
    console.error('비디오 기기 변경 에러 : ', error)
  }
}

// 오디오 변경
async function replaceAudioTrack(deviceId) {
  // 세션에 참가하지 않은 경우 넘어감
  if (!sessionInfo.publisher) return

  const newConstraints = {
    video: false,
    audio: {
      deviceId: { exact: deviceId }
    }
  }

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints)
    const newAudioTrack = newStream.getAudioTracks()[0]
    await sessionInfo.publisher.replaceTrack(newAudioTrack)
  } catch (error) {
    console.error('오디오 기기 변경 에러 : ', error)
  }
}

joinSession();

</script>

<template>
  <div class="app-container">
    <header>
      <DebateHeaderBar :headerBarTitle="headerBarTitle" />
    </header>

    <div class="main-container">
      <div class="teamA-container">
        <div class="team-title">TeamA</div>
        <div class="players">
          <div class="player">+</div>
          <div class="player">+</div>
          <div class="player">+</div>
        </div>
      </div>
      <div class="share-container"></div>
      <div class="teamB-container">
        <div class="team-title">TeamB</div>
        <div class="players">
          <div class="player">+</div>
          <div class="player">+</div>
          <div class="player">+</div>
        </div>
      </div>
      <div class="chatting-container">
        <div class="chatting-tabs">
          <div class="chatting-team-tab"> 팀 채팅 </div>
          <div class="chatting-all-tab"> 전체 채팅 </div>
        </div>
        <div class="chattings"></div>
        <div class="send-message">
          <input type="text" placeholder="메시지 보내기" class="styled-input"/>
          <img src="@/assets/img/send.png"/>
        </div>
      </div>
    </div>

    <!--
  // session이 false일때! 즉, 방에 들어가지 않았을때
  <div id="join" v-if="!openVidu.session.value">
    <div id="join-dialog">
      <div>
        <p>
          <label>세션 ID</label>
          <input v-model="openVidu.sessionId.value" type="text" required />
        </p>
        <p>
          <label>유저 ID</label>
          <input v-model="userInfo.userId.value" type="text" required />
        </p>
        <p>
          <button @click="joinSession">Join!</button>
        </p>
      </div>
    </div>
  </div>

  <div id="session" v-if="openVidu.session.value">
    <div id="session-header">
      // 세션 나가기 여부
      <input
        type="button"
        id="buttonLeaveSession"
        @click="leaveSession"
        value="Leave session"
      />
    </div>
    <DebateVideos 
      :subscribers-left="openVidu.subscribersLeftComputed"
      :subscribers-right="openVidu.subscribersRightComputed" />

    // 방에 들어갔을 때 같이 보이게 될 채팅창
    <ChattingBar 
      :nickname="userInfo.nickname.value" :role="userInfo.team.value"
      :messages-left="chatting.messagesLeft.value" :messages-right="chatting.messagesRight.value" 
      :messages-all="chatting.messagesAll.value"
       />
  </div>
  -->

    <footer>
      <DebateBottomBar
        :team="userInfo.team.value"
        :is-mic-handle-available="communication.isMicHandleAvailable.value"
        :is-camera-handle-available="communication.isCameraHandleAvailable.value"
        :is-share-handle-available="communication.isShareHandleAvailable.value"
        :is-mic-on="communication.isMicOn.value"
        :is-camera-on="communication.isCameraOn.value"
        :is-share-on="communication.isShareOn.value"
        :handle-mic-by-user="handleMicByUser"
        @handleCameraByUser="handleCameraByUser"
        @handleMicByUser="handleMicByUser"
        @handleShareByUser="handleShareByUser"
      />
    </footer>
  </div>
</template>

<style scoped>
footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
}
</style>
