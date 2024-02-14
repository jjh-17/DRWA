<template>
  <header>
    <HeaderComponent />
  </header>
  <div class="most-viewers-rooms">
    <div class="select-rooms">
      <div
        class="select-box"
        :class="{ active: activeCarousel === 'pop' }"
        @click="setActiveBox('pop')"
      >
        🤍 인기토론방
      </div>
      <div
        class="select-box"
        :class="{ active: activeCarousel === 'categ' }"
        @click="setActiveBox('categ')"
      >
        💛 관심 주제
      </div>
    </div>

    <div class="carousel" v-if="activeCarousel === 'pop'">
      <div class="carousel__prev1" @click="navigateToPrev">
        <img src="@/assets/img/left_arrow.png" />
      </div>
      <div class="carousel-container">
        <Carousel ref="carousel" :itemsToShow="2.95" :wrapAround="true" :transition="500">
          <Slide v-for="room in popularRooms" :key="room.sessionId">
            <div class="carousel-item"><RoomCard :room="room" /></div>
          </Slide>
        </Carousel>
      </div>
      <div class="carousel__next1" @click="navigateToNext">
        <img src="@/assets/img/right_arrow.png" />
      </div>
    </div>

    <div class="carousel" v-if="activeCarousel === 'categ'">
      <div class="carousel__prev1" @click="navigateToPrev">
        <img src="@/assets/img/left_arrow.png" />
      </div>
      <div class="carousel-container">
        <Carousel ref="carousel" :itemsToShow="2.95" :wrapAround="true" :transition="500">
          <Slide v-for="room in interestRooms" :key="room.hostId">
            <div class="carousel-item"><RoomCard :room="room" /></div>
          </Slide>
        </Carousel>
      </div>
      <div class="carousel__next1" @click="navigateToNext">
        <img src="@/assets/img/right_arrow.png" />
      </div>
    </div>
  </div>
  <div class="categories">
    <div class="debate-category">토론 카테고리</div>

    <div class="category-container">
      <div
        v-for="category in categories"
        :key="category.name"
        :class="['category-box', { active: activeCategory === category.name }]"
        @click="setActiveCategory(category)"
      >
        {{ category.name }}
      </div>
    </div>
  </div>
  <div class="roomList" ref="roomList">
    <RoomsCategory v-if="activeCategory" :activeCategory="activeCategory" />
  </div>
  <div class="room-create">
    <button @click="openModal"><img src="@/assets/img/create.png" /></button>
    <DebateCreateModal
  :isVisible="isModalVisible"
  :disableOptions="false"
  @update:isVisible="isModalVisible = $event"
/>
  </div>
  <div class="goto-top">
    <button @click="scrollToTop"><img src="@/assets/img/top.png" /></button>
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/common/HeaderComponent.vue'
import { onMounted, ref, reactive, toRefs, watch } from 'vue'
import { Carousel, Slide } from 'vue3-carousel'
import { categories } from '@/components/category/Category.js'
import 'vue3-carousel/dist/carousel.css'
import RoomsCategory from '@/components/room/RoomsCategory.vue'
import DebateCreateModal from '@/components/modal/DebateCreateModal.vue'
import { useRoomStore } from '@/stores/useRoomStore.js'
import RoomCard from '@/components/room/RoomCard.vue'
import { useAuthStore } from '@/stores/useAuthStore.js'

const activeCarousel = ref('pop') // 'pop' 또는 'categ'를 가지는 변수

// select-box 클릭 핸들러
const setActiveBox = (boxType) => {
  activeCarousel.value = boxType
}

// Composition API의 ref와 reactive를 사용하여 데이터 정의
const state = reactive({
  activeIndex: 0,
  activeCategory: null,
  activeBox: null
})


const carousel = ref(null)

// 함수 정의
const navigateToPrev = () => {
  if (carousel.value) {
    carousel.value.prev()
  }
}

const navigateToNext = () => {
  if (carousel.value) {
    carousel.value.next()
  }
}

// 룸리스트
// 인기토론방 받아오기
const roomStore = useRoomStore();

const popularRooms = ref([]);
onMounted(async () => {
  await roomStore.fetchRoomsPopular();

  watch(
    () => roomStore.roomsPopular,
    (newPopularRooms) => {
      popularRooms.value = newPopularRooms;
      console.log(popularRooms.value)
    },
    { immediate: true }
  );
});

// 아래는 임시 rooms정보
// const popularRooms = ref([
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

// // 관심토론방 받아오기
// const authStore = useAuthStore()
// const interestRooms = ref([])

// onMounted(async () => {
//   const userId = authStore.userId

//   // userId가 존재하면 관심 토론방을 가져옵니다.
//   if (userId) {
//     await roomStore.fetchRoomsInterestCateg(userId)

//     watch(
//       () => roomStore.roomsInterestCateg,
//       (newInterestRooms) => {
//         interestRooms.value = newInterestRooms
//       },
//       { immediate: true }
//     )
//   }
// })

// 아래는 임시 관심카테고리 방
const interestRooms = ref([
  {
    thumbnail1: 'https://source.unsplash.com/random',
    thumbnail2: 'https://source.unsplash.com/random',
    leftKeyword: '산',
    rightKeyword: '바다',
    title: '어디서 살고 싶나요?',
    hostId: '나는자연인이다',
    totalNum: 10
  },
  {
    thumbnail1: 'https://source.unsplash.com/random',
    thumbnail2: 'https://source.unsplash.com/random',
    leftKeyword: '치킨',
    rightKeyword: '피자',
    title: '당신의 선호 음식은?',
    hostId: '장사의신',
    totalNum: 8
  },
  {
    thumbnail1: 'https://source.unsplash.com/random',
    thumbnail2: 'https://source.unsplash.com/random',
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
])

const roomList = ref(null)
const setActiveCategory = (category) => {
  state.activeCategory = category.name
  if (roomList.value) {
    roomList.value.scrollIntoView({ behavior: 'smooth' })
  }
}

// toRefs를 사용하여 반응성 있는 데이터를 반환
const { activeCategory, images } = toRefs(state)



const isModalVisible = ref(false)

const openModal = () => {
  isModalVisible.value = true
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth' // 부드러운 스크롤 효과
  })
}
</script>

<style scoped>
.select-box.active {
  opacity: 1;
}

.select-box:not(.active) {
  opacity: 0.5;
}
.most-viewers-rooms {
  background-color: rgba(47, 41, 73, 0.5);
  padding: 10px 30px 30px 30px;
}

.select-rooms {
  display: flex;
  gap: 20px;
  padding: 5px;
  height: 50px;
}

.select-box {
  font-size: 15px;
  background-color: #34227c;
  text-align: center;
  line-height: 40px;
  color: #e8e8e8;
  border-radius: 4px;
  width: 150px;
  height: 40px;
}

.carousel {
}

.carousel-container {
  padding: 20px;
  position: relative;
  /* 내부 절대 위치 요소의 기준이 됨 */
}

.carousel__slide {
  padding: 5px;
}

.carousel__viewport {
  perspective: 2000px;
}

.carousel__track {
  transform-style: preserve-3d;
}

.carousel__slide--sliding {
  transition: 0.5s;
}

.carousel__slide {
  opacity: 0.9;
  transform: rotateY(-20deg) scale(0.8);
}

.carousel__slide--active ~ .carousel__slide {
  transform: rotateY(20deg) scale(0.8);
}

.carousel__slide--prev {
  opacity: 0.5;
  z-index: 1;
  transform: rotateY(-20deg) scale(0.8);
}

.carousel__slide--next {
  opacity: 0.5;
  z-index: 1;
  transform: rotateY(20deg) scale(0.8);
}

.carousel__slide--active {
  z-index: 2;
  opacity: 1;
  transform: rotateY(0) scale(1.3);
}

.carousel-item img {
  width: 35rem;
}

.carousel__prev1,
.carousel__next1 {
  cursor: pointer;
  position: absolute;
  /* 절대 위치 설정 */
  top: 50%;
  /* 상위 요소의 중앙에 위치 */
  transform: translateY(-50%);
  /* Y축 기준 중앙 정렬 */
  z-index: 10;
  height: 60%;
  opacity: 1;
}
.carousel__prev1 img,
.carousel__next1 img {
  height: 100%;
}
.carousel__prev1 {
  left: 27%;
}

.carousel__next1 {
  right: 27%;
}

.categories {
  padding: 20px 50px 50px 50px;
}

.debate-category {
  font-size: 1.5rem;
  text-align: center;
  line-height: 40px;
  color: #34227c;
  width: 200px;
  height: 40px;
  font-weight: bold;
}

.category-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
}

.category-box {
  /* flex: 1 0 16%; */
  font-size: 1.5rem;
  padding: 10px 20px;
  margin: 5px;
  border-radius: 4px;
  background-color: #e8ebf9;
  color: #34227c;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: calc((100% / 6) - 10px);
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.category-box:hover,
.category-box.active {
  /* 클릭 시 박스 스타일 변경 */
  background-color: #34227c;
  color: #e8e8e8;
}

.roomList {
  padding: 0px 50px 50px 50px;
}

.select-box {
  padding-right: 10px;
}

.room-create {
  position: fixed;
  bottom: 80px;
  right: 50px;
  z-index: 100;
}

.goto-top {
  position: fixed;
  bottom: 20px;
  right: 50px;
  z-index: 100;
}

.goto-top button,
.room-create button {
  border: none;
  background: none;
  padding: 0;
  cursor: pointer;
}

.goto-top button img,
.room-create button img {
  display: block;
  /* 이미지 주위의 공간 제거 */
  width: 60px;
  height: 60px;
}
</style>
