<script setup>
import { ref } from "vue";

var openModal = ref(false)

</script>

<template>
    <button class="btn-open-modal">Modal열기</button>

    <div class="game-create-modal">
        <div class="game-create-modal-body">
            <h2>모달창 제목</h2>
            <h2>모달창 내용</h2>
        </div>
    </div>
</template>

<style scoped>
.game-create-modal {
    position: absolute;
    display: none;
    
    justify-content: center;
    background-color: #E5E5E5;
    
    top:0;
    left:0;

    width:100%;
    height:100%;
}

.game-create-modal-body {

    position:absolute;
    top:50%;
    text-align: center;

    width:400px;  
    height:600px; 
    padding:40px;  

    background-color: #FFFFFF;
    border-radius:10px; 
    box-shadow:0 2px 3px 0 rgba(34,36,38,0.15);

    /* 모듈창을 열었을 때 위치 설정 가운데로 */
    transform:translateY(-50%);
}

.btn-open-modal {

}
</style>