<script setup>
import DebateHeaderBar from '@/components/debate/DebateHeaderBar.vue'
import DebateVideos from '@/components/debate/DebateVideos.vue'
import ChattingBar from '@/components/debate/ChattingBar.vue'
import DebateBottomBar from '@/components/debate/DebateBottomBar.vue'
import GameStartModal from '@/components/modal/GameStartModal.vue'
import { ref, reactive, toRefs, computed, defineProps, watch } from 'vue'
import { useRoute } from 'vue-router'
import { onMounted, onUnmounted } from 'vue'
import { useDebateStore } from '@/stores/useDebateStore'
import { useAuthStore } from '@/stores/useAuthStore'
import { OpenVidu } from 'openvidu-browser'
import UserVideo from '@/components/debate/UserVideo.vue'
import { team } from '@/components/common/Team.js'
import { useGameStore } from '@/stores/useGameStore'
import VotingResult from '@/components/modal/VotingResult.vue'

const route = useRoute()

// === 변수 ===
// store 정보
const debateStore = useDebateStore()
const gameStore = useGameStore()
const authStore = useAuthStore()

// 세션 정보
const sessionInfo = reactive({
  session: undefined,
  OV: undefined,
  publisher: undefined, // 자기 자신
  phase: 0,

  // index == 각 팀에서의 순서
  teamLeftList: [], // 팀 A 리스트(자기 자신 포함 가능)
  teamRightList: [] // 팀 B 리스트(자기 자신 포함 가능)
})

// 불변 정보
const constInfo = {
  team: gameStore.team,
  token: gameStore.token,
  roomInfo: gameStore.roomInfo,

  memberId: authStore.memberId,
  nickname: authStore.nickname
}

// 참가자 정보
const playerInfo = reactive({
  // team: '',
  // index == 각 팀에서의 순서
  // 각 팀 플레이어의 {memberId, nickname } 저장
  playerLeftList: [],
  playerRightList: [],
  jurorList: [],
  watcherList: []
})

// 화상 정보
const communication = reactive({
  micList: [],
  cameraList: [],
  selectedMic: '',
  selectedCamera: [],

  isMicOn: false,
  isCameraOn: false,
  isShareOn: false,

  isMicHandleAvailable: false,
  isCameraHandleAvailable: false,
  isShareHandleAvailable: false
})

// 채팅방
const chatting = reactive({
  messagesLeft: [],
  messagesRight: [],
  messagesAll: []
})

// 투표
const vote = reactive({
  voteLeftNum: 0,
  voteRightNum: 0,
  jurorVoteLeftNum: 0,
  jurorVoteRightNum: 0
})

// 세션에 팀별로 합류
function joinSession() {
  // 팀 세션 생성
  sessionInfo.OV = new OpenVidu()

  sessionInfo.session = sessionInfo.OV.initSession()

  // 다른 사용자의 stream(publisher) 생성 감지 이벤트
  // 토큰이 유효하면 세션 연결
  sessionInfo.session.on('streamCreated', ({ stream }) => {
    // 새로운 stream의 클라이언트 정보(memberId, nickname, team)를 받아옴
    const clientDatas = stream.connection.data.split('"')
    const datas = clientDatas[3].split(',')
    // 새로운 subscriber
    const subscriber = sessionInfo.session.subscribe(stream)

    // data에 따라 팀A, 팀B에 데이터 저장
    if (datas[2] == team[0].english) {
      sessionInfo.teamLeftList.push(subscriber)
      playerInfo.playerLeftList.push({
        memberId: datas[0],
        nickname: datas[1]
      })
    } else if (datas[2] == team[1].english) {
      sessionInfo.teamRightList.push(subscriber)
      playerInfo.playerRightList.push({
        memberId: datas[0],
        nickname: datas[1]
      })
    } else {
      console.error(`잘못된 팀 - ${datas[2]} 입니다.`)
    }

    console.log(`streamCreated ${datas[2]} :`, playerInfo)
  })

  // 다른 사용자의 stream 종료 감지(juror, watcher는 해당 X)
  sessionInfo.session.on('streamDestroyed', ({ stream }) => {
    leaveTeam(stream.streamManager)
    console.log(`streamDestroyed :`, playerInfo)
  })

  // 채팅 이벤트 수신 처리(닉네임, 목표 팀, 메시지 내용) => targetTeam에 따라 메시지 저장 공간 변화
  sessionInfo.session.on('signal:chat', (event) => {
    const messageData = JSON.parse(event.data)

    if (messageData.targetTeam == team[0].english) {
      chatting.messagesLeft.push(messageData)
    } else if (messageData.targetTeam == team[1].english) {
      chatting.messagesRight.push(messageData)
    } else if (messageData.targetTeam == team[4].english) {
      chatting.messagesAll.push(messageData)
    } else {
      console.error(`잘못된 채팅 이벤트 수신 : ${messageData}`)
    }
  })

  // 팀 투표 이벤트 수신 처리(이전 투표 팀, 현재 투표 팀, 소속 팀)
  sessionInfo.session.on('signal:voteTeam', (event) => {
    const messageData = JSON.parse(event.data)

    // 이전 투표 수 취소
    if (messageData.beforeTeam == team[0].english) {
      vote.voteLeftNum--
      if (messageData.team == team[2].english) {
        vote.jurorVoteLeftNum--
      }
    } else if (messageData.beforeTeam == team[1].english) {
      vote.voteRightNum--
      if (messageData.team == team[2].english) {
        vote.jurorVoteRightNum--
      }
    }

    // 현재 투표 반영
    if (messageData.targetTeam == team[0].english) {
      vote.voteLeftNum++
      if (messageData.team == team[2].english) {
        vote.jurorVoteLeftNum++
      }
    } else if (messageData.targetTeam == team[1].english) {
      vote.voteRightNum++
      if (messageData.team == team[2].english) {
        vote.jurorVoteRightNum++
      }
    }
  })

  // 배심원, 관전자 참가, 나가기 시 시그널
  sessionInfo.session.on('signal:jurorIn', (event) => {
    const messageData = JSON.parse(event.data)
    playerInfo.jurorList.push({
      memberId: messageData.memberId,
      nickname: messageData.nickname
    })
  });
  sessionInfo.session.on('signal:jurorOut', (event) => {
    const messageData = JSON.parse(event.data)
    for (let i = 0; i < playerInfo.jurorList.length; i++) {
      if (playerInfo.jurorList[i].memberId == messageData.memberId) {
        playerInfo.jurorList.splice(i)
      }
    }
  })
  sessionInfo.session.on('signal:watcherIn', (event) => {
    const messageData = JSON.parse(event.data)
    playerInfo.watcherList.push({
      memberId: messageData.memberId,
      nickname: messageData.nickname
    })
  });
  sessionInfo.session.on('signal:watcherOut', (event) => {
    const messageData = JSON.parse(event.data)
    for (let i = 0; i < playerInfo.watcherList.length; i++) {
      if (playerInfo.watcherList[i].memberId == messageData.memberId) {
        playerInfo.watcherList.splice(i)
      }
    }
  })
  sessionInfo.session.on('signal:startGame', () => {
    sessionInfo.phase += 1
    handleMedia();
    console.log('Game phase updated by startGame:', sessionInfo.phase)
  })
  sessionInfo.session.on('signal:nextPhase', () => {
    sessionInfo.phase += 1
    handleMedia();
    console.log('Game phase updated by nextPhase:', sessionInfo.phase)
  })
  sessionInfo.session.on('signal:endGame', () => {
    isEnd.value = true
    sessionInfo.phase = 0
    console.log('@@@@@@@@@@@@토론끝@@@@@@@@@@')
  })

  // 비동기 관련 오류 처리
  sessionInfo.session.on('exception', ({ exception }) => {
    console.warn(exception)
  })

  // 화면공유
  // sessionInfo.session.on('streamCreated', (event) => {
  //   if (event.stream.typeOfVideo == 'SCREEN') {
  //     // Subscribe to the Stream to receive it. HTML video will be appended to element with 'container-screens' id
  //     var subscriberScreen = session.subscribe(event.stream, 'container-screens')
  //     // When the HTML video has been appended to DOM...
  //     subscriberScreen.on('videoElementCreated', (event) => {
  //       // Add a new <p> element for the user's nickname just below its video
  //       appendUserData(event.element, subscriberScreen.stream.connection)
  //     })
  //   }
  // })

  sessionInfo.session
    .connect(constInfo.token, {
      clientData: `${authStore.memberId},${authStore.nickname},${constInfo.team}`
    })
    .then(() => {
      console.log('세션 연결', constInfo)

      // 기존 배심원
      if (constInfo.roomInfo.jurors != null) {
        constInfo.roomInfo.jurors.forEach((juror) => {
          if (constInfo.memberId != juror.memberId) {
            playerInfo.jurorList.push({
              memberId: juror.memberId,
              nickname: juror.nickName,
            })
          }
        })
      }

      // 기존 시청자
      if (constInfo.roomInfo.watchers != null) {
        constInfo.roomInfo.watchers.forEach((watcher) => {
          if (constInfo.memberId != watcher.memberId) {
            playerInfo.watcherList.push({
              memberId: watcher.memberId,
              nickname: watcher.nickName,
            })
          }
        })
      }

      
      if (constInfo.team == team[0].english || constInfo.team == team[1].english) {
        initCommunication(constInfo.team)
        sessionInfo.publisher = getDefaultPublisher()
        sessionInfo.session.publish(sessionInfo.publisher)

        if (constInfo.team == team[0].english) {
          sessionInfo.teamLeftList.push(sessionInfo.publisher)
          playerInfo.playerLeftList.push({
            memberId: authStore.memberId,
            nickname: authStore.nickname
          })
        } else if (constInfo.team == team[1].english) {
          sessionInfo.teamRightList.push(sessionInfo.publisher)
          playerInfo.playerRightList.push({
            memberId: authStore.memberId,
            nickname: authStore.nickname
          })
        }
      } else if (constInfo.team == team[2].english) {
        sendJurorInMessage()
      } else if (constInfo.team == team[3].english) {
        sendWatcherInMessage()
      }
    })
    .catch((error) => {
      console.error(`세션 연결 실패 : ${error}`)
    })

  // 윈도우 종료 시 세션 나가기 이벤트 등록
  window.addEventListener('beforeunload', leaveSession)
}

function handleMedia() {
  let idx
  switch (sessionInfo.phase % 4) {
    case 1:   // 팀 A
      console.log(playerInfo)
      if (constInfo.team == team[1].english) {        // teamB는 전부 음소거 및 권한 박탈
        console.log(`미디어 제어 : ${constInfo.team}, false`)
        handleMediaBySystem(false);
      } else if (constInfo.team == team[0].english) { // teamA 중 현재 순서만 권한 부여, 나머지 teamB와 동일
        idx = Math.floor(sessionInfo.phase / 4) % playerInfo.playerLeftList.length;
        if (constInfo.memberId == playerInfo.playerLeftList[idx].memberId) {
          console.log(`미디어 제어 : ${constInfo.team}, true`)
          handleMediaBySystem(true);
        } else {
          console.log(`미디어 제어 : ${constInfo.team}, false`)
          handleMediaBySystem(false);
        }
      }
      break;
    case 2:   // QnA
      console.log(playerInfo)
      if (constInfo.team == team[0].english) {
        idx = Math.floor(sessionInfo.phase / 4) % playerInfo.playerLeftList.length;
        if (constInfo.memberId == playerInfo.playerLeftList[idx].memberId) { handleMediaBySystem(true); }
        else { handleMediaBySystem(false) }
      } else if (constInfo.team == team[1].english) {
        idx = Math.floor(sessionInfo.phase / 4) % playerInfo.playerRightList.length;
        if (constInfo.memberId == playerInfo.playerRightList[idx].memberId) { handleMediaBySystem(true); }
        else { handleMediaBySystem(false) }
      }
      break;
    case 3:   // 팀 B
      console.log(playerInfo)
      if (constInfo.team == team[0].english) {        // teamA는 전부 음소거 및 권한 박탈
        console.log(`미디어 제어 : ${constInfo.team}, false`)
        handleMediaBySystem(false);
      } else if (constInfo.team == team[1].english) { // teamB 중 현재 순서만 권한 부여, 나머지 teamA와 동일
        idx = Math.floor(sessionInfo.phase / 4) % playerInfo.playerRightList.length;
        if (constInfo.memberId == playerInfo.playerRightList[idx].memberId) {
          console.log(`미디어 제어 : ${constInfo.team}, true`)
          handleMediaBySystem(true);
        } else {
          console.log(`미디어 제어 : ${constInfo.team}, false`)
          handleMediaBySystem(false);
        }
      }
      break;
    default:  // QnA2
      console.log(playerInfo)
      if (constInfo.team == team[0].english) {
        idx = Math.floor(sessionInfo.phase / 4) % playerInfo.playerLeftList.length;
        if (constInfo.memberId == playerInfo.playerLeftList[idx].memberId) { handleMediaBySystem(true); }
        else { handleMediaBySystem(false) }
      } else if (constInfo.team == team[1].english) {
        idx = Math.floor((sessionInfo.phase / 4 - 1)) % playerInfo.playerRightList.length;
        if (constInfo.memberId == playerInfo.playerRightList[idx].memberId) { handleMediaBySystem(true); }
        else { handleMediaBySystem(false) }
      }
  }
}

// 팀A, 팀B 리스트 내 데이터 제거
function leaveTeam(streamManager) {
  let idx

  // 팀 A 내 제거 시도
  if (playerInfo.team == team[0].english) {
    idx = sessionInfo.teamLeftList.indexOf(streamManager, 0)
    if (idx >= 0) {
      console.log('Leave B :', playerInfo.playerLeftList[idx])
      sessionInfo.teamLeftList.splice(idx, 1)
      playerInfo.playerLeftList.splice(idx, 1)
    }
    return
  }

  // 팀 B 내 제거 시도
  if (playerInfo.team == team[1].english) {
    idx = sessionInfo.teamRightList.indexOf(streamManager, 0)
    if (idx >= 0) {
      console.log('Leave A :', playerInfo.playerRightList[idx])
      sessionInfo.teamRightList.splice(idx, 1)
      playerInfo.playerRightList.splice(idx, 1)
    }
    return
  }

  // juror 제거
  if (playerInfo.team == team[2].english) {
    for (let i = 0; i < playerInfo.jurorList.length; i++) {
      if (playerInfo.jurorList[i].memberId == constInfo.memberId) {
        console.log('Leave Juror :', playerInfo.jurorList[i])
        playerInfo.jurorList.splice(idx, 1)
        return
      }
    }
  }

  // watchers 제거
  if (playerInfo.team == team[3].english) {
    for (let i = 0; i < playerInfo.watcherList.length; i++) {
      if (playerInfo.watcher[i].memberId == constInfo.memberId) {
        console.log('Leave Juror :', playerInfo.watcherList[i])
        playerInfo.watcherList.splice(idx, 1)
        return
      }
    }
  }
}

// 소속 팀에 따라 화상 기기 설정 초기화
function initCommunication(targetTeam) {
  if (targetTeam == team[0].english || targetTeam == team[1].english) {
    communication.isMicOn = true
    communication.isCameraOn = true
    communication.isShareOn = true
    communication.isMicHandleAvailable = true
    communication.isCameraHandleAvailable = true
    communication.isShareHandleAvailable = false
  } else {
    communication.isMicOn = false
    communication.isCameraOn = false
    communication.isShareOn = false
    communication.isMicHandleAvailable = false
    communication.isCameraHandleAvailable = false
    communication.isShareHandleAvailable = false
  }
}

// 비디오 기본 설정 반환
function getDefaultPublisher() {
  return sessionInfo.OV.initPublisher(undefined, {
    audioSource: undefined, // 오디오 => undefined면 microphone, false/null이면 사용 X
    videoSource: undefined, // 비디오 => 기본 webcam, false/null이면 사용 X
    publishAudio: communication.isMicOn, // 시작 오디오 상태(ON/OFF)
    publishVideo: communication.isCameraOn, // 시작 비디오 상태(ON/OFF)
    resolution: '311x170', // 카메라 해상도
    frameRate: 30, // 카메라 프레임
    insertMode: 'APPEND', // 비디오가 추가되는 방식
    mirror: false // 비디오 거울 모드 X
  })
}

// 세션 나가기 메서드
function leaveSession() {
  if (constInfo.team == team[2].english) sendJurorOutMessage()
  else if (constInfo.team == team[3].english) sendWatcherOutMessage()

  // 세션 연결 종료
  if (sessionInfo.session) {
    sessionInfo.session.unpublish(sessionInfo.publisher);
    sessionInfo.session.disconnect()
  }
  // server redis에서 참여자 정보 삭제Leave
  debateStore.leaveDebate({
    sessionId: route.params.sessionId,
    nickName: constInfo.nickname,
    role: constInfo.team
  })

  // 세션 정보 초기화
  sessionInfo.session = undefined
  sessionInfo.OV = undefined
  sessionInfo.debateId = route.params.debateId
  sessionInfo.publisher = undefined // 자기 자신
  sessionInfo.teamLeftList = [] // 팀 A 리스트(자기 자신 포함 가능)
  sessionInfo.teamRightList = [] // 팀 B 리스트(자기 자신 포함 가능)

  // 플레이어 정보 초기화
  playerInfo.playerLeftList = []
  playerInfo.playerRightList = []

  // 통신 정보 초기화
  communication.micList = []
  communication.cameraList = []
  communication.selectedMic = ''
  communication.selectedCamera = []
  communication.isMicOn = false
  communication.isCameraOn = false
  communication.isShareOn = false
  communication.isMicHandleAvailable = false
  communication.isCameraHandleAvailable = false
  communication.isShareHandleAvailable = false

  // 메시지 정보 초기화
  // chatting.targetTeam= team[4].english
  chatting.messagesLeft = []
  chatting.messagesRight = []
  chatting.messagesAll = []

  // 윈도우 종료 시 세션 나가기 이벤트 삭제
  window.removeEventListener('beforeunload', leaveSession)
}

// 시작하기 message 수신
const showModal = ref(false)
const eventSource = ref(null)
const listening = ref(false)

// === 투표 메서드 ===
// 팀 투표 메서드
function sendVoteTeamMessage(event, team, beforeTeam, targetTeam) {
  event.preventDefault()

  // 전원에게 시그널
  sessionInfo.session.signal({
    // 메시지 데이터를 문자열로 변환해서 전송
    data: JSON.stringify({
      beforeTeam: beforeTeam,
      targetTeam: targetTeam,
      team: team
    }),
    type: 'voteTeam' // 신호 타입을 'chat'으로 설정
  })
}

// === 배심원, 관전자 ===
// 배심원 합류 메시지 송신 메서드
function sendJurorInMessage() {
  console.log('call sendJurorInMessage!')

  // event.preventDefault()
  sessionInfo.session.signal({
    data: JSON.stringify({
      memberId: constInfo.memberId,
      nickname: constInfo.nickname
    }),
    type: 'jurorIn'
  })
}

// 관전자 합류 메시지 송신 메서드
function sendWatcherInMessage() {
  console.log('call sendWatcherInMessage!')

  // event.preventDefault()
  sessionInfo.session.signal({
    data: JSON.stringify({
      memberId: constInfo.memberId,
      nickname: constInfo.nickname
    }),
    type: 'watcherIn'
  })
}

// 배심원 나감 메시지 송신 메서드
function sendJurorOutMessage() {
  console.log('call sendJurorOutMessage!')

  // event.preventDefault()
  sessionInfo.session.signal({
    data: JSON.stringify({
      memberId: constInfo.memberId
    }),
    type: 'jurorOut'
  })
}

// 관전자 나감 메시지 송신 메서드
function sendWatcherOutMessage() {
  console.log('call sendWatcherOutMessage!')

  // event.preventDefault()
  sessionInfo.session.signal({
    data: JSON.stringify({
      memberId: constInfo.memberId
    }),
    type: 'watcherOut'
  })
}

// === 채팅방 메서드 ===
// 채팅 보내기 메서드
function sendMessage(event, inputMessage, targetTeam) {
  event.preventDefault()
  if (inputMessage.trim()) {
    // 다른 참가원에게 메시지 전송하기
    sessionInfo.session.signal({
      // 메시지 데이터를 문자열로 변환해서 전송
      data: JSON.stringify({
        nickname: authStore.nickname,
        targetTeam: targetTeam,
        message: inputMessage
      }),
      type: 'chat' // 신호 타입을 'chat'으로 설정
    })
  }
}

// 사용자가 카메라 제어
function handleCameraByUser() {
  // 세션에 참가하지 않았으면 무시
  if (!sessionInfo.publisher || !communication.isCameraHandleAvailable) return

  // 비디오 상태 변경
  communication.isCameraOn = !communication.isCameraOn
  sessionInfo.publisher.publishVideo(communication.isCameraOn)
}

// 사용자가 마이크 제어
function handleMicByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!sessionInfo.publisher || !communication.isMicHandleAvailable) return

  // 마이크 상태 변경
  communication.isMicOn = !communication.isMicOn
  sessionInfo.publisher.publishAudio(communication.isMicOn)
}

function handleShareByUser() {
  // 세션에 참가하지 않은 경우 무시
  if (!sessionInfo.publisher) return

  console.log('handleShareByUser')
}

function handleMediaBySystem(isSpeaker) {
  // 발언자인 경우 => 오디오, 화면 공유 권한 부여
  if (isSpeaker) {
    console.log('마이크 킴')
    communication.isMicOn = true;
    communication.isMicHandleAvailable = true;
    communication.isShareHandleAvailable = true;
    sessionInfo.publisher.publishAudio(true);
  }

  // 발언자가 아닌 경우 => 마이크/화면 제어 권한 박탈 및 마이크 종료
  else {
    console.log('마이크 끔')
    communication.isMicOn = false;
    communication.isMicHandleAvailable = false;
    communication.isShareHandleAvailable = false;
    sessionInfo.publisher.publishAudio(false);
  }
}

// 게임 시작
function startGame() {
  sessionInfo.session
    .signal({
      type: 'startGame',
      data: '' // 필요한 경우 여기에 추가 데이터를 전달할 수 있습니다.
    })
    .then(() => {
      console.log('Game start signal sent successfully.')
      console.log('@@@@@@@@@@@@@페이즈확인' + sessionInfo.phase)
    })
    .catch((error) => {
      console.error('Error sending start game signal:', error)
    })
}
function nextPhase() {
  sessionInfo.session
    .signal({
      type: 'nextPhase',
      data: '' // 필요한 경우 여기에 추가 데이터를 전달할 수 있습니다.
    })
    .then(() => {
      console.log('next Phase signal sent successfully.')
      console.log('@@@@@@@@@@@@@페이즈확인' + sessionInfo.phase)
    })
    .catch((error) => {
      console.error('Error sending start game signal:', error)
    })
}

const isHost = computed(() => constInfo.roomInfo.hostName === constInfo.nickname)
console.log('방장이니?' + isHost.value)
const isPhaseZero = ref(true)
// phase에 따라서 함수 실행
watch(
  () => sessionInfo.phase,
  (newPhase) => {
    if (constInfo.roomInfo.hostName === authStore.nickname) {
      // 토론 시작했냐
      if(newPhase != 0) {isPhaseZero.value = false}
      else {isPhaseZero.value = true}

      if (newPhase === constInfo.roomInfo.playerNum * 4 + 1) {
        endGame()
      } else {
        if (newPhase % 2 === 1) {
          speakingTime()
        } else if (newPhase !== 0 && newPhase % 2 === 0) {
          qnaTime()
        }
      }
    }
  }
)

const isEnd = ref(false)

function endGame() {
  sessionInfo.session
    .signal({
      type: 'endGame',
      data: '' // 필요한 경우 여기에 추가 데이터를 전달할 수 있습니다.
    })
    .then(() => {
      console.log('@@@@@@@@@토론끝@@@@@@@@@@')
    })
    .catch((error) => {
      console.error('Error sending end game signal:', error)
    })
}

const canStart = ref(false)
watch(
  [() => playerInfo.playerLeftList, () => playerInfo.playerRightList, () => playerInfo.jurorList],
  ([leftList, rightList, jurorList]) => {
    if (
      leftList.length == constInfo.roomInfo.playerNum &&
      rightList.length == constInfo.roomInfo.playerNum &&
      jurorList.length == constInfo.roomInfo.jurorNum
    ) {
      canStart.value = true
    } else {
      canStart.value = false
    }
  },
  { deep: true } // deep 사용 객체 내부의 변화까지 감지
)

function speakingTime() {
  setTimeout(() => {
    console.log('nextphase 시작이야')
    nextPhase()
    console.log('발언시간 끝.')
  }, constInfo.roomInfo.speakingTime * 30)
}
function qnaTime() {
  setTimeout(() => {
    console.log('qnaTime 시작이야')
    nextPhase()
    console.log('질문시간 끝.')
  }, constInfo.roomInfo.qnaTime * 30)
}

joinSession()
</script>

<template>
  <div class="app-container">
    <header>
      <DebateHeaderBar
        :headerBarTitle="constInfo.roomInfo.title"
        :leave-session="leaveSession"
        :nowPhase="sessionInfo.phase"
      />
    </header>

    <div class="main-container">
      <!--  -->
      <div class="teamA-container">
        <div class="team-title">{{ constInfo.roomInfo.leftKeyword }}</div>
        <div class="players">
          <UserVideo
            v-for="sub in sessionInfo.teamLeftList"
            :key="sub.stream.connection.connectionId"
            :stream-manager="sub"
          />
          <div
            v-for="num in constInfo.roomInfo.playerNum - sessionInfo.teamLeftList.length"
            :key="num"
          >
            <div class="player">+</div>
          </div>
        </div>
      </div>

      <div class="share-container">
        <div class="play-button1" v-if="isHost && canStart&& !isPhaseZero" @click="startGame">시작하기</div>
        <div class="play-button2" v-else-if="isHost&& !isPhaseZero">시작하기</div>
        <div class="waiting-button" v-else-if="!isPhaseZero">대기중...</div>
        <div class="juror-button">
          배심원 : ({{ playerInfo.jurorList.length }} / {{ constInfo.roomInfo.jurorNum }})
        </div>
        <div class="viewer-button">관전자 : {{ playerInfo.watcherList.length }}</div>
      </div>

      <div class="teamB-container">
        <div class="team-title">{{ constInfo.roomInfo.rightKeyword }}</div>
        <div class="players">
          <UserVideo
            v-for="sub in sessionInfo.teamRightList"
            :key="sub.stream.connection.connectionId"
            :stream-manager="sub"
          />
          <div
            v-for="num in constInfo.roomInfo.playerNum - sessionInfo.teamRightList.length"
            :key="num"
          >
            <div class="player">+</div>
          </div>
        </div>
      </div>

      <!-- 채팅방 -->
      <ChattingBar
        :nickname="playerInfo.nickname"
        :role="constInfo.team"
        :messages-left="chatting.messagesLeft"
        :messages-right="chatting.messagesRight"
        :messages-all="chatting.messagesAll"
        @send-message="sendMessage"
      ></ChattingBar>
    </div>

    <GameStartModal v-if="showModal" :roomInfo="constInfo.roomInfo" />
    <VotingResult v-if="isEnd" :jurorVoteLeftNum="vote.jurorVoteLeftNum" :jurorVoteRightNum="vote.jurorVoteRightNum"/>

    <footer>
      <DebateBottomBar
        :team="constInfo.team"
        :is-mic-handle-available="communication.isMicHandleAvailable"
        :is-camera-handle-available="communication.isCameraHandleAvailable"
        :is-share-handle-available="communication.isShareHandleAvailable"
        :is-mic-on="communication.isMicOn"
        :is-camera-on="communication.isCameraOn"
        :is-share-on="communication.isShareOn"
        :handle-mic-by-user="handleMicByUser"
        :handle-camera-by-user="handleCameraByUser"
        :vote-left-num="vote.voteLeftNum"
        :vote-right-num="vote.voteRightNum"
        :juror-vote-left-num="vote.jurorVoteLeftNum"
        :juror-vote-right-num="vote.jurorVoteRightNum"
        @send-vote-team-message="sendVoteTeamMessage"
      />
    </footer>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  /* 전체 화면 높이를 차지하도록 설정 */
  margin: 0;
  /* 기본 마진 제거 */
}

.main-container {
  flex-grow: 1;
  /* header와 footer를 제외한 모든 공간을 차지 */
  display: flex;
  /* Flexbox 레이아웃 사용 */
  height: calc(100vh - 140px);
  padding-bottom: 70px;
}

.teamA-container,
.teamB-container,
.share-container,
.chatting-container {
  height: 100%;
}

.share-container {
  position: relative;
  flex: 4.5;
}

.play-button1,
.play-button2,
.waiting-button {
  position: absolute;
  /* 절대 위치 지정 */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  /* 중앙 정렬 */
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  top: 50%;
  width: 150px;
  height: 80px;
  background-color: #e8ebf9;
  color: #34227c;
  font-size: 30px;
  font-weight: bold;
}
.play-button1 {
  border: 5px solid red;
  cursor: pointer;
}

.juror-button,
.viewer-button {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  position: absolute;
  transform: translate(-50%, -50%);
  /* 중앙 정렬 */
  left: 50%;
  width: 300px;
  height: 40px;
  background-color: #f0eeee;
  font-size: 20px;
  font-weight: bold;
}

.juror-button {
  top: 70%;
}

.viewer-button {
  top: 80%;
}

.chatting-container {
  display: flex;
  flex-direction: column;
  max-height: 610px;
  border-left: 1px solid #ccc;
  box-shadow: -4px 0 5px -2px rgba(0, 0, 0, 0.2);
  overflow-y: auto;
}

.chatting-tabs {
  height: 10%;
  display: flex;
  justify-content: space-around;
  padding: 10px;
}

.chatting-team-tab,
.chatting-all-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  border-radius: 4px;
  background-color: #e8ebf9;
  color: #34227c;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 42%;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.chattings {
  flex: 8;
}

.send-message {
  flex: 1;
  height: 10%;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 5px;
}

.send-message img {
  height: 50%;
  object-fit: contain;
}

.styled-input {
  font-size: 16px;
  padding: 10px 20px;
  border: 2px solid #34227c;
  /* Adjust the color to match the image */
  border-radius: 25px;
  /* This gives the rounded corners */
  outline: none;
  /* Removes the default focus outline */
  width: 230px;
  /* Adjust the width as needed */
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  /* Adds a subtle shadow */
}

.teamA-container,
.teamB-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.team-title {
  height: 10%;
  text-align: center;
  line-height: 56.31px;
  background-color: #e8ebf9;
  color: #34227c;
  font-size: 20px;
  font-weight: bold;
}

.players {
  height: 90%;
  display: flex;
  flex-direction: column;
}

.player {
  flex: 1;
  border: 1px solid #34227c;
  background-color: #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #34227c;
  font-size: 30px;
  cursor: pointer;
}

footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
}

.form-group {
  margin-bottom: 1rem;
}
</style>
