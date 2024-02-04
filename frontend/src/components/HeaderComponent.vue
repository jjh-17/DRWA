<script setup>
import { ref } from 'vue';

// 로그인 상태를 시뮬레이션하기 위한 ref. 실제 앱에서는 상태 관리 라이브러리나 props를 통해 관리될 수 있습니다.
const isLoggedIn = ref(false);

// 사용자 프로필 사진 URL. 로그인 한 사용자에 대한 정보입니다.
const userProfilePic = 'https://cdn.quasar.dev/img/boy-avatar.png';
</script>

<template>
    <q-toolbar class="header">
        <!-- 메뉴 버튼 -->
        <q-btn flat round dense icon="menu" class="q-mr-sm text-white" />

        <!-- 로고 및 랭킹 버튼 -->
        <q-avatar class="q-mr-sm">
            <img src="https://cdn.quasar.dev/logo-v2/svg/logo-mono-white.svg">
        </q-avatar>
        <q-btn flat label="랭킹" class="text-white q-mr-sm" />


        <!-- 가운데 정렬을 위한 공간 배분 -->
        <q-space />

        <!-- 검색창 -->
        <div class="search-container">
            <q-input color="orange-12" bg-color="white" rounded outlined dense v-model="search" placeholder=" 검색..."
                class="search-input" light />
        </div>


        <!-- 로그인 상태에 따른 조건부 렌더링 -->
        <q-space /> <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
        <template v-if="isLoggedIn">
            <q-avatar>
                <img :src="userProfilePic">
            </q-avatar>
        </template>
        <q-btn v-else flat label="로그인" class="text-white" />
    </q-toolbar>
</template>

<style scoped>
.header{
    background-color: #34227C;
}
.search-container {
    flex: 1;
    max-width: 800px;
    /* 검색창의 최대 넓이를 조정합니다. */
}

.search-input {
    width: 100%;
}
</style>
