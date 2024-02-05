<template>
  <div>
    <h1>검색 결과</h1>
    <p>검색어: {{ query }}</p>
    <p>검색 타입: {{ type }}</p>

    <div v-if="rooms.length > 0">
      <div v-for="room in rooms" :key="room.id">
        <p>방 제목: {{ room.title }}</p>
        <p>방 제시어: {{ room.keyword }}</p>
      </div>
    </div>
    <div v-else>
      <p>검색 결과가 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';

const route = useRoute();
const rooms = ref([]);

onMounted(async () => {
    const { type, query } = route.query;
    try {
        const response = await axios.get(`http://localhost:8080/search/${type}`, { params: { query } });
        rooms.value = response.data;
    } catch (error) {
        console.error('검색 결과를 불러오는 중 오류 발생:', error);
    }
});
</script>
