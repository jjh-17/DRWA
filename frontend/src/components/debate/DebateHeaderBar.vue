<template>
  <q-toolbar class="custom-toolbar">
    <q-avatar class="q-mr-lg" @click="toggleInfoModal">
    <img src="@/assets/img/questionmark.png" class="avatar-img" />
  </q-avatar>
    <q-avatar class="q-mr-lg" @click="toggleReportModal">
      <img src="@/assets/img/exclamationmark.png" style="height: 100%" class="avatar-img" />
    </q-avatar>

    <!-- 가운데 정렬을 위한 공간 배분 -->
    <q-space />

    <!-- 현재 진행 상태 및 남은시간 -->
    <div class="q-pa-md q-ma-md text-center" style="font-size: 40px;">
      {{ headerBarTitle }}  {{ phase[props.nowPhase].korean }}
    </div>

    <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
    <q-space />

    <q-avatar class="q-mr-lg" @click="toggleDebateCreateModal">
      <img src="@/assets/img/settingmark.png" style="height: 100%" class="avatar-img" />
    </q-avatar>
    <q-avatar class="exit" @click="toggleExitModal">
      <img src="@/assets/img/exit.png" style="height: 100%" class="avatar-img" />
    </q-avatar>
    <InfoModal v-if="showInfoModal" @close="showInfoModal = false" />
    <ReportModal v-if="showReportModal" @close="showReportModal = false" />
    <ExitModal v-if="showExitModal" @close="showExitModal = false" :leaveSession="props.leaveSession" />
    <DebateCreateModal
      v-if="showDebateCreateModal"
      :isVisible="showDebateCreateModal"
      :disableOptions="true"
      @update:isVisible="showDebateCreateModal = $event"
/>




  </q-toolbar>
</template>

<script setup>
import { ref } from 'vue'
import InfoModal from '@/components/modal/InfoModal.vue'
import ReportModal from '@/components/modal/ReportModal.vue'
import ExitModal from '@/components/modal/ExitModal.vue'
import DebateCreateModal from '@/components/modal/DebateCreateModal.vue'
import { phase } from '../common/phase'


const showInfoModal = ref(false)
const showReportModal = ref(false)
const showExitModal = ref(false)
const showDebateCreateModal = ref(false)

const props = defineProps({
  headerBarTitle: String,
  leaveSession: Function,
  nowPhase: Number,
})

const headerBarTitle = props.headerBarTitle


function toggleInfoModal() {
  showInfoModal.value = !showInfoModal.value
}


function toggleReportModal() {
  showReportModal.value = !showReportModal.value
}

function toggleExitModal() {
  showExitModal.value = !showExitModal.value
}

function toggleDebateCreateModal() {
  console.log('toggleDebateCreateModal called');
  showDebateCreateModal.value = !showDebateCreateModal.value;
}

</script>

<style scoped>
.custom-toolbar {
  background-color: #34227c;
  color: white;
  height: 70px;
}

.exit {
  margin-right: 30px;
}

.q-avatar {
  margin-left: 30px;
  margin-right: 0px;
}

.avatar-img {
  height: 100%;
}</style>
