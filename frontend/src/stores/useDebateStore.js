import { defineStore } from 'pinia'
import axios from 'axios'
import { httpService } from '@/api/axios'

export const useDebateStore = defineStore('debate', {
  state: () => ({
    debate: []
  }),
  actions: {
    async createRoom(roomDto) {
      try {
        const response = await httpService.post('openvidu/session/create', roomDto)
        console.log(`방 생성 정보 : ${response.data}`)
        return response
      } catch (error) {
        console.error(`방 생성 에러: ${error}`)
      }
    },
    async joinDebate(joinRoom) {
      try {
        const response = await httpService.post(`openvidu/session/enter`, joinRoom)
        console.log('연결 정보 응답:', response.data)
        return response
      } catch (error) {
        console.error('연결 정보 가져오기 에러:', error)
      }
    },
    async leaveDebate(debateRoom) {
      try {
        const response = await httpService.post('openvidu/session/leave', debateRoom)
        console.log('성공 ! ', response.status)
      } catch (error) {
        console.error('방 나가기 실패...', error)
      }
    }
  },
  persist: {
    enabled: true, // 상태를 로컬 스토리지에 저장합니다.
    strategies: [{ storage: localStorage }] // 로컬 스토리지에 저장합니다.
  }
})
