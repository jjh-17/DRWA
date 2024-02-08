<template>
  <div class="roomList-container">
    <h2>Rooms in Category: {{ categoryName }}</h2>
    <ul>
      <!-- <li v-for="room in roomStore.rooms" :key="room.id">
          {{ room.name }} - {{ room.description }}
        </li> -->
    </ul>
  </div>
  
</template>

<script setup>
import { computed, watch, ref } from 'vue'
import { useRoomStore } from '@/stores/useRoomStore'
import { defineProps } from 'vue'

const props = defineProps({
  activeCategory: String
})

const roomStore = useRoomStore()
const categoryName = computed(() => props.activeCategory)

watch(
  () => props.activeCategory,
  (newCategory) => {
    console.log('New active category:', newCategory) // 로깅
    roomStore.fetchRoomsCategory(newCategory)
  },
  { immediate: true }
)


</script>

<style>
.roomList-container {
  background-color: #e8ebf9;
  width: 100%;
  height: 500px;
  position: relative; /* 상대 위치 설정 */
}



</style>
