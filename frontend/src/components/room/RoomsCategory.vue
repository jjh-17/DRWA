<template>
  <div class="roomList-container">
    <h2>Rooms in Category: {{ categoryName }}</h2>
    <ul>
      <li v-for="room in rooms.slice(0, displayCount)" :key="room.name">
        <RoomCard :room=room />
      </li>
    </ul>
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
const categoryhostId = computed(() => props.activeCategory)
// const rooms = ref([]);
watch(
  () => props.activeCategory,
  (newCategory) => {
    console.log('New active category:', newCategory) 
    // rooms = roomStore.fetchRoomsCategory(newCategory)
  },
  { immediate: true }
)






</script>

<style>
.roomList-container {
  background-color: #e8ebf9;
  width: 100%;
  height: 500px;
  position: relative;
  /* 상대 위치 설정 */
}
</style>
