import { defineStore } from 'pinia'

export const useRoomInfo = defineStore('roomInfo', {
  state: () => ({
    // 각 connectionId에 대응하는 방 정보를 저장할 객체
    roomInfos: {},
  }),
  actions: {
    // connectionId와 debateInfoResponse를 인자로 받는 액션
    setRoomInfo(connectionId, debateInfoResponse) {
      // roomInfo 상태 업데이트
      this.roomInfo[connectionId] = debateInfoResponse;
    },
  },
});
