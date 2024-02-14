import { httpService } from "@/api/axios"
import { defineStore } from 'pinia'

const REST_DEBATE_API = `http://i10a708.p.ssafy.io/debate`

export const useGameStore = defineStore('game', {
  state: () => ({   
    sessionId: '',

  }),
  actions: {

  },
  persist: {
    enabled: true, // 상태를 로컬 스토리지에 저장합니다.
    strategies: [{ storage: localStorage }] // 로컬 스토리지에 저장합니다.
  }
})