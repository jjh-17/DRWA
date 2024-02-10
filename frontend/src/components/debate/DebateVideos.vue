<script setup>
import { createSession, createToken } from "@/api/debate";
import { ref, defineProps } from "vue";
import { useDebateStore } from "@/stores/useDebateStore";
import { useAuthStore } from "@/stores/useAuthStore";
import { useRoute } from "vue-router";
import { OpenVidu } from "openvidu-browser";
import UserVideo from "./UserVideo.vue";

// === 변수 ===
// Debate 정보
const route = useRoute()
const debateId = route.params.debateId
const debateStore = useDebateStore()

// User 정보
const authStore = useAuthStore();
const userId = ref('');

// RoomInfo => API 호출 필요
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

// === 메서드 정의 ===
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

  // 이벤트 수신 처리
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
</script>

<template>
  <div>
    
  </div>
</template>

<style scoped>

</style>