<template>
    <div class="room-card">
      <div class="thumbnail-part">
        <div class="image-container">
          <img :src="room.img1" alt="Image 1" />
        </div>
        <!-- <div>vs</div> -->
        <div class="image-container">
          <img :src="room.img2" alt="Image 2" />
        </div>
      </div>
      <div>제목 : {{ room.title }}</div>
      <div>제시어 : {{ room.keyword1 }} vs {{ room.keyword2 }}</div>
      <div>방장 : {{ room.name }}</div>
    </div>
  </template>
  
  <script setup>
  import { defineProps } from 'vue'

  const props = defineProps({
    room: Object,
  })
  
  </script>
  
  <style scoped>
  .room-card {
    margin: 30px;
    display: flex;
    flex-direction: column; /* 카드 내용을 세로로 배치 */
    align-items: center;
    padding: 16px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
  }
  
  .thumbnail-part {
    display: flex; /* 이미지를 가로로 배치 */
    justify-content: center; /* 이미지를 중앙 정렬 */
    width: 100%; /* 썸네일 부분의 너비를 room-card의 너비에 맞춤 */
  }
  
  .image-container img {
    max-width: 100%;
    height: auto;
    display: block; /* 이미지를 블록 요소로 만들어 컨테이너의 크기에 맞춤 */
  }
  
  .image-container {
    flex: 1; /* 이미지 컨테이너들이 공간을 균등하게 차지하도록 함 */
    padding: 0 0px; /* 이미지 사이에 간격 추가 */
  }
  </style>
  