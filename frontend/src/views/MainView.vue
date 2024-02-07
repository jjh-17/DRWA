<template>
  <header>
    <HeaderComponent />
  </header>
  <div class="most-viewers-rooms">
    <div class="select-rooms">
      <div class="select-box" @click="setActiveBox('pop')">🤍 인기토론방</div>
      <div class="select-box" @click="setActiveBox('categ')">💛 관심 주제</div>
    </div>

    <div class="carousel">
      <div class="carousel__prev1" @click="navigateToPrev">&lt;</div>
      <div class="carousel-container">
        <Carousel ref="carousel" :itemsToShow="2.95" :wrapAround="true" :transition="500">
          <Slide v-for="slide in images" :key="slide">
            <div class="carousel-item"><img :src="slide" /></div>
          </Slide>
        </Carousel>
      </div>
      <div class="carousel__next1" @click="navigateToNext">&gt;</div>
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
  <div class="roomList">
    <RoomsCategory v-if="activeCategory" :activeCategory="activeCategory" />
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/common/HeaderComponent.vue'
import { ref, reactive, toRefs } from 'vue'
import { Carousel, Slide } from 'vue3-carousel'
import { categories } from '@/components/category/Category.js'
import 'vue3-carousel/dist/carousel.css'
import RoomsCategory from '@/components/room/RoomsCategory.vue'

// Composition API의 ref와 reactive를 사용하여 데이터 정의
const state = reactive({
  images: [
    'https://cdn.quasar.dev/img/mountains.jpg',
    'https://cdn.quasar.dev/img/parallax1.jpg',
    'https://cdn.quasar.dev/img/parallax2.jpg'
    // [임시] 여기에 방 thumbnail이 들어감. pinia로
  ],
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

const setActiveCategory = (category) => {
  state.activeCategory = category.name
}

const setActiveBox = (boxType) => {
  state.activeBox = boxType
}

// toRefs를 사용하여 반응성 있는 데이터를 반환
const { activeCategory, images } = toRefs(state)
</script>

<style scoped>
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
  position: relative; /* 내부 절대 위치 요소의 기준이 됨 */
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

img {
  width: 35rem;
}
.carousel__prev1,
.carousel__next1 {
  cursor: pointer;
  position: absolute; /* 절대 위치 설정 */
  top: 50%; /* 상위 요소의 중앙에 위치 */
  transform: translateY(-50%); /* Y축 기준 중앙 정렬 */
  background-color: rgba(0, 0, 0, 0); /* 버튼 배경색 설정 */
  z-index: 10; /* 다른 요소 위에 표시 */
  height: 50%;
  line-height: 200px;
  font-size: calc(50vh / 2);
  color: #34227c;
}

.carousel__prev1 {
  left: 15%; /* 왼쪽에서부터의 거리 */
}

.carousel__next1 {
  right: 15%; /* 오른쪽에서부터의 거리 */
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
  padding-right: 10px;;
}
</style>
