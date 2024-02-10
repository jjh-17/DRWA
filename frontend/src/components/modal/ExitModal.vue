<template>
    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <br>
        <div class="line1">정말로 나가시겠습니까?</div>
        <div class="line2">방 이탈시 페널티가 부여됩니다.</div>
        <div class="btn">
          <button class="btn-yes" @click="closeModal">예</button>
          <button class="btn-no" @click="closeModal">아니요</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const showModal = ref(true);
  
  const closeModal = () => {
    showModal.value = false;
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
  margin-bottom: 20px; 
}

.btn {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  margin-bottom: 30px;
}

.btn-yes {
  background-color: #34227C;
  color: #E8EBF9;
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
  background-color: #E8EBF9;
  color: #34227C;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 150px;
  margin-right:70px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 
}

.btn-no:hover {
  background-color: #28054b;
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
  margin-top:10px;
}

.line2 {
  color: darkgray;
  font-size: small;
}
</style>
  