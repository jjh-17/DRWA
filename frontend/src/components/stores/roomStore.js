import { defineStore } from 'pinia'
import axios from 'axios'

export const useRoomStore = defineStore('roomList', {
  state: () => ({
    rooms: []
  }),
  actions: {
    // async fetchRooms(categoryName) {
    //   try {
    //     const response = await axios.get(`http://localhost:8080/api/rooms/${categoryName}`);
    //     this.rooms = response.data;
    //   } catch (error) {
    //     console.error('Error fetching rooms:', error);
    //   }
    // }
  }
})
