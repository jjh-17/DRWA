<template>
  <div class="room-card">
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
      <div class="host">방장 : {{ room.hostId }}</div>
    </div>
  </template>
  
  <script setup>
  import { defineProps } from 'vue'

  const props = defineProps({
    room: Object,
  })
  
  </script>
  
  <style scoped>
  .top-container {
    height:10px;
    background-color:#34227C;
  }
  .room-card {
    margin: 30px;
    display: flex;
    flex-direction: column; /* 카드 내용을 세로로 배치 */
    align-items: left;
    /* padding: 16px; */
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #34227C;
  }
  
  .thumbnail-part {
    background-color:#f9f9f9;
    margin-bottom:10px;
    padding:10px;
    display: flex; /* 이미지를 가로로 배치 */
    justify-content: center; /* 이미지를 중앙 정렬 */
    width: 100%; /* 썸네일 부분의 너비를 room-card의 너비에 맞춤 */
  }
  .title,.keyword,.host {
    color:#f9f9f9;
    text-align:left;
    font-weight: bold;
    padding-left:16px;
    padding-bottom:5px;
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
  