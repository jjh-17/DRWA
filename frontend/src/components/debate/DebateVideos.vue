<script setup>
import { ref, defineProps, computed } from "vue";
import { useDebateStore } from "@/stores/useDebateStore";
import { useAuthStore } from "@/stores/useAuthStore";
import { useRoute } from "vue-router";
import { OpenVidu } from "openvidu-browser";
import UserVideo from "./UserVideo.vue";
import { team } from '@/components/common/Team.js'

// === 변수 ===
// 부모
const props = defineProps({
  teamLeftList: Array,
  teamRightList: Array,
  playerLeftList: Array,
  playerRightList: Array,
  playerNum: Number,
});

// Debate 정보
const route = useRoute()
const debateId = route.params.debateId
const debateStore = useDebateStore()

// User 정보
const authStore = useAuthStore();


// === 통신 관련 메서드 ===

</script>

<template>
  <!-- A팀 캠 -->
  <div id="teamA-container">
    <h3>A팀 캠</h3>
    <UserVideo
      v-for="sub in props.subscribersLeft"
      :key="sub.stream.connection.connectionId"
      :stream-manager="sub"
    />
  </div>
  
  <div id="teamA-container">
    <h3>B팀 캠</h3>
    <UserVideo
      v-for="sub in props.subscribersRight"
      :key="sub.stream.connection.connectionId"
      :stream-manager="sub"
    />
  </div>
</template>

<style scoped>

</style>@/components/common/Team.js