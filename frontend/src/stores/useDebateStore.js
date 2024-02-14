import { defineStore } from 'pinia'
import axios from 'axios'
import { httpService } from "@/api/axios"

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

    async createRoom(roomDto) {
      try {
        const response = await httpService.post('/openvidu/session', roomDto)
        console.log(`방 생성 정보 : ${response.data}`)
        return response
      } catch (error) {
        console.error(`방 생성 에러: ${error}`)
      }
    },
    async joinDebate(sessionId) {
      try {
        const response = await httpService.get(`/openvidu/session/${sessionId}`);
        console.log('연결 정보 응답:', response.data);
        return response
      } catch (error) {
        console.error('연결 정보 가져오기 에러:', error);
      }
    },
    
  },
  persist: {
    enabled: true, // 상태를 로컬 스토리지에 저장합니다.
    strategies: [{ storage: localStorage }] // 로컬 스토리지에 저장합니다.
  }

})
