<template>
  <div v-if="showModal" class="modal">
    <div class="modal-content">
      <div class="modal-header">
        <h4>토론 종료</h4>
      </div>
      <div class="team-info">
        <div class="teamA">팀A <span class="result-win"></span></div>
        <div class="teamB">팀B <span class="result-lose"></span></div>
      </div>
      <div class="progress-bars">
          <div class="progress-bar">
          <div class="progress" style="width: 70%; background-color: green;"></div>
          <div class="progress" style="width: 30%; background-color: red; margin-left: 70%;"></div>
        </div>
      </div>
      <div class="points">
        <p>획득 포인트 : 10p</p>
      </div>
      <div class="modal-buttons">
        <button class="btnS" @click="stay">머물기</button>
        <button class="btnE" @click="leave">나가기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const showModal = ref(true);
const router = useRouter();

const stay = () => {
  showModal.value = false;
};

const leave = () => {
  router.push('/');
};
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
}

.modal-content {
  background-color: white;
  border-radius: 10px; 
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 
  padding: 20px;
  text-align: center;
  width: 500px;
}

.modal-header {
  margin-top: 30px;
  margin-bottom: 60px;
}

.team-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 50px;
  font-size: 30px;
  font-weight: bold;
}

.teamA {
  margin-left: 90px;
}
.teamB {
  margin-right: 90px;
}
.progress-bars {
  display: flex;
  justify-content: center; 
}

  .progress-bar {
  background-color: lightgray;
  border-radius: 5px;
  height: 20px;
  overflow: hidden;
  width: 400px; 
  }

  .progress {
  height: 100%;
  text-align: center;
  }


.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top:80px;
  margin-bottom: 50px;
}

.btnS {
  background-color: #34227C;
  color: #E8EBF9;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 150px;
  margin-left: 50px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);

}

.btnS:hover {
  background-color: #28054b;
}
.btnE {
  background-color: #E8EBF9;
  font-weight: bold;
  color: #34227C;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 150px;
  margin-right: 50px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.btnE:hover {
  background-color: #28054b;
}
</style>
