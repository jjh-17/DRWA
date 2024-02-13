import { defineStore, storeToRefs } from "pinia";
import { ref, computed, reactive } from "vue";
import { OpenVidu } from 'openvidu-browser'
import { team } from '@/components/common/Team.js'
import { useAuthStore } from "./useAuthStore";

export const useSessionStore = defineStore('sessionStore', () => {
  // 세션 정보
  const sessionInfo = reactive({
    session: ref(undefined),
    OV: ref(undefined),
    publisher: ref(undefined),  // 자기 자신
    subscribersAll: ref([]),    // 전체 리스트
    subscribersLeft: ref([]),   // 팀 A 리스트
    subscribersRight: ref([]),  // 팀 B 리스트
  })

  // 참가자 정보
  const authStore = useAuthStore();
  const { memberId, userId } = storeToRefs(authStore);
  const role = ref(team[3].english);

  // 화상 정보
  const communication = reactive({
    isMicOn: false,
    isCameraOn: false,
    isShareOn: false,
    isMicHandleAvailable: false,
    isCameraHandleAvailable: false,
    isShareHandleAvailable: false,
  });
  

  // 채팅방
  const chatting = reactive({
    messagesLeft: [],
    messagesRight: [],
    messagesAll: [],
  })


  // 세션 합류 메서드
  const joinSession = () => {
    // openvidu, 세션 객체 생성
    sessionInfo.OV.value = new OpenVidu()
    sessionInfo.session.value = sessionInfo.OV.value.initSession()

    // 새로운 publish/subscribe 감지
    sessionInfo.session.value.on('streamCreated', ({ stream }) => {
      // 새로운 subscriber
      const subscriber = sessionInfo.value.subscribe(stream)
      
      // clientData를 받아온다 => 
      const clientDatas = subscriber.stream.connection.data.splt('"');
      const datas = clientDatas[3].split(',');

      // 관전자로 편입
      sessionInfo.subscribersAll.value.push(subscriber)
      
    })

    // 세션 종료 작업 => 현재 팀에서 나간다.
    session.value.on('streamDestroyed', ({ stream }) => {
      leaveTeam(stream.streamManager)
    })

    // 비동기 관련 오류 처리
    session.value.on('exception', ({ exception }) => {
      console.warn(exception)
    })

    // 이벤트 수신 처리 => targetTeam에 따라 메시지 저장 공간 변화
    session.value.on(`signal:chat`, (event) => {
      const messageData = JSON.parse(event.data)

      if (messageData.targetTeam == team[0].english) {
        messagesLeft.value.push(messageData);
      } else if (messageData.targetTeam == team[1].english) {
        messagesRight.value.push(messageData)
      } else {
        messagesAll.value.push(messageData)
      }
    })

    // 토큰이 유효하면 세션 연결
    getToken().then((token) => {
      session.value
        .connect(token, { clientData: `${authStore.userId.value},${authStore.memberId.value},${role.value}` })
        .then(() => {
          // 사용자의 openVidu 설정 생성
          let publisher_tmp = OV.value.initPublisher(undefined, {
            audioSource: undefined, // 오디오 => 기본 microphone
            videoSource: undefined, // 비디오 => 기본 webcam
            publishAudio: false, // 시작 오디오 상태(ON/OFF)
            publishVideo: false, // 시작 비디오 상태(ON/OFF)
            resolution: '311x170', // 카메라 해상도
            frameRate: 30, // 카메라 프레임
            insertMode: 'APPEND', // 비디오가 추가되는 방식
            mirror: false // 비디오 거울 모드 X
          })

          // 사용자 publisher 설정 수정 및 송출
          publisher.value = publisher_tmp
          session.value.publish(publisher_tmp)
        })
        .catch((error) => {
          console.log('session 연결 실패 : ', error)
        })
    })

    // 윈도우 종료 시 세션 나가기 이벤트 등록
    window.addEventListener('beforeunload', leaveSession)
  }

  // 현재 팀에서 나가기
const leaveTeam = (team, subscriber) => {
  let index
  if (userInfo.team.value == team[0].english) {
    index = openVidu.subscribersLeft.value.indexOf(subscriber, 0)
    if (index >= 0) {
      openVidu.subscribersLeft.value.splice(index, 1)
    }
  } else if (userInfo.team.value == team[1].english) {
    index = openVidu.subscribersRight.value.indexOf(subscriber, 0)
    if (index >= 0) {
      openVidu.subscribersRight.value.splice(index, 1)
    }
  } else if (userInfo.team.value == team[2].english) {
    index = openVidu.subscribersLeft.value.indexOf(subscriber, 0)
    if (index >= 0) {
      openVidu.subscribersLeft.value.splice(index, 1)
    }
  } else if (userInfo.team.value == team[3].english) {
    index = openVidu.subscribersLeft.value.indexOf(subscriber, 0)
    if (index >= 0) {
      openVidu.subscribersLeft.value.splice(index, 1)
    }
  }
}

// 세션 나가기 메서드
const leaveSession = () => {
  // 세션 연결 종료
  if (openVidu.session.value) openVidu.session.value.disconnect()

  // Empty all properties...
  openVidu.session.value = undefined
  openVidu.publisher.value = undefined
  openVidu.subscribersLeft.value = []
  openVidu.subscribersRight.value = []
  openVidu.subscribersJuror.value = []
  openVidu.subscribersWatcher.value = []
  openVidu.OV.value = undefined

  // 윈도우 종료 시 세션 나가기 이벤트 삭제
  window.removeEventListener('beforeunload', leaveSession)
}

// API 호출 메서드를 이용하여 토큰 반환
async function getToken() {
  // API 호출 필요
  await createSession(openVidu.sessionId.value)
  const token = await createToken(debateId)
  return token
}

})