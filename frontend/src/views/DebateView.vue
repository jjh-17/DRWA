<script setup>
import DebateHeaderBar from '@/components/debate/DebateHeaderBar.vue'
import DebateVideos from '@/components/debate/DebateVideos.vue'
import ChattingBar from '@/components/debate/ChattingBar.vue'
import DebateBottomBar from '@/components/debate/DebateBottomBar.vue'
import { createSession, createToken } from '@/api/debate'
import { ref, reactive, toRefs, computed, defineProps } from 'vue'
import { useDebateStore } from '@/stores/useDebateStore'
import { useAuthStore } from '@/stores/useAuthStore'
import { useRoute } from 'vue-router'
import { OpenVidu } from 'openvidu-browser'
import UserVideo from '@/components/debate/UserVideo.vue'
import { team } from '@/components/common/Team.js'
import { onMounted } from "vue"
import { storeToRefs } from "pinia"

// === 변수 ===
const roomInfo = reactive({
  category: 'category', // 토론 카테고리
  hostId: 1, // 방장 Id
  title: 'title', // 제목
  keywordLeft: 'keywordA', // 제시어 A
  keywordRight: 'keywordB', // 제시어 B
  playerNum: 4, // 플레이어 수 제한
  jurorNum: 10, // 배심원 수 제한
  isPrivate: false, // 사설방 여부
  password: 'password', // 비밀번호
  readyTime: 1, // 준비 시간
  speakingTime: 5, // 발언 시간
  qnaTime: 4, // qna 시간
  thumbnailA: '', // 썸네일 A
  thumbnailB: '', // 썸네일 B
});

// Debate 정보
const route = useRoute()
const debateId = route.params.debateId
const debateStore = useDebateStore()
const debate = debateStore.getDebate(debateId)
const headerBarTitle = ref('[임시]제목입니다.')
// headerBarTitle = debate.getTitle();

// 세션 정보
const sessionInfo = reactive({
  session: ref(undefined),
  OV: ref(undefined),
  debateId: route.params.debateId,
  publisher: ref(undefined),  // 자기 자신

  // index == 각 팀에서의 순서
  subscribersLeft: ref([]),   // 팀 A 리스트(자기 자신 포함 가능)
  subscribersRight: ref([]),  // 팀 B 리스트(자기 자신 포함 가능)
});

// 참가자 정보
const authStore = useAuthStore();
const { memberId,  } = storeToRefs(authStore);
const playerInfo = reactive({
  memberId: memberId,
  nickname: '',
  team: team[3].english,
  order: -1,

  // index == 각 팀에서의 순서
  // 각 팀 플레이어의 {memberId, nickname, team } 저장
  playerLeftList: [],
  playerRightList: [],
});

<<<<<<< HEAD
// 화상 정보
const communication = reactive({
  isMicOn: false,
  isCameraOn: false,
  isShareOn: false,
  isMicHandleAvailable: false,
  isCameraHandleAvailable: false,
  isShareHandleAvailable: false,
});
=======
const userInfo = {
  memberId: ref(1),
  userId: ref(''),
  nickname: ref(''),
  team: ref(''),
}

// OPEN_VIDU 정보
const openVidu = {
  OV : ref(undefined),            // 비디오
  session: ref(undefined),        // 세션 정보
  sessionId: ref(''),
>>>>>>> 31c7e7fa2851d8347d608dbdcf17bfab5700a55f
  

// 채팅방
const chatting = reactive({
  targetTeam: team[3].english,
  messagesLeft: [],
  messagesRight: [],
  messagesAll: [],
});

// 자신의 팀 내 순서
const getOrder = computed(() => {
  if (sessionInfo.session) {
    if (playerInfo.team == team[0].english) {
      return sessionInfo.subscribersLeft.indexOf(sessionInfo.publisher)
    } else if (playerInfo.team == team[1].english) {
      return sessionInfo.subscribersRight.indexOf(sessionInfo.publisher)
    }
  }
  return -1;
})

// === 세션 관련 메서드 ===
// 게임 방에 처음 왔을 때. 즉, 배심원/관전자 팀 합류
function joinSession() {
  // openvidu, 세션 객체 생성
<<<<<<< HEAD
  sessionInfo.OV = new OpenVidu()
  sessionInfo.session = sessionInfo.OV.initSession()
=======
  openVidu.OV.value = new OpenVidu()
  openVidu.session.value = openVidu.OV.value.initSession();

  // 세션 시작 작업 => 새로운 참여자의 합류
  openVidu.session.value.on("streamCreated", ({ stream }) => {
    // 새로운 subscriber
    const subscriber = openVidu.session.value.subscribe(stream)

    // 관전자로 편입(host도 일단 관전자로?)
    openVidu.subscribersWatcher.value.push(subscriber);
    console.log(`joinSession : ${team[3].english}`);
    joinTeam(team[3].english);
    // handle
    
    // // hostId == memberId면 초기팀 A, 그 외엔 관전자
    // if (roomInfo.hostId == userInfo.memberId) {
    //   openVidu.subscribersLeft.value.push(subscriber);
    // } else {
    //   openVidu.subscribersWatcher.value.push(subscriber);
    // }
  })

  // 세션 종료 작업 => 현재 팀에서 나간다.
  openVidu.session.value.on("streamDestroyed", ({ stream }) => {
    leaveTeam(stream.streamManager);
  })
>>>>>>> 31c7e7fa2851d8347d608dbdcf17bfab5700a55f

  // 비동기 관련 오류 처리
  sessionInfo.session.on('exception', ({ exception }) => {
    console.warn(exception)
  })

  // 다른 참여자의 채팅 이벤트 수신 처리(닉네임, 목표 팀, 메시지 내용)
  sessionInfo.session.on('signal:chat', (event) => {
    const messageData = JSON.parse(event.data)

    if (messageData.targetTeam == team[3].english) {
      chatting.messagesAll.value.push(messageData)
    } else {
      console.error(`잘못된 채팅 이벤트 수신 : ${messageData}`)
    }
  })

  // 세션 ALL로의 토큰이 유효하면, 세션 연결
  // 관전자/배심원은 오디오, 캠이 필요없으므로 설정 X
  getToken(team[3].english).then((token) => {
    sessionInfo.session.value
      .connect(token, { clientData: `${playerInfo.memberId},${playerInfo.nickname},${playerInfo.team}`})
      .catch((error) => {
        console.log('session 연결 실패 : ', error)
      })
  })
}

// sessionType으로 생성
function joinSession2(sessionType) {
  // openvidu, 세션 객체 생성
  sessionInfo.OV = new OpenVidu()
  sessionInfo.session = sessionInfo.OV.initSession()

  // 다른 사용자의 stream(publisher) 생성 감지
  sessionInfo.session.on('streamCreated', ({ stream }) => {
    // 새로운 subscriber
    const subscriber = sessionInfo.session.subscribe(stream)

    // 새로운 참가자의 클라이언트 정보(memberId, nickname, team)를 받아옴
    const clientDatas = subscriber.stream.connection.data.split('"');
    const datas = clientDatas[3].split(',');
    
    // 다른 플레이어의 팀에 따라 저장 위치 변경
    if (datas[2] == team[0].english) {
      sessionInfo.subscribersLeft.push(subscriber)
      playerInfo.playerLeftList.push({
        memberId: datas[0],
        nickname: datas[1],
        team: datas[2],
      });
    } else if (datas[2] == team[1].english) {
      sessionInfo.subscribersRight.push(subscriber)
      playerInfo.playerRightList.push({
        memberId: datas[0],
        nickname: datas[1],
        team: datas[2],
      });
    } else if (datas[2] == team[2].english || datas[2] == team[3].english) {
      console.error(`${datas[2]}는 subscribers에 저장하면 안됩니다.`)
    } else {
      console.error(`joinSession Fail :  team(${playerInfo.team})`)
    }
  })

  // 다른 사용자의 stream 종료 감지
  sessionInfo.session.on('streamDestroyed', ({ stream }) => {
    leaveTeam(stream.streamManager)
  })

  // 비동기 관련 오류 처리
  sessionInfo.session.on('exception', ({ exception }) => {
    console.warn(exception)
  })

  // 채팅 이벤트 수신 처리(닉네임, 목표 팀, 메시지 내용) => targetTeam에 따라 메시지 저장 공간 변화
  sessionInfo.session.on('signal:chat', (event) => {
    const messageData = JSON.parse(event.data)

    if (messageData.targetTeam == team[0].english) {
      chatting.messagesLeft.push(messageData)
    } else if (messageData.targetTeam == team[1].english) {
      chatting.messagesRight.push(messageData)
    } else if (messageData.targetTeam == team[2].english || messageData.targetTeam == team[3].english) {
      chatting.messagesAll.value.push(messageData)
    } else {
      console.error(`잘못된 채팅 이벤트 수신 : ${messageData}`)
    }
  })

  // 팀 변경 이벤트 수신 처리(이전 팀, 목표 팀)


  // 토큰이 유효하면 세션 연결
  getToken().then((token) => {
    sessionInfo.session.value
      .connect(token, { clientData: `${playerInfo.memberId},${playerInfo.nickname},${playerInfo.team}`})
      .then(() => {
        // 사용자의 openVidu 설정 생성
        let audioSource, videoSource
        if (playerInfo.team == team[2].english || playerInfo.team == team[3].english) {
          audioSource = false
          videoSource = false
          communication.isMicOn = false
          communication.isCameraOn = false
        } else {
          audioSource = undefined
          videoSource = undefined
          communication.isMicOn = true;
          communication.isCameraOn = true;
        }
        let publisher_tmp = sessionInfo.OV.value.initPublisher(undefined, {
          audioSource: audioSource, // 오디오 => undefined면 microphone, false/null이면 사용 X
          videoSource: videoSource, // 비디오 => 기본 webcam, false/null이면 사용 X
          publishAudio: communication.isMicOn, // 시작 오디오 상태(ON/OFF)
          publishVideo: communication.isCameraOn, // 시작 비디오 상태(ON/OFF)
          resolution: '311x170', // 카메라 해상도
          frameRate: 30, // 카메라 프레임
          insertMode: 'APPEND', // 비디오가 추가되는 방식
          mirror: false // 비디오 거울 모드 X
        })

        // 사용자 publisher 설정 수정 및 publish
        sessionInfo.publisher.value = publisher_tmp
        sessionInfo.session.value.publish(publisher_tmp)

        // 미디어 정보를 불러온다.
        getMedia()
      })
      .catch((error) => {
        console.log('session 연결 실패 : ', error)
      })
  })

  // 윈도우 종료 시 세션 나가기 이벤트 등록
<<<<<<< HEAD
  window.addEventListener('beforeunload', leaveSession)
}

// 현재 팀에서 나가기
function leaveTeam(streamManager) {
  let idx;

  // 팀 A 내 제거 시도
  idx = sessionInfo.subscribersLeft.indexOf(streamManager, 0);
  if (idx >= 0) {
    sessionInfo.subscribersLeft.splice(idx, 1)
    playerInfo.playerLeftList.splice(idx, 1)
    if (playerInfo.team == team[0].english) {
      playerInfo.order = getOrder();
=======
  window.addEventListener("beforeunload", leaveSession);
}

// targetTeam으로 합류
function joinTeam(targetTeam) {
  // 기존 팀 나감
  leaveTeam(openVidu.publisher.value);

  // 현재 팀정보 수정
  userInfo.team.value = targetTeam;
  console.log(`팀 변경 : ${userInfo.team.value}`);

  // team에 따라 다른 subscrber 합류
  if (userInfo.team.value == team[0].english) {
    openVidu.subscribersLeft.value.push(openVidu.publisher.value);
    communication.isMicHandleAvailable.value = true;
    communication.isCameraHandleAvailable.value = true;
    communication.isShareHandleAvailable.value = true;
  } else if (userInfo.team.value == team[1].english) {
    openVidu.subscribersRight.value.push(openVidu.publisher.value);
    communication.isMicHandleAvailable.value = true;
    communication.isCameraHandleAvailable.value = true;
    communication.isShareHandleAvailable.value = true;
  } else if (userInfo.team.value == team[2].english) {
    openVidu.subscribersJuror.value.push(openVidu.publisher.value);
    communication.isMicHandleAvailable.value = false;
    communication.isCameraHandleAvailable.value = false;
    communication.isShareHandleAvailable.value = false;
    communication.isMicOn.value = false;
    communication.isCameraOn.value = false;
    communication.isShareOn.value = false;
  } else if (userInfo.team.value == team[3].english) {
    openVidu.subscribersWatcher.value.push(openVidu.publisher.value);
    communication.isMicHandleAvailable.value = false;
    communication.isCameraHandleAvailable.value = false;
    communication.isShareHandleAvailable.value = false;
    communication.isMicOn.value = false;
    communication.isCameraOn.value = false;
    communication.isShareOn.value = false;
  }
}

// 현재 팀에서 나가기
function leaveTeam(subscriber) {
  let index;
  if (userInfo.team.value == team[0].english) {
    index = openVidu.subscribersLeft.value.indexOf(subscriber, 0)
    if (index >= 0) {
      openVidu.subscribersLeft.value.splice(index, 1)
>>>>>>> 31c7e7fa2851d8347d608dbdcf17bfab5700a55f
    }
    return
  }

  // 팀 B 내 제거 시도
  idx = sessionInfo.subscribersRight.indexOf(streamManager, 0);
  if (idx >= 0) {
    sessionInfo.subscribersRight.splice(idx, 1)
    playerInfo.playerRightList.splice(idx, 1)
    if (playerInfo.team == team[1].english) {
      playerInfo.order = getOrder();
    }
  }
}

// 세션 나가기 메서드
function leaveSession() {
  // 세션 연결 종료
  if (sessionInfo.session.value) sessionInfo.session.value.disconnect()

  // Empty all properties...
<<<<<<< HEAD
  sessionInfo.session.value = undefined
  sessionInfo.OV.value = undefined
  sessionInfo.publisher.value = undefined
  sessionInfo.subscribersLeft.value = []
  sessionInfo.subscribersRight.value = []
  sessionInfo.subscribersAll.value = []
=======
  console.log(`publisher : ${openVidu.publisher.value}`)
  openVidu.session.value = undefined;
  openVidu.publisher.value = undefined;
  openVidu.subscribersLeft.value = [];
  openVidu.subscribersRight.value = [];
  openVidu.subscribersJuror.value = [];
  openVidu.subscribersWatcher.value = [];
  openVidu.OV.value = undefined;
>>>>>>> 31c7e7fa2851d8347d608dbdcf17bfab5700a55f

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
    openVidu.session.value.signal({
      // 메시지 데이터를 문자열로 변환해서 전송
      data: JSON.stringify({
        nickname: userInfo.nickname.value,
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
    communication.micList.value = devices.filter((device) => device.kind === 'videoinput')
    communication.cameraList.value = devices.filter((device) => device.kind === 'audioinput')
  } catch (error) {
    console.error('미디어 정보 불러오기 실패 : ', error)
  }
}

// 시스템이 사용자의 마이크 설정 제어
function handleMicBySystem(micStatus) {
  // 세션 미참가자이면 무시
  if (!openVidu.publisher.value) return

  // true면 마이크 제어권 부여
  // false면 마이크 제어권 박탈, 마이크 강제 OFF
  if (micStatus) {
    communication.isMicHandleAvailable = true
  } else {
    communication.isMicHandleAvailable = false
    openVidu.publisher.value.publishVideo = false
  }
}

// 시스템이 사용자의 카메라 설정 제어
function handleCameraBySystem(cameraStatus) {
  // 세션 미참가자이면 무시
  if (!openVidu.publisher.value) return

  // true면 마이크 제어권 부여
  // false면 마이크 제어권 박탈, 마이크 강제 OFF
  if (cameraStatus) {
    communication.isCameraHandleAvailable = true
  } else {
    communication.isCameraHandleAvailable = false
    openVidu.publisher.value.publishAudio = false
  }
}

// 사용자가 카메라 제어
function handleCameraByUser() {
  // 세션에 참가하지 않았으면 무시
  if (!openVidu.publisher.value) return

  // 비디오 상태 변경
  communication.isCameraOn.value = !communication.isCameraOn.value
  openVidu.publisher.value.publishVideo(communication.isCameraOn.value)

  console.log('handleCameraByUser')
}

// 마이크 상태 토클 이벤트
function handleMicByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!openVidu.publisher.value) return

  // 마이크 상태 변경
  communication.isMicOn.value = !communication.isMicOn.value
  openVidu.publisher.value.publishAudio(communication.isAudioOn.value)

  console.log(`handleMicByUser : ${communication.isMicOn.value}`)
}

function handleShareByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!openVidu.publisher.value) return

  console.log('handleShareByUser')
}

// 비디오 리스트에서 사용할 기기 선택 시 이벤트
async function handleVideoChange(event) {
  communication.selectedCamera.value = event.target.value
  await replaceVideoTrack(communication.selectedCamera.value)
}

// 마이크 리스트에서 사용할 기기 선택 시 이벤트
async function handleAudioChange(event) {
  communication.selectedAudio.value = event.target.value
  await replaceAudioTrack(communication.selectedAudio.value)
}

// 비디오 변경
async function replaceVideoTrack(deviceId) {
  // 세션에 참가하지 않은 경우 넘어감
  if (!openVidu.publisher.value) return

  const newConstraints = {
    audio: false,
    video: {
      deviceId: { exact: deviceId }
    }
  }

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints)
    const newVideoTrack = newStream.getVideoTracks()[0]
    await openVidu.publisher.value.replaceTrack(newVideoTrack)
  } catch (error) {
    console.error('비디오 기기 변경 에러 : ', error)
  }
}

// 오디오 변경
async function replaceAudioTrack(deviceId) {
  // 세션에 참가하지 않은 경우 넘어감
  if (!openVidu.publisher.value) return

  const newConstraints = {
    video: false,
    audio: {
      deviceId: { exact: deviceId }
    }
  }

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints)
    const newAudioTrack = newStream.getAudioTracks()[0]
    await openVidu.publisher.value.replaceTrack(newAudioTrack)
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
          <div v-for="num in roomInfo.playerNum.value/2" :key="num">
            <div class="player">+</div>
          </div>
        </div>
      </div>
      <div class="share-container"></div>
      <div class="teamB-container">
        <div class="team-title">TeamB</div>
        <div class="players">
          <div v-for="num in roomInfo.playerNum.value/2" :key="num">
            <div class="player">+</div>
          </div>
        </div>
      </div>
      <div class="chatting-container">
        <div class="chatting-tabs">
          <div class="chatting-team-tab" :aria-readonly="true"> 팀 채팅 </div>
          <div class="chatting-all-tab" :aria-readonly="true"> 전체 채팅 </div>
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
<<<<<<< HEAD
      // 세션 나가기 여부
      <input
        type="button"
        id="buttonLeaveSession"
        @click="leaveSession"
        value="Leave session"
=======
      <!-- 세션 나가기 여부 -->
      <input type="button" @click="leaveSession" value="Leave session"
>>>>>>> 31c7e7fa2851d8347d608dbdcf17bfab5700a55f
      />

      <!-- 팀 변경 -->
      <div>
        <input type="button" @click="joinTeam(team[0].english)" value="팀 A"/>
        <input type="button" @click="joinTeam(team[1].english)" value="팀 B"/>
        <input type="button" @click="joinTeam(team[2].english)" value="배심원"/>
        <input type="button" @click="joinTeam(team[3].english)" value="관전자"/>
      </div>
    </div>
    <!-- <DebateVideos 
      :subscribers-left="openVidu.subscribersLeftComputed"
      :subscribers-right="openVidu.subscribersRightComputed" /> -->

<<<<<<< HEAD
    // 방에 들어갔을 때 같이 보이게 될 채팅창
    <ChattingBar 
=======
    <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
    <!-- <ChattingBar 
>>>>>>> 31c7e7fa2851d8347d608dbdcf17bfab5700a55f
      :nickname="userInfo.nickname.value" :role="userInfo.team.value"
      :messages-left="chatting.messagesLeft.value" :messages-right="chatting.messagesRight.value" 
      :messages-all="chatting.messagesAll.value"/> -->
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
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh; /* 전체 화면 높이를 차지하도록 설정 */
  margin: 0; /* 기본 마진 제거 */
}

.main-container {
  flex-grow: 1; /* header와 footer를 제외한 모든 공간을 차지 */
  display: flex; /* Flexbox 레이아웃 사용 */
  margin-bottom:70px;
}

.teamA-container,
.teamB-container,
.share-container,
.chatting-container {
  height: 100%;
}
.share-container {
  flex: 4.5;
}
.chatting-container {
  display:flex;
  flex: 1.5;
  border-left: 1px solid #ccc;
  box-shadow: -4px 0 5px -2px rgba(0, 0, 0, 0.2); /* 왼쪽 그림자 설정 */
  flex-direction:column;
}
.chatting-tabs {
  height:10%;
  display:flex;
  justify-content: space-around;
  padding:10px;
}
.chatting-team-tab,
.chatting-all-tab {
  display:flex;
  align-items:center;
  justify-content: center;
  font-size: 1rem;
  border-radius: 4px;
  background-color: #e8ebf9;
  color: #34227c;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 42%;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}
.chattings {
  flex:8;
}
.send-message {
  flex:1;
  height:10%;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 5px;
}
.send-message img {
  height:50%;
  object-fit: contain;
}

.styled-input {
  font-size: 16px;
  padding: 10px 20px;
  border: 2px solid #34227C; /* Adjust the color to match the image */
  border-radius: 25px; /* This gives the rounded corners */
  outline: none; /* Removes the default focus outline */
  width: 230px; /* Adjust the width as needed */
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); /* Adds a subtle shadow */
}

.teamA-container,
.teamB-container {
  flex: 1;
  display:flex;
  flex-direction:column;
}
.team-title {
  height:10%;
  text-align:center;
  line-height:56.31px;
  background-color:#E8EBF9;
  color:#34227C;
  font-size:20px;
  font-weight:bold;
}
.players{
  height:90%;
  display:flex;
  flex-direction:column;
}
.player {
  flex:1; 
  border: 1px solid #34227C;
  background-color:#D9D9D9;
  display:flex;
  align-items:center;
  justify-content:center;
  color:#34227C;
  font-size:30px;
  cursor:pointer;
}
footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
}
.form-group {
  margin-bottom: 1rem;
}
</style>
