<script setup>
import DebateHeaderBar from '@/components/debate/DebateHeaderBar.vue'
import DebateBottomBar from '@/components/debate/DebateBottomBar.vue'

import { ref, reactive, toRefs, computed, defineProps } from 'vue'
import { useDebateStore } from '@/stores/useDebateStore'
import { useAuthStore } from "@/stores/useAuthStore"
import { useRoute } from 'vue-router'
import { OpenVidu } from "openvidu-browser";
import UserVideo from "@/components/debate/UserVideo.vue"

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
const userId = ref('');
const nickname = ref('');

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

// OPEN_VIDU 정보
const OV = ref(undefined);            // 비디오
const session = ref(undefined);       // 세션 정보
const token = ref('')
const openVidu = {
  // mainStreamManager : ref(undefined), // 호스트 카메라
  publisher : ref(undefined),         // 자기 자신
  subscribersLeft : ref([]),          // 팀 A 카메라
  subscribersRight : ref([]),         // 팀 B 카메라
}
// const mainStreamManagerComputed = computed(() => openVidu.mainStreamManager.value);  // 메인 비디오 Computed
const publisherComputed = computed(() => openVidu.publisher.value);                  
const subscribersLeftComputed = computed(() => openVidu.subscribersLeft.value);
const subscribersRightComputed = computed(() => openVidu.subscribersRight.value);

// 카메라, 오디오 정보
const communication = {
  isAudioOn : ref(false),       // 기본은 음소거
  isVideoOn : ref(true),        // 기본은 카메라 ON
  selectedAudio : ref(""),      // 오디오 변경시 사용할 변수
  selectedVideo : ref(""),      // 카메라 변경시 사용할 변수 
}

// 채팅창 정보
const inputMessage = ref("")
const targetTeam = ref("")
const chatting = {
  messagesLeft : ref([]),
  messagesRight : ref([]),
  messagesAll : ref([]),
}

// === 세션 관련 메서드 ===
// 세션 합류 메서드
function joinSession(team) {
  // openvidu, 세션 객체 생성
  OV.value = new OpenVidu()
  session.value = OV.value.initSession();

  // 세션 시작 작업
  session.value.on("streamCreated", ({ stream }) => {
    // 새로운 subscriber
    const subscriber = session.value.subscribe(stream)

    // team에 따라 다른 subscrber 합류
    if (team == "teamLeft") {
      openVidu.subscribersLeft.value.push(subscriber) 
    } else if(team == "teamRight") {
      openVidu.subscribersRight.value.push(subscriber)
    } else {
      console.log('세션 들어가기 실패');
    }
  })

  // 세션 종료 작업
  session.value.on("streamDestroyed", ({ stream }) => {
    let index;
    if (team == "teamLeft") {
      index = openVidu.subscribersLeft.value.indexOf(stream.streamManager, 0)
      if (index >= 0) {
        openVidu.subscribersLeft.value.splice(index, 1)
      }
    } else if (team == "teamRight") {
      index = openVidu.subscribersRight.value.indexOf(stream.streamManager, 0);
      if (index >= 0) {
        openVidu.subscribersRight.value.splice(index, 1)
      }
    }
  })

  // 비동기 관련 오류 처리
  session.value.on("exception", ({ exception }) => {
    console.warn(exception);
  });

  // 메시지 수신 처리
  session.value.on(`signal:chat`, (event) => {
    const messageData = JSON.parse(event.data);
    if (targetTeam.value == "teamLeft") {
      chatting.messagesLeft.value.push(messageData)
    } else if (targetTeam.value == "teamRight") {
      chatting.messagesRight.value.push(messageData)
    } else if (targetTeam.value == "teamAll") {
      chatting.messagesAll.value.push(messageData)
    }
  })

  // 5) 토큰이 유효하면 세션 연결
  getToken().then((token) => {
    session.value
      .connect(token1, { clientData: userId.value })
      .then(() => {
        // 사용자의 openVidu 설정 생성
        let publisher_tmp = OV.value.initPublisher(
          undefined,
          {
            audioSource: undefined, // 오디오 => 기본 microphone
            videoSource: undefined, // 비디오 => 기본 webcam
            publishAudio: communication.isAudioOn.value,  // 시작 오디오 상태(ON/OFF)
            publishVideo: communication.isVideoOn.value,  // 시작 비디오 상태(ON/OFF)
            resolution: "640x480",  // 카메라 해상도
            frameRate: 30,          // 카메라 프레임
            insertMode: "APPEND",   // 비디오가 추가되는 방식
            mirror: false,          // 비디오 거울 모드 X
          });

        // 사용자 publisher 설정 수정 및 송출 
        openVidu.publisher.value = publisher_tmp;
        session.value.publish(publisher_tmp);

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

// 세션 나가기 메서드
function leaveSession() {
  // --- 7) Leave the session by calling 'disconnect' method over the Session object ---
  if (session.value) session.value.disconnect()

  // Empty all properties...
  session.value = undefined;
  openVidu.publisher = undefined;
  openVidu.subscribersLeft = undefined;
  openVidu.subscribersRight = undefined;
  OV.value = undefined;

  // 윈도우 종료 시 세션 나가기 이벤트 삭제
  window.removeEventListener("beforeunload", leaveSession)
}

// API 호출 메서드를 이용하여 토큰 반환
async function getToken() {
  // API 호출 필요
  await createSession(roomInfo.category.value)
  const token = await createToken(debateId)
  return token
}

// 세션 생성 API 호출
async function createSession(category) {
  const response = await category;
}

// 토큰 생성 API 호출
async function createToken(debateId) {
  const response = await token.value
  return response
}

// 채팅 보내기 메서드
function sendMessage(event) {
  event.preventDefault();
  if (inputMessage.value.trim()) {
    // messages.value.push({username : myUserName.value, message : inputMessage.value})
    // 다른 참가원에게 메시지 전송하기
    session.value.signal({
      data: JSON.stringify({ username: nickname.value, message: inputMessage.value }), // 메시지 데이터를 문자열로 변환해서 전송
      type: 'chat' // 신호 타입을 'chat'으로 설정
    });
    inputMessage.value = '';
  }
}

// === 미디어 관련 메서드 ===
// 카메라, 오디오를 가져오는 메서드
async function getMedia() {
  try {
    // 현 기기에서 가용 가능한 카메라, 오디오 정보를 불러옴
    const devices = await navigator.mediaDevices.enumerateDevices();
    const videos = devices.filter((device) => device.kind === 'videoinput');
    const audios = devices.filter((device) => device.kind === 'audioinput');

    // 카메라 선택창 생성
    const videoSelections = document.querySelector('select[name="videos"]');
    if (videos) {
      videos.forEach((video) => {
        const option = document.createElement('option');
        option.value = video.deviceId;
        option.text = video.label;
        videoSelections.appendChild(option);
      });
    } else {
      const notVideo = videoSelections.querySelector('option:disabled');
      notVideo.innerText = '사용 가능한 비디오가 없습니다.'
    }

    // 오디오 선택창 생성
    const audioSelections = document.querySelector('select[name="audios"]');
    if(audios){
      audios.forEach((audio) => {
        const option = document.createElement('option');
        option.value = audio.deviceId;
        option.text = audio.label;
        audioSelections.appendChild(option);
      });
    } else {
      const notAudio = audioSelections.querySelector('option:disabled');
      notAudio.innerText = '사용 가능한 마이크가 없습니다.'
    }
  } catch (error) {
    console.error('미디어 정보 불러오기 실패 : ', error);
  }
}

// 비디오 상태 토글 이벤트
function handleVideoBtn() {
  // 세션에 참가하지 않은 경우 무시
  if (!openVidu.publisher.value) return;

  // 비디오 상태 토글
  communication.isVideoOn.value = !communication.isVideoOn.value;

  // 비디오 상태에 따른 텍스트 변화
  const videoActivate = document.getElementById('video-activate')
  if (communication.isVideoOn.value) {   
    videoActivate.innerText = '비디오 ON'
  } else {
    videoActivate.innerText = '비디오 OFF'
  }

  // 비디오 송출 상태 적용
  openVidu.publisher.value.publishVideo(communication.isVideoOn.value);
}

// 마이크 상태 토클 이벤트
function handleAudioBtn() {
  // 세션에 참가하지 않은 경우 무시
  if (!openVidu.publisher.value) return;

  // 음소거 상태 토글
  communication.isAudioOn.value = !communication.isAudioOn.value;
  const audioActivate = document.getElementById('audio-activate')
  if (communication.isAudioOn.value) {
    audioActivate.innerText = '마이크 ON'
  } else {
    audioActivate.innerText = '마이크 OFF'
  }

  // 음소거 설정을 적용
  openVidu.publisher.value.publishAudio(communication.isAudioOn.value);
}
  
// 비디오 리스트에서 사용할 기기 선택 시 이벤트
async function handleVideoChange(event) {
  communication.selectedVideo.value = event.target.value;
  await replaceVideoTrack(communication.selectedVideo.value);
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
  <div id="join" v-if="!session">
    <div id="join-dialog">
      <div>
        <p>
          <label>토큰</label>
          <input v-model="token" type="text" required />
        </p>
        <p>
          <label>유저 ID</label>
          <input v-model="userId" type="text" required />
        </p>
        <p>
          <button @click="joinSession">Join!</button>
        </p>
      </div>
    </div>
  </div>

  <div id="session" v-if="session">
    <div id="session-header">
      <!-- 캠활성화, 음소거 버튼 -->
      <button id="video-activate" @click="handleVideoBtn">캠 비활성화</button>
      <button id="audio-activate" @click="handleAudioBtn">음소거 활성화</button>
      
      <!-- 캠,오디오 선택 옵션 -->
      <div>
        <select name="videos" @change="handleVideoChange">
          <option disabled>사용할 카메라를 선택하세요</option>
        </select>
        <select name="audios" @change="handleAudioChange">
          <option disabled>사용할 마이크를 선택하세요</option>
        </select>
      </div>
      
      <!-- 세션 나가기 여부 -->
      <input
        type="button"
        id="buttonLeaveSession"
        @click="leaveSession"
        value="Leave session"
      />
    </div>
    
    <!-- A팀 캠 -->
    <div id="teamA-container">
      <h3>A팀 캠</h3>
      <UserVideo
        v-for="sub in subscribersLeftComputed"
        :key="sub.stream.connection.connectionId"
        :stream-manager="sub"
      />
    </div>

    <!-- B팀 캠 -->
    <div id="teamA-container">
      <h3>B팀 캠</h3>
      <UserVideo
        v-for="sub in subscribersRightComputed"
        :key="sub.stream.connection.connectionId"
        :stream-manager="sub"
      />
    </div>

    <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
    <div id="chat-container">
      <div id="chat-window">
        <ul id="chat-history">
          <li v-for="(message, index) in messages" :key="index">
            <strong>{{message.username}}:</strong> {{message.message}}
          </li>
        </ul>
      </div>
      <form id="chat-write">
        <input type="text" placeholder="전달할 내용을 입력하세요." v-model="inputMessage">
        <button @click="sendMessage">전송</button>
      </form>
    </div>
  </div>

  <footer>
    <DebateBottomBar />
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
