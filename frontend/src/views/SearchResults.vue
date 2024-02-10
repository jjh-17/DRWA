<template>
  <div>
    <template v-if="rooms.length > 0">
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
    <template v-else>
      <div class="q-pa-md">
        <p>검색 결과가 없습니다.</p>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoomStore } from '@/stores/useRoomStore'; // import the room store
import { useRoute } from 'vue-router';

const route = useRoute();
const searchQuery = ref(route.query.query || '');


const { searchRooms } = useRoomStore();

const rooms = ref([]);

async function fetchData() {
  try {
    await searchRooms(searchQuery.value, route.query.type);
    if (rooms.value.length === 0) {
      console.log("dhkd");
      rooms.value = [
        { id: 1, title: '임시 방 1', host: '임시 호스트', keyword: '임시 키워드', totalMembers: 0 },
        { id: 2, title: '임시 방 2', host: '임시 호스트', keyword: '임시 키워드', totalMembers: 0 },
      ];
    }
  } catch (error) {
    console.error('Error searching rooms:', error);
  }
}

onMounted(fetchData);
</script>
