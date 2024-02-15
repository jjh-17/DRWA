import { defineStore } from 'pinia'
import { httpService } from '@/api/axios'

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
    async fetchRoomsInterestCateg() {
      try {
        const response = await httpService.get(`debate`);
        this.roomsInterestCateg = response.data.debateInfoResponses;
      } catch (error) {
        console.error('Error fetching roomsInterestCateg:', error);
      }
    },
    async searchRooms(query, type) {
      try {
        const response = await httpService.get(`room/search/${type}`, {
          params: { query }
        });
        this.roomsAll = response.data;
      } catch (error) {
        console.error('Error searching rooms:', error);
      }
    },
    async updateRoomThumbnail(thumbnailUpdateInfo) {
      try {
        await httpService.post(`room/rooms/thumbnail`, thumbnailUpdateInfo);
      } catch (error) {
        console.error('Error updating room thumbnail:', error);
      }
    }
  },
  

})
