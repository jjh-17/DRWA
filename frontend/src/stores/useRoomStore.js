import { defineStore } from 'pinia'
import axios from 'axios'

const REST_ROOM_API = `http://i10a708.p.ssafy.io/room`
export const useRoomStore = defineStore('room', {
  state: () => ({   
    roomsPopular: [],
    roomsInterestCateg: [],
    roomsCategory:{},
  }),
  actions: {
    async fetchRoomsCategory(categoryName) {
      // try {
      //   const response = await axios.get(`${REST_ROOM_API}/${categoryName}`);
      //   this.roomsCategory = response.data;
      // } catch (error) {
      //   console.error('Error fetching roomsCategory:', error);
      // }
    },
    async fetchRoomsPopular() {
      try {
        const response = await axios.get(`${REST_ROOM_API}/popular`);
        this.roomsPopular = response.data;
      } catch (error) {
        console.error('Error fetching roomsPopular:', error);
      }
    },
    async fetchRoomsInterestCateg(userId) {
      try {
        const response = await axios.get(`${REST_ROOM_API}/${userId}`);
        this.roomsCategory = response.data;
      } catch (error) {
        console.error('Error fetching roomsInterestCateg:', error);
      }
    }
  },
  

})
