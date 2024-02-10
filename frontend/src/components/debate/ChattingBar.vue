<script setup>
import { ref, defineProps, defineEmits, defineExpose } from "vue";

// === 부모 ===
const props = defineProps({
  nickname: String,
  role: String,
});
defineEmits(['sendMessage']);

// === 변수 선언 ===
// 채팅
const targetTeam = ref("");
const inputMessage = ref("");
const messagesLeft = ref([]);
const messagesRight = ref([]);
const messagesAll = ref([]);

// === 메서드 정의 ===
function sendMessageToLeft(message) {
  messagesLeft.value.push(message);
}
function sendMessageToRight(message) {
  messagesRight.value.push(message);
}
function sendMessageToAll(message) {
  messagesAll.value.push(message);
}

defineExpose({
  sendMessageToAll, sendMessageToLeft, sendMessageToRight,
});

</script>

<template>
  <div id="chat-container">
      <div id="chat-window">
        <!-- 전체 채팅방 -->
        <ul id="chat-history">
          <hr>
          <li v-for="(message, index) in messagesAll" :key="index">
            <strong>{{message.username}}:</strong> {{message.message}}
          </li>
        </ul>

        <!-- 팀A 방 -->
        <ul v-if="props.role=='teamA'" id="chat-history">
          <hr>
          <li v-for="(message, index) in messagesLeft" :key="index">
            <strong>{{message.username}}:</strong> {{message.message}}
          </li>
        </ul>

        <!-- 팀B 방 -->
        <ul v-if="props.role=='teamB'" id="chat-history">
          <hr>
          <li v-for="(message, index) in messagesRight" :key="index">
            <strong>{{message.username}}:</strong> {{message.message}}
          </li>
        </ul>
      </div>

      <!-- 메시지 작성 -->
      <form id="chat-write">
        <label for="targetTeam">목표 채팅창</label>
          <select id="targetTeam" v-model="targetTeam">
            <option>전체</option>
            <option v-if="props.role=='teamA'">팀 A</option>
            <option v-if="props.role=='teamB'">팀 B</option>
          </select>
        <input type="text" placeholder="전달할 내용을 입력하세요." v-model="inputMessage">
        <button @click="sendMessage">전송</button>
      </form>
    </div>
</template>

<style scoped>

</style>