import { defineStore } from 'pinia';
import axios from 'axios';

const REST_ROOM_API = `http://i10a708.p.ssafy.io/ranking`
export const useRankStore = defineStore('rankStore', {
  state: () => ({
    rankings: [],
  }),
  actions: {
    async fetchRankings(category) {
      try {
        const response = await axios.get(`${REST_ROOM_API}/${category}`);
        this.rankings = response.data;
      } catch (error) {
        console.error("Error fetching rankings:", error);
      }
    },
    async searchUserRank(nickname) {
      try {
        const response = await axios.get(`${REST_ROOM_API}/${nickname}`);
        this.rankings = [response.data];
      } catch (error) {
        console.error("Error searching user rank:", error);
      }
    },
  },
});
