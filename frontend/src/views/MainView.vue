<template>
  <div class="carousel_container">
    <button type="button" class="carousel__prev" aria-label="Navigate to previous slide" @click="navigateToPrev">
      <svg
        class="carousel__icon"
        viewBox="0 0 24 24"
        role="img"
        aria-label="Arrow pointing to the left"
      >
      <title>Arrow pointing to the left</title>
      <path d="M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6 1.41-1.41z"></path>
      </svg>
    </button>
    <Carousel ref="carousel" :itemsToShow="2.95" :wrapAround="true" :transition="500">
      <Slide v-for="slide in images" :key="slide">
        <div class="carousel__item"><img :src="slide" /></div>
      </Slide>
    </Carousel>
    <button type="button" class="carousel__next" aria-label="Navigate to next slide" @click="navigateToNext">
      <svg
        class="carousel__icon"
        viewBox="0 0 24 24"
        role="img"
        aria-label="Arrow pointing to the right"
      >
      <title>Arrow pointing to the right</title>
        <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"></path>
      </svg>
    </button>
  </div>

  <div> 토론 카테고리 </div>

  <div class="category-container">
    <div
      v-for="category in categories"
      :key="category.name"
      :class="['category-box', { active: activeCategory === category }]"
      @click="setActiveCategory(category)"
    >
      {{ category.name }}
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue'
import { Carousel, Slide } from 'vue3-carousel'
import { useRouter } from 'vue-router'
import { categories } from '@/components/category/Category.js'
import 'vue3-carousel/dist/carousel.css'

export default defineComponent({
  name: 'Autoplay',
  components: {
    Carousel,
    Slide,

  },
  data() {
    return {
      images: [
        'https://cdn.quasar.dev/img/mountains.jpg',
        'https://cdn.quasar.dev/img/parallax1.jpg',
        'https://cdn.quasar.dev/img/parallax2.jpg'
        // [임시] 여기에 방 thumbnail이 들어감.
      ],
      activeIndex: 0,
      activeCategory: null,
    }
  },
  setup() { 
    const router = useRouter();

    function goToCategory(path) {
      router.push(path);
    }
    
    const carousel = ref(null);

    const navigateToPrev = () => {
      if (carousel.value) {
        carousel.value.prev();
      }
    };

    const navigateToNext = () => {
      if (carousel.value) {
        carousel.value.next();
      }
    };

    function setActiveCategory(category) {
      this.activeCategory = category;
      this.selectedCategory = category.english; // 클릭된 카테고리의 english 값을 selectedCategory에 할당
    }

    

    return { carousel, navigateToPrev, navigateToNext, goToCategory, categories, setActiveCategory };
  }
})
</script>

<style scoped>
.carousel_container {
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
  transform: rotateY(-20deg) scale(0.9);
}

.carousel__slide--active ~ .carousel__slide {
  transform: rotateY(20deg) scale(0.9);
}

.carousel__slide--prev {
  opacity: 0.5;
  z-index: 1;
  transform: rotateY(-10deg) scale(0.95);
}

.carousel__slide--next {
  opacity: 0.5;
  z-index: 1;
  transform: rotateY(10deg) scale(0.95);
}

.carousel__slide--active {
  z-index: 2;
  opacity: 1;
  transform: rotateY(0) scale(1.1);
}

img {
  width: 35rem;
}
.carousel__prev, .carousel__next {
  position: absolute; /* 절대 위치 설정 */
  top: 50%; /* 상위 요소의 중앙에 위치 */
  transform: translateY(-50%); /* Y축 기준 중앙 정렬 */
  background-color: rgba(0, 0, 0, 0.5); /* 버튼 배경색 설정 */
  border-radius: 50%; /* 원형 버튼으로 만듬 */
  padding: 30px; /* 버튼 크기 조절 */
  z-index: 10; /* 다른 요소 위에 표시 */
}

.carousel__prev {
  left: 20%; /* 왼쪽에서부터의 거리 */
}

.carousel__next {
  right: 20%; /* 오른쪽에서부터의 거리 */
}

.carousel__icon {
  fill: white; /* 아이콘 색상 */
  width: 24px; /* 아이콘 크기 */
  height: 24px; /* 아이콘 크기 */
}

.category-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  gap: 10px;
}

.category-box {
  padding: 10px 20px;
  margin: 5px;
  border: 1px solid blue;
  border-radius: 4px;
  background-color: #f0f0f0;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: calc(100% / 6 - 10px);
}

.category-box:hover,
.category-box.active { /* 클릭 시 박스 스타일 변경 */
  background-color: #e8e8e8;
}
</style>
