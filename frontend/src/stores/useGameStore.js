import { httpService } from "@/api/axios"
import { defineStore } from 'pinia'
import { team } from "@/components/common/Team"

export const useGameStore = defineStore('game', {
  state: () => ({   
    sessionId: '',
    token: '',
    team: team[0].english,
    connectionId : '',
  }),
  actions: {

  },
  // persist: {
  //   enabled: true, // 상태를 로컬 스토리지에 저장합니다.
  //   strategies: [{ storage: localStorage }] // 로컬 스토리지에 저장합니다.
  // }
})