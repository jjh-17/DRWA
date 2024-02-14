
<template>
  <q-dialog v-model="showModal" persistent @hide="resetForm">
    <q-card class="reportmodal">
      <q-card-section class="text-center">
          <br>
        <q-card-title class="text-h6">유저 신고하기</q-card-title>
      </q-card-section>
      <q-card-section>
        <q-select
          v-model="selectedUser"
          :options="userOptions"
          label="유저 선택"
          emit-value
          map-options
          style="width: 100%;"
        />
        <br>
        <q-card-section class="report-label">신고 사유</q-card-section>
        <div class="reason">
          <q-checkbox v-model="reportReasons" val="욕설 및 편파적 언행" label="욕설 및 편파적 언행" />
          <q-checkbox v-model="reportReasons" val="토론에서 나가거나 자리 비움" label="토론에서 나가거나 자리 비움" />
          <q-checkbox v-model="reportReasons" val="고의적으로 한 팀의 편을 들음" label="고의적으로 한 팀의 편을 들음" />
          <q-checkbox v-model="reportReasons" val="공격적이거나 부적절한 닉네임 사용" label="공격적이거나 부적절한 닉네임 사용" />
          <q-checkbox v-model="reportReasons" val="부적절한 자료 화면 사용" label="부적절한 자료 화면 사용" />
        </div>
        <q-input
          v-model="details"
          label="상세 사유"
          type="textarea"
          rows="4"
          hint="유저가 어떤 행동을 했는지 자세히 설명해주세요."
          style="width: 100%;"
        />
      </q-card-section>
      <q-card-actions align="center">
        <q-btn label="취소" class="btn1" @click="closeModal" />
        <q-btn label="제출하기" class="btn2" @click="submitReport" :disable="!selectedUser || reportReasons.length === 0" />
      </q-card-actions>
      <br>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const showModal = ref(true);
const selectedUser = ref(null);
const userOptions = ref([]);

const resetForm = () => {
  selectedUser.value = null;
};

const submitReport = async () => {
try {
  await axios.post('/api/member/reportUser', { userId: selectedUser.value });
  showModal.value = false;
  alert('제출이 성공적으로 완료되었습니다.');
} catch (error) {
  console.error('신고 제출 중 에러 발생:', error);
  alert('신고 제출에 실패했습니다.');
}
};

const closeModal = () => {
  showModal.value = false;
};

onMounted(async () => {
try {
  const response = await axios.get('참가자 정보 받아오는 api');
  userOptions.value = response.data;
} catch (error) {
  console.error('사용자 목록을 불러오는 중 에러 발생:', error);
}
});
</script>

<style scoped>
.q-card-section {
  padding: 0.5rem 1rem;
}
.text-h6{
  font-weight: bold;
}
.q-card-actions {
  justify-content: center;
}

.report-label {
  padding: 0;
  color: gray;
  font-size: medium;
  font-weight: bold; 
}

.reason{
  font-size:small;
  display:flex;
  flex-direction: column;
}
.reportmodal {
  width: 400px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
.btn1, .btn2 {
  background-color: #34227C;
  color: #E8EBF9;
  margin-top:10px;
  margin-bottom:10px;
}
</style>
