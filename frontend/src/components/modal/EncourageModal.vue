<template>
    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <br>
        <div class="line1">토론이 종료되었습니다.</div>
        <div class="line2">
          <span style="font-weight: bold; font-size: larger;">{{ timeLeft }}초</span> 안에 투표를 완료해주세요.<br>
        </div>
        <div class="line3">미투표시 페널티가 부여됩니다.</div>
        <button class="btn-yes" @click="closeModal">네, 투표할게요!</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  
  const showModal = ref(true);
  const timeLeft = ref(10);
  
  onMounted(() => {
    const interval = setInterval(() => {
      if (timeLeft.value > 0) {
        timeLeft.value--;
      } else {
        showModal.value = false;
        clearInterval(interval);
      }
    }, 1000);
  });
  
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
  
  .btn-yes {
    background-color: #34227C;
    color: #E8EBF9;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    width: 350px;
    margin-top: 30px; 
    margin-bottom: 30px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 
  }
  
  .btn-yes:hover {
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
    font-size: medium;
  }
  
  .line2 {
    color: black;
    font-size: medium;
  }
  
  .line3 {
    color: darkgray;
    font-size: small;
  }
  </style>
  