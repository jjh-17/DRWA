import { defineStore } from "pinia";
import { httpService } from '@/api/axios'
import router from '@/router'
import { team } from "@/components/common/Team";

export const useGameInfoStore = defineStore('gameInfo', {
  state: () => ({
    // 방 정보
    title: '',
    keywordA: '',
    keywordB: '',
    playerNum: -1,
    jurorNum: -1,
    debateTime: -1,
    preparationTime: -1,
    questionTime: -1,
    thumbnailAUrl: '',
    thumbnailBUrl: '',
    isPublic: true,
    password: '',

    // 세션 관련 정보
    session: undefined,
    OV: undefined,
    publisher: undefined,  // 자기 자신
    teamLeftList: [],   // 팀 A 리스트(자기 자신 포함 가능)
    teamRightList: [],  // 팀 B 리스트(자기 자신 포함 가능)
    connectionId: '',

    // 참가자 정보
    team: team[3].english,
    playerLeftList: [],
    playerRightList: [],

    // 화상 기기 정보
    micList: [],
    cameraList: [],
    selectedMic: '',
    selectedCamera: '',
    isMicOn: false,
    isCameraOn: false,
    isShareOn: false,
    isMicHandleAvailable: false,
    isCameraHandleAvailable: false,
    isShareHandleAvailable: false,
  
    // 채팅방 정보
    messagesLeft: [],
    messagesRight: [],
    messagesAll: [],

    // 투표 정보
    voteLeftNum: 0,
    voteRightNum: 0,
    jurorVoteLeftNum: 0,
    jurorVoteRightNum: 0,
  }),
  actions: {
    /**
     * 모든 데이터 초기화 함수
     */
    initGameData() {
      this.session = undefined
      this.OV = undefined
      this.publisher = undefined
      this.teamLeftList = []
      this.teamRightList = []
      this.connectionId = ''

      // 참가자 정보
      this.team = team[3].english
      this.playerLeftList = []
      this.playerRightList = []

      // 화상 기기 정보
      this.micList = []
      this.cameraList = []
      this.selectedMic = ''
      this.selectedCamera = ''
      this.isMicOn = false
      this.isCameraOn = false
      this.isShareOn = false
      this.isMicHandleAvailable = false
      this.isCameraHandleAvailable = false
      this.isShareHandleAvailable = false
  
      // 채팅방 정보
      this.messagesLeft = []
      this.messagesRight = []
      this.messagesAll = []

      // 투표 정보
      this.voteLeftNum = 0
      this.voteRightNum = 0
      this.jurorVoteLeftNum = 0
      this.jurorVoteRightNum = 0
    }
  },
  persist: {
    // 상태를 로컬 스토리지에 저장
    enabled: true,
    strategies: [{ storate: localStorage }],
  }
})