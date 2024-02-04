<template>
  <div class="img-container">
    <div class ="imgSearch">
    <input v-model="searchQuery" @input="fetchImages" placeholder="키워드를 검색하세요.">
    <button @click="attachImage">이미지 첨부</button>
    </div>
    <div class="selectedImg" v-for="image in images" :key="image.id">
      <img :src="image.url" :alt="image.alt_description" width="200" height="200">
      <button @click="selectImage(image.url)">선택</button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';

export default {
  setup() {
    const searchQuery = ref('');
    const images = ref([]);
    const selectedImage = ref('');

    const fetchImages = async () => {
      if (!searchQuery.value) {
        images.value = [];
        return;
      }

      try {
        const response = await axios.get(`https://source.unsplash.com/featured/?${searchQuery.value}`);
        // Unsplash API에서 이미지를 랜덤하게 반환하므로 결과가 배열이 아닌 이미지 URL 하나만 반환됩니다.
        // 여기서는 이미지 URL 하나만 images 배열에 객체 형태로 저장합니다.
        images.value = [{ id: 1, url: response.request.responseURL, title: searchQuery.value }];
      } catch (error) {
        console.error('Error fetching images:', error);
        // 에러 발생 시 이미지 배열을 초기화합니다.
        images.value = [];
      }
    };

    const selectImage = (imageUrl) => {
      selectedImage.value = imageUrl;
    };

    const attachImage = async () => {
      if (!selectedImage.value) {
        console.error('선택된 이미지가 없습니다.');
        return;
      }

      try {
        await axios.post('http://localhost:5173/upload', {
          imageUrl: selectImage.value
        }, {
          headers: {
            'Content-Type': 'application/json'
          }
        });
        console.log('이미지가 성공적으로 첨부되었습니다');
      } catch (error) {
        console.error('이미지 첨부에 실패하였습니다:', error);
      }
    };

    return {
      searchQuery,
      images,
      fetchImages,
      attachImage,
      selectImage,
    };
  },
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
.selectedImg{
  display: felx;
  justify-content: center;
}
</style>
