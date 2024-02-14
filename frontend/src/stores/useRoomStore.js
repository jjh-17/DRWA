import { defineStore } from 'pinia'
import axios from 'axios'
import { httpService } from '@/api/axios'


const REST_ROOM_API = `http://i10a708.p.ssafy.io/room`
export const useRoomStore = defineStore('room', {
  state: () => ({   
    roomsPopular: [],
    roomsInterestCateg: [],
    roomsCategory:[],
    roomsAll: [],
  }),
  actions: {
    async fetchRoomsCategory(categoryName) {
      try {
        const response = await httpService.get(`debate/${categoryName}`);
        this.roomsCategory = response.data.debateInfoResponses;
      } catch (error) {
        console.error('Error fetching roomsCategory:', error);
      }
    },
    async fetchRoomsPopular() {
      try {
        const response = await httpService.get(`debate/popular`);
        console.log(response.data);
        this.roomsPopular = response.data.debateInfoResponses;
      } catch (error) {
        console.error('Error fetching roomsPopular:', error);
      }
    },
    async fetchRoomsAll() {
      try {
        const response = await httpService.get(`debate/all`);
        console.log(response.data);
        this.roomsAll = response.data.debateInfoResponses;
      } catch (error) {
        console.error('Error fetching roomsAll:', error);
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
