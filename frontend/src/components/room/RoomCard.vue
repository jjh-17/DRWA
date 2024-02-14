<template>
  <div class="room-card" @click="joinDebate(room.sessionId)">
    <div class="top-container"></div>
    <div class="thumbnail-part">
      <div class="image-container">
        <img :src="room.thumbnail1" alt="Image 1" />
      </div>
      <!-- <div>vs</div> -->
      <div class="image-container">
        <img :src="room.thumbnail2" alt="Image 2" />
      </div>
    </div>
    <div class="title">제목 : {{ room.title }}</div>
    <div class="keyword">제시어 : {{ room.leftKeyword }} vs {{ room.rightKeyword }}</div>
    <div class="host">방장 : {{ room.hostName }}</div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router';
import { useDebateStore } from "@/stores/useDebateStore";
import { useGameStore } from "@/stores/useGameStore";

const router = useRouter();
const debateStore = useDebateStore();
const gameStore = useGameStore();
import { httpService } from '@/api/axios'
import { useRoomInfo } from '@/stores/useRoomInfo'
const { setRoomInfo } = useRoomInfo()


const joinDebate = async (sessionId) => {
  try {
    const response = await httpService.get(`/openvidu/session/${sessionId}`)
    console.log('연결 정보 응답:', response.data)

    // connectionId를 키로, debateInfoResponse를 값으로 사용하여 스토어 업데이트
    setRoomInfo(response.data.connection.connectionId, response.data.debateInfoResponse)

    router.push(`/debate/${sessionId}`)
  } catch (error) {
    console.error('연결 정보 가져오기 에러:', error)
  }
}


const props = defineProps({
  room: Object,
})
</script>

<style scoped>
.top-container {
  height: 10px;
  background-color: #34227c;
}
.room-card {
  margin: 30px;
  display: flex;
  flex-direction: column; /* 카드 내용을 세로로 배치 */
  align-items: left;
  /* padding: 16px; */
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #34227c;
  cursor:pointer;
}

.thumbnail-part {
  background-color: #f9f9f9;
  margin-bottom: 10px;
  padding: 10px;
  display: flex; /* 이미지를 가로로 배치 */
  justify-content: center; /* 이미지를 중앙 정렬 */
  width: 100%; /* 썸네일 부분의 너비를 room-card의 너비에 맞춤 */
}
.title,
.keyword,
.host {
  color: #f9f9f9;
  text-align: left;
  font-weight: bold;
  padding-left: 16px;
  padding-bottom: 5px;
}
.image-container img {
  max-width: 100%;
  /* height: auto; */
  width:150px;
  height:100px;
  display: block; /* 이미지를 블록 요소로 만들어 컨테이너의 크기에 맞춤 */
}

.image-container {
  flex: 1; 
  max-width:100%;
}

</style>
