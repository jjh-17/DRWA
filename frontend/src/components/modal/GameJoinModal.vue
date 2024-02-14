<template>
  <div v-if="showModal" class="modal">
    <div class="modal-content">
      <br />
      <div class="line1">역할을 선택하세요.</div>
      <div class="btn">
        <label class="radio-container"
          >팀A
          <input type="radio" name="role" value="teamA" v-model="selectedRole" />
          <span class="checkmark"></span>
        </label>
        <label class="radio-container"
          >팀B
          <input type="radio" name="role" value="teamB" v-model="selectedRole" />
          <span class="checkmark"></span>
        </label>
        <label class="radio-container"
          >배심원
          <input type="radio" name="role" value="juror" v-model="selectedRole" />
          <span class="checkmark"></span>
        </label>
        <label class="radio-container"
          >관전자
          <input type="radio" name="role" value="viewer" v-model="selectedRole" />
          <span class="checkmark"></span>
        </label>
      </div>
      <div class="btn2">
        <button class="btn-yes" @click="closeModal('confirm')">예</button>
        <button class="btn-no" @click="closeModal('cancel')">아니요</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import { useRouter } from 'vue-router';
import { httpService } from '@/api/axios'
import { useRoomInfo } from '@/stores/useRoomInfo'
import { useDebateStore } from "@/stores/useDebateStore";

const props = defineProps({
  room: Object
});
const { setRoomInfo } = useRoomInfo()
const router = useRouter()

const showModal = ref(true)
const selectedRole = ref('')
const debateStore = useDebateStore();

const closeModal = (action) => {
  if (action === 'confirm' && !selectedRole.value) {
    alert('역할을 선택해주세요.')
    return
  }
  if (action === 'confirm') {
    async (sessionId) => {
      try {
        // const response = await httpService.get(`/openvidu/session/${sessionId}`)
        const response = await debateStore.joinDebate(sessionId)
        console.log('연결 정보 응답:', response.data)

        // connectionId를 키로, debateInfoResponse를 값으로 사용하여 스토어 업데이트
        setRoomInfo(response.data.connection.connectionId, response.data.debateInfoResponse)

        router.push(`/debate/${sessionId}`)
      } catch (error) {
        console.error('연결 정보 가져오기 에러:', error)
      }
    }
  }

  console.log('Selected role:', selectedRole.value)
  // 선택된 역할로 서버에 join 요청 보내

  showModal.value = false
}
</script>

<style scoped>
.modal {
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index:100;
}

.modal-content {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  padding: 20px;
  text-align: center;
  width: 500px;
  margin-bottom: 20px;
}

.btn {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  margin-bottom: 30px;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-content {
  animation: modalFadeIn 0.3s ease-in-out;
}

.line1 {
  color: black;
  font-size: 20px;
  margin-top: 10px;
  margin-bottom: 20px;
}
.radio-container {
  display: block;
  position: relative;
  padding-left: 35px;
  margin-bottom: 12px;
  cursor: pointer;
  font-size: 22px;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}

/* 숨겨진 라디오 버튼을 숨김 */
.radio-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

/* 커스텀 라디오 버튼 */
.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  height: 25px;
  width: 25px;
  background-color: #eee;
  border-radius: 50%;
}

/* 라디오 버튼이 체크되었을 때, 커스텀 동그라미를 그림 */
.radio-container input:checked ~ .checkmark {
  background-color: #2196f3;
}

/* 커스텀 동그라미 안에 있는 체크 표시 생성 */
.checkmark:after {
  content: '';
  position: absolute;
  display: none;
}

/* 라디오 버튼이 체크되었을 때, 표시를 보여줌 */
.radio-container input:checked ~ .checkmark:after {
  display: block;
}

/* 체크 표시 스타일 */
.radio-container .checkmark:after {
  top: 9px;
  left: 9px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: white;
}
.btn2 {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  margin-bottom: 30px;
}

.btn-yes {
  background-color: #34227c;
  color: #e8ebf9;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 150px;
  margin-left: 70px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.btn-yes:hover {
  background-color: #28054b;
}

.btn-no {
  background-color: #e8ebf9;
  color: #34227c;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 150px;
  margin-right: 70px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.btn-no:hover {
  background-color: #28054b;
}
</style>
