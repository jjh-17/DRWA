<template>
    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <div class="line1"><span style="font-weight: bold; font-size: larger; color: red;">{{ timeLeft }}초</span> 후 팀 별 준비 방으로 이동합니다.</div>
          <div class="line2">3분 후 토론이 시작됩니다. 팀원들과 완벽한 준비를 해보세요!</div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  
  const showModal = ref(true);
  const timeLeft = ref(5);
  
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
      font-weight:bold;
    }
    
    .close {
      position: absolute;
      top: 10px;
      right: 10px;
      cursor: pointer;
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
    .line1{
      margin-top: 20px;
      margin-bottom:10px;
    }
    .line2{
      margin-bottom: 20px;
    }
    </style>
    