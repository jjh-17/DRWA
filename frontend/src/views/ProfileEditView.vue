<script setup>
import { ref } from 'vue';

// 사용자가 입력한 닉네임을 저장할 ref
const nickname = ref('');

// 관심 카테고리 선택 상태를 관리할 ref
// 예시로 몇 가지 카테고리를 설정했습니다. 실제 카테고리 목록에 따라 변경해주세요.
const categories = ref([
    { name: '기술', selected: false },
    { name: '예술', selected: false },
    { name: '과학', selected: false },
]);

// 프로필 이미지를 관리할 ref
const profileImage = ref('');

// 닉네임 중복 확인 함수
function checkNickname() {
    // 중복 확인 로직 구현
    alert('중복 확인 로직을 구현하세요.');
}

// 관심 카테고리 선택/해제
function toggleCategory(category) {
    category.selected = !category.selected;
}

// 프로필 이미지 변경 핸들러
function onFileChange(event) {
    const files = event.target.files;
    if (files.length > 0) {
        const fileReader = new FileReader();
        fileReader.onload = (e) => {
            profileImage.value = e.target.result;
        };
        fileReader.readAsDataURL(files[0]);
    }
}

// 정보 수정 완료 핸들러
function submitProfile() {
    // 정보 수정 완료 로직 구현
    alert('정보 수정 완료 로직을 구현하세요.');
}
</script>

<template>
    <div class="profile-edit-container">
        <div class="title">정보 수정</div>
        <div class="subtitle">회원 가입에 필요한 정보를 입력해주세요.</div>

        <div class="field">이름</div>
        <div class="value">김싸피</div>

        <div class="field">닉네임</div>
        <div class="nickname-input">
            <input v-model="nickname" placeholder="닉네임을 입력해주세요" />
            <button @click="checkNickname">중복확인</button>
        </div>

        <div class="field">관심 카테고리</div>
        <div class="categories">
            <div v-for="category in categories" :key="category.name" class="category"
                :class="{ selected: category.selected }" @click="toggleCategory(category)">
                {{ category.name }}
            </div>
        </div>

        <div class="field">프로필 이미지(선택)</div>
        <div class="profile-image">
            <img v-if="profileImage" :src="profileImage" alt="프로필 이미지" />
            <input type="file" @change="onFileChange">
        </div>

        <button class="submit-button" @click="submitProfile">수정 완료</button>
    </div>
</template>

<style scoped>
.profile-edit-container {
    background-color: white;
    padding: 20px;
}

.title {
    color: purple;
    font-weight: bold;
    text-align: center;
}

.subtitle {
    color: grey;
    text-align: center;
}

.field {
    margin-top: 20px;
    font-weight: bold;
}

.value {
    margin-bottom: 10px;
}

.nickname-input,
.profile-image {
    display: flex;
    align-items: center;
    gap: 10px;
}

.categories {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.category {
    padding: 5px 10px;
    border: 1px solid transparent;
    cursor: pointer;
}

.category.selected {
    border-color: purple;
}

.submit-button {
    display: block;
    width: 100%;
    background-color: darkgray;
    color: white;
    padding: 10px;
    margin-top: 20px;
    cursor: pointer;
}
</style>
