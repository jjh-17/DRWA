<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { team } from '@/components/common/Team.js'

// === 부모 ===
const props = defineProps({
  nickname: String,
  role: String,
  messagesLeft: [],
  messagesRight: [],
  messagesAll: [],
  sendMessage: Function,
});

const emit = defineEmits(['sendMessage'])
function sendMessage(event) {
  emit('sendMessage', event, inputMessage.value, targetTeam.value)
  inputMessage.value = ''
}

// === 변수 선언 ===
// 채팅
const targetTeam = ref(team[4].english);
const inputMessage = ref('');

</script>

<template>
  <div class="chatting-container">
    <!-- 채팅창 선택창  -->
    <div class="chatting-tabs">
      <div v-if="props.role==team[0].english || props.role==team[1].english" class="chatting-team-tab" :aria-readonly="true"
          @click="targetTeam = props.role"> 팀 채팅 </div>
      <div class="chatting-all-tab" :aria-readonly="true"
          @click="targetTeam = team[4].english"> 전체 채팅 </div>
    </div>

    <!-- 선택한 채팅창에 따른 채팅 목록 -->
    <div class="chattings">
      <!-- 팀 A 채팅 -->
      <ul v-if="targetTeam == team[0].english">
        <li v-for="(message, index) in props.messagesLeft" :key="index">
          <strong>{{message.nickname}}:</strong> {{message.message}}
        </li>
      </ul>

      <!-- 팀 B 채팅 -->
      <ul v-else-if="targetTeam == team[1].english">
        <li v-for="(message, index) in props.messagesRight" :key="index">
          <strong>{{message.nickname}}:</strong> {{message.message}}
        </li>
      </ul>

      <!-- 전체 채팅 -->
      <ul v-else>
        <li v-for="(message, index) in props.messagesAll" :key="index">
          <strong>{{message.nickname}}:</strong> {{message.message}}
        </li>
      </ul>

    </div>
    
    <!-- 채팅 보내기 -->
    <div class="send-message">
      <input type="text" placeholder="메시지 보내기" class="styled-input" v-model="inputMessage"/>
      <img src="@/assets/img/send.png" @click="(event) => sendMessage(event)"/>
    </div>
  </div>

</template>

<style scoped>
.chatting-container {
  display:flex;
  flex: 1.5;
  border-left: 1px solid #ccc;
  box-shadow: -4px 0 5px -2px rgba(0, 0, 0, 0.2); /* 왼쪽 그림자 설정 */
  flex-direction:column;
}
.chatting-tabs {
  height:10%;
  display:flex;
  justify-content: space-around;
  padding:10px;
}
.chatting-team-tab,
.chatting-all-tab {
  display:flex;
  align-items:center;
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
  flex:8;
}
.send-message {
  flex:1;
  height:10%;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 5px;
}
.send-message img {
  height:50%;
  object-fit: contain;
}
.styled-input {
  font-size: 16px;
  padding: 10px 20px;
  border: 2px solid #34227C; 
  border-radius: 25px; 
  outline: none; 
  width: 230px; 
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}
</style>