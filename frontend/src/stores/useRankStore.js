import { defineStore } from 'pinia';
import { httpService } from '@/api/axios'; 

export const useRankStore = defineStore('rankStore', {
  state: () => ({
    rankings: [],
  }),
  actions: {
    async fetchRankings(category) {
      try {
        const response = await httpService.get(`ranking/${category}`);
        this.rankings = response.data;
      } catch (error) {
        console.error("Error fetching rankings:", error);
      }
    },
    async searchUserRank(nickname) {
      try {
        const response = await httpService.get(`ranking/${nickname}`);
        this.rankings = [response.data];
      } catch (error) {
        console.error("Error searching user rank:", error);
      }
    },
  },
});
