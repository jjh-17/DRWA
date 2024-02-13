<template>
  <q-toolbar class="custom-toolbar">
    <!-- 
      참가자인 경우에만 화상 아이콘이 보임
      사용 권한이 있는 경우에만 클릭을 가능하게 함
    -->
    <q-avatar v-if="props.team == team[0].english || props.team == team[1].english" class="q-mr-lg">
      <img
        v-if="props.isMicOn"
        src="@/assets/img/micon.png"
        style="height: 100%"
        class="avatar-img"
        @click="props.handleMicByUser"
      />
      <img
        v-else
        src="@/assets/img/micoff.png"
        :aria-disabled="!props.handleMicByUser"
        style="height: 100%"
        class="avatar-img"
        @click="props.handleMicByUser"
      />
    </q-avatar>
    <q-avatar v-if="props.team == team[0].english || props.team == team[1].english" class="q-mr-lg">
      <img
        v-if="props.isCameraOn"
        src="@/assets/img/cameraon.png"
        style="height: 100%"
        class="avatar-img"
        @click="props.handleCameraByUser"
      />
      <img
        v-else
        src="@/assets/img/cameraoff.png"
        v-bind:aria-disabled="!props.isCameraHandleAvailable"
        style="height: 100%"
        class="avatar-img"
        @click="props.handleCameraByUser"
      />
    </q-avatar>
    <q-avatar v-if="props.team == team[0].english || props.team == team[1].english" class="q-mr-lg">
      <img
        v-if="props.isShareOn"
        src="@/assets/img/shareon.png"
        style="height: 100%"
        class="avatar-img"
        @click="props.handleShareByUser"
      />
      <img
        v-else
        src="@/assets/img/shareoff.png"
        v-bind:aria-disabled="!props.isShareHandleAvailable"
        style="height: 100%"
        class="avatar-img"
        @click="props.handleShareByUser"
      />
    </q-avatar>

    <!-- 가운데 정렬을 위한 공간 배분 -->
    <q-space />
    <!-- 투표 -->
    <div class="vote-container">
      <div class="label">지지율</div>
      <div class="bar-container">
        <div
          class="barA"
          :class="{ selected: selectedBar === team[0].english }"
          @click="(event) => vote(event, team[0].english)"
          :style="{ width: getBarWidth(supportRateA) }"
        >
          A : {{ supportRateA }}%
        </div>
        <div
          class="barB"
          :class="{ selected: selectedBar === team[1].english }"
          @click="(event) => vote(event, team[1].english)"
          :style="{ width: getBarWidth(supportRateB) }"
        >
          B : {{ supportRateB }}%
        </div>
      </div>
    </div>

    <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
    <q-space />

    <q-btn
      v-if="props.team == team[0].english || props.team == team[1].english"
      class="end-speech"
      label="발언 종료"
    />
  </q-toolbar>
</template>

<script setup>
import { team } from '@/components/common/Team.js'
import { ref, defineProps, defineEmits, computed } from 'vue'

// 전달 받을 정보
const props = defineProps({
  isMicHandleAvailable: Boolean,
  isCameraHandleAvailable: Boolean,
  isShareHandleAvailable: Boolean,
  isMicOn: Boolean,
  isCameraOn: Boolean,
  isShareOn: Boolean,
  handleMicByUser: Function,
  handleCameraByUser: Function,
  handleShareByUser: Function,

  team: String,

  voteLeftNum: Number,
  voteRightNum: Number,
  jurorVoteLeftNum: Number,
  jurorVoteRightNum: Number,
})

const emit = defineEmits(['sendVoteTeamMessage'])


// 배심원 투표인지 저장
const selectedBar = ref('')

const supportRateA = computed(() => {
  const totalVotes = props.voteLeftNum + props.voteRightNum
  return totalVotes > 0 ? Math.round((props.voteLeftNum / totalVotes) * 100) : 50
})

const supportRateB = computed(() => {
  const totalVotes = props.voteLeftNum + props.voteRightNum
  return totalVotes > 0 ? Math.round((props.voteRightNum / totalVotes) * 100) : 50
})

function getBarWidth(rate) {
  return `calc(${rate}% + 80px)`; // 최소 80px + 지지율 비율
}


// 배심원 투표인지 아닌지도 구분해놓음.
function vote(event, bar) {
  // 다른 바를 선택했을 때만 동작
  if (selectedBar.value !== bar) {
    emit('sendVoteTeamMessage', event, props.team, selectedBar.value, bar)
    selectedBar.value = bar;
  }
}



console.log(props.team)
</script>

<style scoped>
.custom-toolbar {
  background-color: #34227c;
  color: white;
  height: 70px;
}

.q-avatar {
  margin-left: 30px;
  margin-right: 0px;
}

.avatar-img {
  height: 100%;
  /* 이미지 높이를 부모 요소 높이에 맞춤 */
}

.end-speech {
  background-color: #e8ebf9;
  color: #34227c;
  width: 200px;
  height: 50px;
  border-radius: 4px;
  font-size: 20px;
  margin-right: 30px;
}

.vote-container {
  text-align: center;
  background-color: #34227c;
  color: white;
  padding: 10px;
  height: 100%;
  width: 50%;
}

.label {
  margin-bottom: 10px;
}

.bar-container {
  display: flex;
  background-color: white;
  padding: 5px;
}

.barA, .barB {
  cursor: pointer;
  transition: width 0.5s ease-in-out;
  background-color: #6247aa;
  color: white;
  text-align: center;
  margin-right: 2px; /* 바 사이의 간격 */
  min-width: 20px; /* 최소 너비 설정 */
}

.selected {
  border: 4px solid red; /* 선택된 바의 테두리 */
}
</style>