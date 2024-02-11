<script setup>
import DebateHeaderBar from '@/components/debate/DebateHeaderBar.vue'
import DebateVideos from "@/components/debate/DebateVideos.vue";
import ChattingBar from "@/components/debate/ChattingBar.vue";
import DebateBottomBar from '@/components/debate/DebateBottomBar.vue'
import { createSession, createToken } from "@/api/debate";
import { ref, reactive, toRefs, computed, defineProps } from 'vue'
import { useDebateStore } from '@/stores/useDebateStore'
import { useAuthStore } from "@/stores/useAuthStore"
import { useRoute } from 'vue-router'
import { OpenVidu } from "openvidu-browser";
import UserVideo from "@/components/debate/UserVideo.vue"
import { team } from '@/components/common/Team.js'

// === 변수 ===
// Debate 정보
const route = useRoute()
const debateId = route.params.debateId
const debateStore = useDebateStore()
const debate = debateStore.getDebate(debateId)
const headerBarTitle = ref('[임시]제목입니다.')
// headerBarTitle = debate.getTitle();

// User 정보
const authStore = useAuthStore();

// Room 정보 => API 호출 필요
const roomInfo = {
  category: ref('category'),      // 토론 카테고리
  hostId : ref(1),                // 방장 Id
  title : ref('title'),           // 제목
  keywordLeft : ref('keywordA'),  // 제시어 A
  keywordRight : ref('keywordB'), // 제시어 B
  playerNum : ref(4),             // 플레이어 수 제한
  jurorNum: ref(10),              // 배심원 수 제한
  isPrivate : ref(false),         // 사설방 여부
  password: ref('password'),      // 비밀번호
  readyTime : ref(1),             // 준비 시간
  speakingTime : ref(5),          // 발언 시간
  qnaTime: ref(4),                // qna 시간
  thumbnailA : ref(''),           // 썸네일 A
  thumbnailB : ref(''),           // 썸네일 B
}

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
  
  publisher: ref(undefined),      // 자기 자신
  subscriber: ref(undefined),
  subscribersLeft : ref([]),      // 팀 A 리스트
  subscribersRight: ref([]),      // 팀 B 리스트
  subscribersJuror: ref([]),      // 배심원 리스트
  subscribersWatcher: ref([]),    // 관전자 리스트
  publisherComputed: computed(() => openVidu.publisher.value),
  subscribersLeftComputed: computed(() => openVidu.subscribersLeft.value),
  subscribersRightComputed : computed(() => openVidu.subscribersRight.value),
  subscribersJurorComputed : computed(() => openVidu.subscribersJuror.value),
  subscrbersWatcherComputed : computed(() => openVidu.subscribersWatcher.value), 
}     


const communication = {
  // 마이크/카메라 켜짐 여부
  isMicOn: ref(false),
  isCameraOn: ref(false),
  isShareOn: ref(false),

  // 마이크/카메라 핸들링 권한 여부
  isMicHandleAvailable: ref(true),
  isCameraHandleAvailable: ref(false),
  isShareHandleAvailable: ref(false),

  // 현재 사용중인 마이크/카메라 기기 정보
  selectedMic: ref(""),
  selectedCamera: ref(""),

  // 가용 가능한 마이크/카메라 정보
  micList: ref([]),
  cameraList: ref([]),
  micListComputed: computed(() => communication.micList.value),
  cameraListComputed: computed(() => communication.cameraList.value),
}

// 채팅방
const chatting = {
  targetTeam: ref(team[3].english),
  inputMessage: ref(''),
  messagesLeft : ref([]),
  messagesRight : ref([]),
  messagesAll : ref([]),
}

// === 세션 관련 메서드 ===
// 세션 합류 메서드
function joinSession() {
  // openvidu, 세션 객체 생성
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

  // 비동기 관련 오류 처리
  openVidu.session.value.on("exception", ({ exception }) => {
    console.warn(exception);
  });

  // 이벤트 수신 처리 => targetTeam에 따라 메시지 저장 공간 변화
  openVidu.session.value.on(`signal:chat`, (event) => {
    const messageData = JSON.parse(event.data);

    if (messageData.targetTeam == team[0].english) {
      chatting.messagesLeft.value.push(messageData)
    } else if (messageData.targetTeam == team[1].english) {
      chatting.messagesRight.value.push(messageData)
    } else if (messageData.targetTeam == team[2].english
                || messageData.targetTeam == team[3].english) {
      chatting.messagesAll.value.push(messageData)
    }
  })

  // 토큰이 유효하면 세션 연결
  getToken().then((token) => {
    openVidu.session.value
      .connect(token, { clientData: userInfo.userId.value })
      .then(() => {
        // 사용자의 openVidu 설정 생성
        let publisher_tmp = openVidu.OV.value.initPublisher(
          undefined,
          {
            audioSource: undefined, // 오디오 => 기본 microphone
            videoSource: undefined, // 비디오 => 기본 webcam
            publishAudio: false,  // 시작 오디오 상태(ON/OFF)
            publishVideo: false,  // 시작 비디오 상태(ON/OFF)
            resolution: "640x480",  // 카메라 해상도
            frameRate: 30,          // 카메라 프레임
            insertMode: "APPEND",   // 비디오가 추가되는 방식
            mirror: false,          // 비디오 거울 모드 X
          });

        // 사용자 publisher 설정 수정 및 송출 
        openVidu.publisher.value = publisher_tmp;
        openVidu.session.value.publish(publisher_tmp);

        // 미디어 정보를 불러온다.
        getMedia();
      })
      .catch((error) => {
        console.log('session 연결 실패 : ', error);
      })
  })

  // 윈도우 종료 시 세션 나가기 이벤트 등록
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
    }
  } else if (userInfo.team.value == team[1].english) {
    index = openVidu.subscribersRight.value.indexOf(subscriber, 0);
    if (index >= 0) {
      openVidu.subscribersRight.value.splice(index, 1)
    }
  } else if (userInfo.team.value == team[2].english) {
    index = openVidu.subscribersLeft.value.indexOf(subscriber, 0)
    if (index >= 0) {
      openVidu.subscribersLeft.value.splice(index, 1)
    }
  } else if (userInfo.team.value == team[3].english) {
    index = openVidu.subscribersLeft.value.indexOf(subscriber, 0)
    if (index >= 0) {
      openVidu.subscribersLeft.value.splice(index, 1)
    }
  }
}

// 세션 나가기 메서드
function leaveSession() {
  // 세션 연결 종료
  if (openVidu.session.value) openVidu.session.value.disconnect()

  // Empty all properties...
  console.log(`publisher : ${openVidu.publisher.value}`)
  openVidu.session.value = undefined;
  openVidu.publisher.value = undefined;
  openVidu.subscribersLeft.value = [];
  openVidu.subscribersRight.value = [];
  openVidu.subscribersJuror.value = [];
  openVidu.subscribersWatcher.value = [];
  openVidu.OV.value = undefined;

  // 윈도우 종료 시 세션 나가기 이벤트 삭제
  window.removeEventListener("beforeunload", leaveSession)
}

// API 호출 메서드를 이용하여 토큰 반환
async function getToken() {
  // API 호출 필요
  await createSession(openVidu.sessionId.value)
  const token = await createToken(debateId)
  return token
}

// === 채팅방 메서드 ===
// 채팅 보내기 메서드
function sendMessage(event, inputMessage) {
  event.preventDefault();
  if (inputMessage.trim()) {
    // 다른 참가원에게 메시지 전송하기
    openVidu.session.value.signal({
      // 메시지 데이터를 문자열로 변환해서 전송
      data: JSON.stringify({
        username: userInfo.nickname.value,
        targetTeam: chatting.targetTeam.value,
        message: inputMessage,
      }), 
      type: 'chat' // 신호 타입을 'chat'으로 설정
    });
  }
}

// === 미디어 관련 메서드 ===
// 카메라, 오디오를 가져오는 메서드
async function getMedia() {
  try {
    // 현 기기에서 가용 가능한 카메라, 오디오 정보를 불러옴
    // deviceId, label
    const devices = await navigator.mediaDevices.enumerateDevices();
    communication.micList.value = devices.filter((device) => device.kind === 'videoinput');
    communication.cameraList.value = devices.filter((device) => device.kind === 'audioinput');
  } catch (error) {
    console.error('미디어 정보 불러오기 실패 : ', error);
  }
}

// 시스템이 사용자의 마이크 설정 제어
function handleMicBySystem(micStatus) {
  // 세션 미참가자이면 무시
  if (!openVidu.publisher.value) return;

  // true면 마이크 제어권 부여
  // false면 마이크 제어권 박탈, 마이크 강제 OFF
  if (micStatus) {
    communication.isMicHandleAvailable = true;
  } else {
    communication.isMicHandleAvailable = false;
    openVidu.publisher.value.publishVideo = false;
  }
}

// 시스템이 사용자의 카메라 설정 제어
function handleCameraBySystem(cameraStatus) {
  // 세션 미참가자이면 무시
  if (!openVidu.publisher.value) return;

  // true면 마이크 제어권 부여
  // false면 마이크 제어권 박탈, 마이크 강제 OFF
  if (cameraStatus) {
    communication.isCameraHandleAvailable = true;
  } else {
    communication.isCameraHandleAvailable = false;
    openVidu.publisher.value.publishAudio = false;
  }
}

// 사용자가 카메라 제어
function handleCameraByUser() {
  // 세션에 참가하지 않았으면 무시
  if (!openVidu.publisher.value) return;

  // 비디오 상태 변경
  communication.isCameraOn.value = !communication.isCameraOn.value;
  openVidu.publisher.value.publishVideo(communication.isCameraOn.value);

  console.log('handleCameraByUser')
}

// 마이크 상태 토클 이벤트
function handleMicByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!openVidu.publisher.value) return;

  // 마이크 상태 변경
  communication.isMicOn.value = !communication.isMicOn.value;
  openVidu.publisher.value.publishAudio(communication.isAudioOn.value);

  console.log(`handleMicByUser : ${communication.isMicOn.value}`)
}

function handleShareByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!openVidu.publisher.value) return;

  console.log("handleShareByUser")
}
  
// 비디오 리스트에서 사용할 기기 선택 시 이벤트
async function handleVideoChange(event) {
  communication.selectedCamera.value = event.target.value;
  await replaceVideoTrack(communication.selectedCamera.value);
}

// 마이크 리스트에서 사용할 기기 선택 시 이벤트
async function handleAudioChange(event) {
  communication.selectedAudio.value = event.target.value;
  await replaceAudioTrack(communication.selectedAudio.value);
}

// 비디오 변경
async function replaceVideoTrack(deviceId) {
  // 세션에 참가하지 않은 경우 넘어감
  if (!openVidu.publisher.value) return;

  const newConstraints = {
    audio: false,
    video: {
      deviceId: { exact: deviceId },
    },
  };

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints);
    const newVideoTrack = newStream.getVideoTracks()[0];
    await openVidu.publisher.value.replaceTrack(newVideoTrack);
  } catch (error) {
    console.error("비디오 기기 변경 에러 : ", error);
  }
}

// 오디오 변경
async function replaceAudioTrack(deviceId) {
  // 세션에 참가하지 않은 경우 넘어감
  if (!openVidu.publisher.value) return;

  const newConstraints = {
    video: false,
    audio: {
      deviceId: { exact: deviceId },
    },
  };

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints);
    const newAudioTrack = newStream.getAudioTracks()[0];
    await openVidu.publisher.value.replaceTrack(newAudioTrack);
  } catch (error) {
    console.error("오디오 기기 변경 에러 : ", error);
  }
}
</script>

<template>
  <header>
    <DebateHeaderBar :headerBarTitle="headerBarTitle"/>
  </header>
  
  <!-- session이 false일때! 즉, 방에 들어가지 않았을때 -->
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
      <!-- 세션 나가기 여부 -->
      <input type="button" @click="leaveSession" value="Leave session"
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

    <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
    <!-- <ChattingBar 
      :nickname="userInfo.nickname.value" :role="userInfo.team.value"
      :messages-left="chatting.messagesLeft.value" :messages-right="chatting.messagesRight.value" 
      :messages-all="chatting.messagesAll.value"/> -->
  </div>

  <footer>
    <DebateBottomBar
      :team="userInfo.team.value"
      :is-mic-handle-available="communication.isMicHandleAvailable.value"
      :is-camera-handle-available="communication.isCameraHandleAvailable.value"
      :is-share-handle-available="communication.isShareHandleAvailable.value"
      :is-mic-on="communication.isMicOn.value" :is-camera-on="communication.isCameraOn.value" :is-share-on="communication.isShareOn.value" 
      :handle-mic-by-user="handleMicByUser"
      @handleCameraByUser="handleCameraByUser" @handleMicByUser="handleMicByUser" @handleShareByUser="handleShareByUser"
      />
  </footer>
</template>

<style scoped>
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
