import { defineStore } from 'pinia'
import axios from 'axios'

const REST_DEBATE_API = `http://i10a708.p.ssafy.io/debate`
export const useDebateStore = defineStore('debate', {
  state: () => ({   
    debate: [],
  }),
  actions: {
    async getDebate(debateId) {
      // try {
      //   const response = await axios.get(`${REST_DEBATE_API}/${debateId}`);
      //   this.debate = response.data;
      // } catch (error) {
      //   console.error('Error fetching debate:', error);
      // }
    },
    
  },
  

})
