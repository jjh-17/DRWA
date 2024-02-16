<template>
  <div v-if="showModal" class="modal">
  <div class="container1">
    <div class="balloon1">신고하기</div>
  </div>
  <div class="container2">
    <div class="balloon2">팀 클릭시 팀 투표</div>
  </div>
  <div class="container3">
    <div class="balloon3">현재 지지율</div>
  </div>

  <div class="modal-content">
    <div class="modal-header">
      <h4>토론 흐름</h4>
    </div>
      <div class="progress-bars">
        <div class="progress-bar1">
            <div class="progress1" v-show="nowPhase >= 1" style="background-color: #e8ebf9;">팀 A 발언</div>
            <div class="progress2" v-show="nowPhase >= 2" style="background-color: #e8ebf9;">질의응답 1-1</div>
            <div class="progress3" v-show="nowPhase >= 3" style="background-color: #e8ebf9;">팀 B 발언1</div>
            <div class="progress4" v-show="nowPhase >= 4" style="background-color: #e8ebf9;">질의응답 1-2</div>
          </div>
          <div class="progress-bar2">
              <div class="progress5" v-show="nowPhase >= 5" style="width: 25%; background-color: #e8ebf9;">팀 A  발언2</div>
              <div class="progress6" v-show="nowPhase >= 6" style="width: 25%; background-color: #e8ebf9;">질의응답 2-1</div>
              <div class="progress7" v-show="nowPhase >= 7" style="width: 25%; background-color: #e8ebf9;">팀 B  발언1</div>
              <div class="progress8" v-show="nowPhase >= 8" style="width: 25%; background-color: #e8ebf9;">질의응답 2-2</div>
          </div>
          <div class="progress-bar3">
              <div class="progress9" v-show="nowPhase >= 9" style="width: 25%; background-color: #e8ebf9;">팀 A  발언3</div>
              <div class="progress10" v-show="nowPhase >= 10" style="width: 25%; background-color: #e8ebf9;">질의응답 3-1</div>
              <div class="progress11" v-show="nowPhase >= 11" style="width: 25%; background-color: #e8ebf9;">팀 B  발언3</div>
              <div class="progress12" v-show="nowPhase >= 12" style="width: 25%; background-color: #e8ebf9;">질의응답 3-2</div>
          </div>
      </div>
      <div class="modal-buttons">
        <button class="btnE" @click="$emit('close')">나가기</button>
      </div>
    </div>
  </div>
</template>

<script setup>



import { ref, defineProps } from 'vue';

const props = defineProps({
  nowPhase: Number
})

const showModal = ref(true);

</script>

<style scoped>

.balloon1, .balloon2, .balloon3 {
position: absolute;
padding: 10px; 
background-color: #E8EBF9; 
border-radius: 10px; 
box-shadow: 0 2px 4px rgba(0,0,0,0.2); 
z-index: 101;
font-weight:bold;
color: #34227C;
}
.balloon1 {
  top: 70px; 
left: 90px; 
}
.balloon2 {
bottom: 50px; 
left: 400px; 
}
.balloon3 {
bottom: 50px; 
right: 400px; 
}


.balloon3::after  {
content: '';
position: absolute;
top: 100%; 
left: 10px; 
border-width: 10px 10px 0 0; 
border-style: solid;
border-color: #f0f0f0 transparent transparent transparent; 
}
.balloon2::after{
content: '';
position: absolute;
top: 100%; 
right: 10px; 
border-width: 10px 0 0 10px ; 
border-style: solid;
border-color: #f0f0f0 transparent transparent transparent; 
}
.balloon1::after {
content: '';
position: absolute;
bottom: 100%; 
right: 10px;
border-width: 10px 0 0 10px;
border-style: solid;
border-color: transparent transparent transparent #f0f0f0; 
}

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
  z-index: 100;
}

.modal-content {
  background-color: white;
  border-radius: 10px; 
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 
  padding: 20px;
  text-align: center;
  width: 500px;
  height:430px;
}

.modal-header {
  margin-top: 30px;
  margin-bottom: 50px;
  z-index: 102;
  color:black;
  font-weight:bold;
}


.progress-bars {
display: flex;
justify-content: center;
flex-direction: column;
border-radius: 30px;
gap: 10px;
width:460px;
height: 160px;
}

.progress-bar1, .progress-bar2, .progress-bar3 {
display: flex;
background-color: white;
border-radius: 30px;
height:100%;
width: 460px;
border: 2px solid #34227C;
overflow: hidden;
padding: 3px;
}

.progress1, .progress2, .progress3, .progress4,
.progress5, .progress6, .progress7, .progress8,
.progress9, .progress10, .progress11, .progress12 {
display: flex;
justify-content: center;
align-items: center;
background-color: #e8ebf9;
border: 2px solid #34227C;
height: 100%;
margin: 0 2px;
flex-grow: 1;
font-weight: bold;
color: #34227C;
}

.progress1:first-child, 
.progress5:first-child, 
.progress9:first-child {
border-top-left-radius: 20px;
border-bottom-left-radius: 20px;
}

.progress4:last-child, 
.progress8:last-child, 
.progress12:last-child {
border-top-right-radius: 20px;
border-bottom-right-radius: 20px;
}


.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top:40px;
  margin-bottom: 30px;
  display:flex;
  justify-content:center;
}

.btnE {
  background-color: #34227C;
  font-weight: bold;
  color: #E8EBF9;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 150px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);

}

.btnE:hover {
  background-color: #28054b;
}
h4{
  font-weight:bold;
}
</style>
