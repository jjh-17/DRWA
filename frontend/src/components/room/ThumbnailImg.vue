<!-- <template>
  <div>
    <input v-model="searchQuery" @keyup.enter="searchImages" placeholder="Search for images...">
    <button @click="searchImages">Search</button>
    <div class="images">
      <img v-for="image in images" :key="image.id" :src="image.urls.small" :alt="image.alt_description">
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const searchQuery = ref('');
const images = ref([]);

const searchImages = async () => {
  const accessKey = '2PzjHzjtz_UkTIsvnagcuoQrr18VYgsR4iGUtx_XpF4'; // 여기에 Unsplash API 액세스 키를 넣으세요.
  const url = `https://api.unsplash.com/search/photos?query=${searchQuery.value}&client_id=${accessKey}`;

  try {
    const response = await axios.get(url);
    images.value = response.data.results;
  } catch (error) {
    console.error("Error fetching images:", error);
  }
};
</script>

<style>
.images img {
  width: 200px;
  height: auto;
  margin: 10px;
}
</style> -->

<template>
  <div class="img-container">
    <div class="imgSearch">
      <input v-model="searchQuery" @input="fetchImages" placeholder="키워드를 검색하세요.">
      <button @click="attachImage">이미지 첨부</button>
    </div>
    <div class="selectedImage" v-for="image in images" :key="image.id">
      <img :src="image.urls.small" :alt="image.description" width="200" height="200">
      <button @click="selectImage(image)">선택</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';


const searchQuery = ref('');
const images = ref([]);
const selectedImage = ref({});

const fetchImages = async () => {
  if (!searchQuery.value) {
    images.value = [];
    return;
  }
  
  // const accessKey = 'YOUR_UNSPLASH_ACCESS_KEY';
  const url = `https://api.unsplash.com/search/photos?query=${searchQuery.value}`;

  try {
    const response = await axios.get(url);
    images.value = response.data.results; // Unsplash API 응답에서 이미지 배열을 처리
  } catch (error) {
    console.error('Error fetching images:', error);
    images.value = [];
  }
};

const selectImage = (image) => {
  selectedImage.value = image; // 선택된 이미지 객체 전체를 저장
};

const attachImage = async () => {
  if (!selectedImage.value.urls) {
    console.error('선택된 이미지가 없습니다.');
    return;
  }

  // 예시: thumbnail_id는 선택된 이미지의 id를 사용
  const data = {
    thumbnail_id: selectedImage.value.id,
    original_path: selectedImage.value.urls.full // 원본 이미지 URL
  };

  try {
    await axios.post('http://your-backend-server.com/api/thumbnails', data, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log('이미지 정보가 성공적으로 DB에 저장되었습니다');
  } catch (error) {
    console.error('DB에 이미지 정보 저장 실패:', error);
  }
};

</script>
<style scoped>
.img-container{
  text-align:center;

}
.imgSearch{
  display: felx;
  justify-content: center;
}
.selectedImage{
  display: felx;
  justify-content: center;
}
</style>