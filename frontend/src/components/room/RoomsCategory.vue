<template>
  <div class="roomList-container">
    <ul>
      <li v-for="room in rooms.slice(0, displayCount)" :key="room.name">
        <!-- <RoomCard :img1="room.img1" :img2="room.img2" /> -->
        <RoomCard :room=room />
      </li>
    </ul>
    <div class="see-more" v-if="isMoreAvailable" @click="loadMoreRooms">
      더보기
    </div>
  </div>
</template>
 
<script setup>
import { computed, watch, ref } from 'vue'
import { useRoomStore } from '@/stores/useRoomStore'
import { defineProps } from 'vue'
import RoomCard from '@/components/room/RoomCard.vue'

// [임시]더미데이터
const rooms = ref([
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다1',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신1',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다2',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신2',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다3',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신3',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다4',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신4',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다5',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신5',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다6',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신6',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다7',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신7',
    totalNum: 8
  },
  {
    img1: 'https://cdn.quasar.dev/img/mountains.jpg',
    img2: 'https://cdn.quasar.dev/img/parallax1.jpg',
    keyword1: '산',
    keyword2: '바다',
    title: '어디서 살고 싶나요?',
    name: '나는자연인이다8',
    totalNum: 10
  },
  {
    img1: 'https://cdn.quasar.dev/img/parallax2.jpg',
    img2: 'https://cdn.quasar.dev/img/mountains.jpg',
    keyword1: '치킨',
    keyword2: '피자',
    title: '당신의 선호 음식은?',
    name: '장사의신8',
    totalNum: 8
  }
])

const props = defineProps({
  activeCategory: String
})

const roomStore = useRoomStore()
const categoryName = computed(() => props.activeCategory)

watch(
  () => props.activeCategory,
  (newCategory) => {
    console.log('New active category:', newCategory) // 로깅
    roomStore.fetchRoomsCategory(newCategory)
  },
  { immediate: true }
)

const displayCount = ref(8);

const loadMoreRooms = () => {
  if (displayCount.value + 8 <= rooms.value.length) {
    displayCount.value += 8;
  } else {
    displayCount.value = rooms.value.length; // 배열의 끝에 도달했을 경우
  }
};


const isMoreAvailable = computed(() => {
  return displayCount.value < rooms.value.length;
});

</script>

<style>
.roomList-container {
  background-color: #e8ebf9;
  width: 100%;
  height: 100%;
}
.roomList-container ul {
  display: flex;
  flex-wrap: wrap;
  list-style-type: none; /* 기본 목록 스타일 제거 */
  padding: 0; /* 기본 패딩 제거 */
}

.roomList-container li {
  width: calc(25%); /* 한 줄에 4개씩 배치 (20px는 마진을 고려한 값) */
}
.see-more {
  width: 100%;
  background-color: 
}
</style>
