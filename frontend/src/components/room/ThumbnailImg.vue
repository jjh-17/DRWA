<template>
  <div>
    <q-carousel v-if="images.length > 0" animated v-model="slide" infinite arrows class="imagesCarousel">
      <q-carousel-slide v-for="image in images" :name="image.id" :key="image.id" :img-src="image.urls.small" @click="selectImage(image.id, image.urls.small)" />
    </q-carousel>
    <div v-else>No images found</div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import { QCarousel, QCarouselSlide } from 'quasar'

const props = defineProps(['searchQuery'])
const emits = defineEmits(['selectImg'])

const images = ref([])
const slide = ref('')

watch(() => props.searchQuery, () => {
  searchImages(); // 검색어가 변경될 때마다 이미지를 검색합니다.
}, { immediate: true });

async function searchImages() {
  if (!props.searchQuery.trim()) {
    images.value = []; // 검색어가 비어 있으면 이미지 배열을 비웁니다.
    return;
  }

  const accessKey = '2PzjHzjtz_UkTIsvnagcuoQrr18VYgsR4iGUtx_XpF4'; // Unsplash 접근 키
  const url = `https://api.unsplash.com/search/photos?query=${props.searchQuery}&client_id=${accessKey}`;

  try {
    const response = await axios.get(url);
    if (response.data && response.data.results) {
      images.value = response.data.results;
      if (images.value.length > 0) {
        slide.value = images.value[0].id;
      } else {
        console.log("No images found for the query:", props.searchQuery);
      }
    }
  } catch (error) {
    console.error("Error fetching images:", error);
    images.value = []; // 에러 발생 시 이미지 배열을 비웁니다.
  }
}


// 이미지 선택 시 부모 컴포넌트에 이미지 URL을 전달하는 함수
function selectImage(imageId, imageUrl) {
  emits('selectImg', { imageId, imageUrl });
}
</script>

<style scoped>
.q-carousel {
  height: 80px;
  width: 80px;
}

.q-carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.imagesCarousel .q-carousel__control {
  bottom: -40px;
}
</style>
