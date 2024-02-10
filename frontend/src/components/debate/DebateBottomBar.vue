<template>
  <q-toolbar class="custom-toolbar">
    <q-avatar class="q-mr-lg">
      <img :src="isMicOn ? '@/assets/img/micon.png' : '@/assets/img/micoff.png'" 
            style="height: 100%" class="avatar-img" />
    </q-avatar>
    <q-avatar class="q-mr-lg">
      <img :src="isCameraOn ? '@/assets/img/cameraon.png' : '@/assets/img/cameraoff.png'" 
            style="height: 100%" class="avatar-img" />
    </q-avatar>
    <q-avatar class="q-mr-lg">
      <img src="@/assets/img/shareon.png" style="height: 100%" class="avatar-img" />
    </q-avatar>

    <!-- 가운데 정렬을 위한 공간 배분 -->
    <q-space />
    <!-- 투표 -->
    <div class="vote-container">
      <div class="label">지지율</div>
      <div class="bar-container">
        <div class="bar" :style="{ width: supportRateA + '%' }">{{ supportRateA }}%</div>
        <div class="bar" :style="{ width: supportRateB + '%' }">{{ supportRateB }}%</div>
        <div class="bar" :style="{ width: noneRate + '%' }">{{ nonRate }}%</div>
      </div>
    </div>

    <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
    <q-space />

    <q-btn class="end-speech" label="발언 종료" />
  </q-toolbar>
</template>

<script setup>
import { ref, defineProps, defineEmits } from "vue";

// 전달 받을 정보
const props = defineProps({
  session: Object,
  publisher: Object,
});

// === 기기 정보 ===
// 마이크/카메라 켜짐 여부
const isMicOn = ref(false);
const isCameraOn = ref(false);

// 마이크/카메라 킬 수 있음 여부
const isMicAvailable = ref(false);
const isCameraAvailable = ref(false);

// 현재 사용중인 마이크/카메라 기기 정보
const selectedMic = ref("");
const selectedCamera = ref("");

// 유저 정보


// === 미디어 관련 메서드 ===
// 카메라, 오디오 정보를 가져오는 메서드
async function getMedia() {
  try {
    // 현 기기에서 가용 가능한 카메라, 오디오 정보를 불러옴
    const devices = await navigator.mediaDevices.enumerateDevices();
    const mics = devices.filter((device) => device.kind === 'audioinput');
    const cameras = devices.filter((device) => device.kind === 'videoinput');

    // 오디오 선택창 생성
    const micSelections = document.querySelector('select[name="audios"]');
    if(mics){
      mics.forEach((mic) => {
        const option = document.createElement('option');
        option.value = mic.deviceId;
        option.text = mic.label;
        micSelections.appendChild(option);
      });
    } else {
      const noMic = micSelections.querySelector('option:disabled');
      noMic.innerText = '사용 가능한 마이크가 없습니다.'
    }

    // 카메라 선택창 생성
    const cameraSelections = document.querySelector('select[name="videos"]');
    if (cameras) {
      cameras.forEach((camera) => {
        const option = document.createElement('option');
        option.value = camera.deviceId;
        option.text = camera.label;
        cameraSelections.appendChild(option);
      });
    } else {
      const noCamera = cameraSelections.querySelector('option:disabled');
      noCamera.innerText = '사용 가능한 카메라가 없습니다.'
    }
  } catch (error) {
    console.error('미디어 정보 불러오기 실패 : ', error);
  }
}

// 카메라 제어 메서드
function handleCamera() {
  // 세션에 참가하지 않았거나 권한이 없는 경우
  if (!props.publisher || isCameraAvailable) return;

  // 카메라 상태 토글
  isCameraOn.value = !isCameraOn.value;

  // 비디오 상태에 따른 텍스트 변화
  const cameraState = document.getElementById('cameraState')
  if (isCameraOn.value) {
    cameraState.innerText = '비디오 '
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

<style scoped>
.custom-toolbar {
  background-color: #34227c;
  color: white;
  height: 70px;
}

.q-avatar {
  margin-left: 30px;
  margin-right: 0px;
}

.avatar-img {
  height: 100%;
  /* 이미지 높이를 부모 요소 높이에 맞춤 */
}

.end-speech {
  background-color: #e8ebf9;
  color: #34227c;
  width: 200px;
  height: 50px;
  border-radius: 4px;
  font-size: 20px;
  margin-right: 30px;
}

.vote-container {
  text-align: center;
  background-color: #34227c;
  color: white;
  padding: 10px;
  height: 100%;
  width: 50%;
}

.label {
  margin-bottom: 10px;
}

.bar-container {
  display: flex;
  background-color: white;
}

.bar {
  background-color: #6247aa;
  color: white;
  text-align: center;
  padding: 5px;
  margin-right: 2px;
  /* 바 사이의 간격 */
}
</style>
