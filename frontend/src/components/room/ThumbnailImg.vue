<template>
  <div>
    <input v-model="searchQuery" @keyup.enter="searchImages" placeholder="키워드를 검색하세요...">
    <button @click="searchImages">검색</button>
    <q-carousel animated v-model="slide" infinite arrows class="imagesCarousel">
      <q-carousel-slide v-for="image in images" :name="image.id" :key="image.id" :img-src="image.urls.small"
        @click="selectImg(image.urls.small)" />

    </q-carousel>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { QCarousel, QCarouselSlide } from 'quasar';

const searchQuery = ref('');
const images = ref([]);
const slide = ref('');

const searchImages = async () => {
  const accessKey = '2PzjHzjtz_UkTIsvnagcuoQrr18VYgsR4iGUtx_XpF4';
  const url = `https://api.unsplash.com/search/photos?query=${searchQuery.value}&client_id=${accessKey}`;

  try {
    const response = await axios.get(url);
    images.value = response.data.results;
    if (images.value.length > 0) {
      slide.value = images.value[0].id;
    }
  } catch (error) {
    console.error("Error fetching images:", error);
  }
};
const selectImg = (imageUrl) => {
  selectImg.value = imageUrl;
  console.log('Selected image URL:', selectImg.value);
};

</script>

<style>
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
