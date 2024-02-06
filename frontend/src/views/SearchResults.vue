<template>
  <div v-for="room in rooms" :key="room.id" class="q-pa-md">
    <q-card>
      <q-card-section class="row items-center">
        <q-avatar>
          <img :src="room.thumbnailUrl">
        </q-avatar>
        <div class="q-ml-md">
          <div class="text-h6">{{ room.title }}</div>
          <div class="text-subtitle2">방장: {{ room.host }}</div>
        </div>
      </q-card-section>
      <q-card-section>
        제시어: {{ room.keyword }}
      </q-card-section>
      <q-card-section>
        총인원수: {{ room.totalMembers }}
      </q-card-section>
    </q-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';

const route = useRoute();
const rooms = ref([]);
const searchQuery = ref(route.query.query || '');

async function searchRooms() {
    const type = route.query.type;
    try {
        const response = await axios.get(`http://localhost:8080/room/search/${type}`, {
            params: { query: searchQuery.value }
        });
        rooms.value = response.data; 
    } catch (error) {
        console.error('검색 중 오류 발생:', error);
    }
}

onMounted(searchRooms);
</script>
