<template>
  <header>
    <HeaderComponent />
  </header>
  <div class="option">
    <div class="title">방 검색결과</div>
    <div class="text">  {{ route.query.type }} '{{searchQuery.value}}' 에 대한 방 {{rooms.length}}개의 결과입니다</div> 
  </div>
  <div class="roomList-container">
    <template v-if="rooms.length > 0">
      <ul>
      <li v-for="room in rooms" :key="room.id" class="q-pa-md">
        <RoomCard :room=room />
      </li>
    </ul>
    </template>
    <template v-else>
      <div class="q-pa-md">
        <p>검색 결과가 없습니다.</p>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoomStore } from '@/stores/useRoomStore'; // import the room store
import { useRoute } from 'vue-router';
import RoomCard from '../components/room/RoomCard.vue'
import HeaderComponent from '../components/common/HeaderComponent.vue'

const route = useRoute();
const searchQuery = ref(route.query.query || '');


const { searchRooms } = useRoomStore();

const rooms = ref([]);

async function fetchData() {
  try {
    await searchRooms(searchQuery.value, route.query.type);
    if (rooms.value.length === 0) {
      console.log("dhkd");
      rooms.value = [
      {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다1',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신1',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다2',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신2',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다3',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신3',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다4',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신4',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다5',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신5',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다6',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신6',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다7',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신7',
    totalNum: 8
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다8',
    totalNum: 10
  },
  {
    thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신8',
    totalNum: 8
  }
      ];
    }
  } catch (error) {
    console.error('Error searching rooms:', error);
  }
}

onMounted(fetchData);
</script>
<style>
.option{
  margin-top: 40px;
  margin-left: 50px;
  margin-bottom: 30px;
}
.title{
  font-size: 30px;;
  color: #34227C;
  font-weight:bold;
}
.text{
  font-size: medium;
  color: #34227C;
}
.roomList-container {
  background-color: #e8ebf9;
  width: 100%; /* 변경된 부분: 페이지 넓이의 80% */
}

.roomList-container ul {
  display: flex;
  flex-wrap: wrap;
  list-style-type: none;
}

.roomList-container li {
  width: calc(25%);
}
</style>