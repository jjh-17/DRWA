<template>
  <div class="roomList-container">
    <ul>
      <li v-for="room in rooms.slice(0, displayCount)" :key="room.hostId">
        <RoomCard :room=room />
      </li>
    </ul>
    <div class="see-more" v-if="isMoreAvailable" @click="loadMoreRooms">
      &#11167; 더보기 &#11167;
    </div>
  </div>
</template>
 
<script setup>
import { computed, watch, ref } from 'vue'
import { useRoomStore } from '@/stores/useRoomStore'
import { defineProps } from 'vue'
import RoomCard from '@/components/room/RoomCard.vue'

// [임시]더미데이터
// const rooms = ref([
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다1',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신1',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다2',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신2',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다3',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신3',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다4',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신4',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다5',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신5',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다6',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신6',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다7',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신7',
//     totalNum: 8
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/mountains.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/parallax1.jpg',
//     leftKeyword: '산',
//     rightKeyword: '바다',
//     title: '어디서 살고 싶나요?',
//     hostId: '나는자연인이다8',
//     totalNum: 10
//   },
//   {
//     thumbnail1: 'https://cdn.quasar.dev/img/parallax2.jpg',
//     thumbnail2: 'https://cdn.quasar.dev/img/mountains.jpg',
//     leftKeyword: '치킨',
//     rightKeyword: '피자',
//     title: '당신의 선호 음식은?',
//     hostId: '장사의신8',
//     totalNum: 8
//   }
// ])

const props = defineProps({
  activeCategory: String
})

const roomStore = useRoomStore()
const categoryhostId = computed(() => props.activeCategory)
const rooms = ref([])
watch(
  () => props.activeCategory,
  async (newCategory) => {
    if (newCategory === 'ALL') {
      await roomStore.fetchRoomsAll(); 
      rooms.value = roomStore.roomsAll; 
    } else {
      await roomStore.fetchRoomsCategory(newCategory);
      rooms.value = roomStore.roomsCategory;
    }
  },
  { immediate: true }
);

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
  list-style-type: none;

}

.roomList-container li {
  width: calc(25%); 
}
.see-more {
  width: 100%;
  height:30px;
  text-align:center;
  color:#34227C;
  font-size: 100%;
  font-weight:bold;
  cursor:pointer;
}
</style>
