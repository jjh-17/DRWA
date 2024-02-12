<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { team } from '@/components/common/Team.js'
import { Event } from "openvidu-browser";

// === 부모 ===
const props = defineProps({
  nickname: String,
  role: String,
  messagesLeft: [],
  messagesRight: [],
  messagesAll: [],
});
const emit = defineEmits([
  'sendMessage'
])

// === 변수 선언 ===
// 채팅
const targetTeam = ref("");
const inputMessage = ref("");

// 메서드
function sendMessageAndClear() {
  emit('sendMessage', event, inputMessage.value);
  inputMessage.value = '';
}

</script>

<template>
  <div id="chat-container">
    <div id="chat-window">
      <!-- 전체 채팅방 -->
      <ul id="chat-history">
        <h4>전체 채팅방</h4>
        <li v-for="(message, index) in props.messagesAll" :key="index">
          <strong>{{message.username}}:</strong> {{message.message}}
        </li>
      </ul>
      <!-- 팀A 방 -->
      <ul v-if="props.role=='teamA'" id="chat-history">
        <h4>팀A 채팅방</h4>
        <li v-for="(message, index) in props.messagesLeft" :key="index">
          <strong>{{message.username}}:</strong> {{message.message}}
        </li>
      </ul>
      <!-- 팀B 방 -->
      <ul v-if="props.role=='teamB'" id="chat-history">
        <h4>팀B 채팅방</h4>
        <li v-for="(message, index) in props.messagesRight" :key="index">
          <strong>{{message.username}}:</strong> {{message.message}}
        </li>
      </ul>
    </div>

    <!-- 메시지 작성 -->
    <form id="chat-write">
      <label for="targetTeam">목표 채팅창</label>
        <select id="targetTeam" v-model="targetTeam">
          <option>{{ team[3].english }}</option>
          <option v-if="props.role=='teamA'">{{ team[0].english }}</option>
          <option v-if="props.role=='teamB'">{{ team[1].english }}</option>
        </select>
      <input type="text" placeholder="전달할 내용을 입력하세요." v-model="inputMessage">
      <button @click="sendMessageAndClear">전송</button>
    </form>
  </div>
</template>

<style scoped>

</style>