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
    },
    async searchRooms(query, type) {
      try {
        const response = await axios.get(`${REST_ROOM_API}/search/${type}`, {
          params: { query }
        });
        this.$state.rooms = response.data;
      } catch (error) {
        console.error('Error searching rooms:', error);
      }
    },    
    async updateRoomThumbnail(thumbnailUpdateInfo) {
      try {
        await axios.post(`${REST_ROOM_API}/rooms/thumbnail`, thumbnailUpdateInfo);
      } catch (error) {
        console.error('Error updating room thumbnail:', error);
      }
    }
  },
  

})
