<template>
    <div class="roomList-container">
        hi
      <!-- <h2>Rooms in Category: {{ categoryName }}</h2> -->
      <ul>
        <!-- <li v-for="room in roomStore.rooms" :key="room.id">
          {{ room.name }} - {{ room.description }}
        </li> -->
      </ul>
    </div>
  </template>

<script setup>
import { onMounted } from 'vue'
import { useRoomStore } from '@/components/stores/roomStore'
import { defineProps } from 'vue'

const props = defineProps({
  activeCategory: String
})

const roomStore = useRoomStore()
const categoryName = 'props.activeCategory'

onMounted(() => {
  roomStore.fetchRooms(categoryName)
})
</script>

<style>
.roomList-container {
    background-color:#E8EBF9;
    width: 100%;
    height: 500px;
}
</style>
