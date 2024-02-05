<script setup>
import { ref } from 'vue';
import { QDialog, QIcon } from 'quasar';
import { useAuthStore } from '@/stores/auth';

// // 로그인 상태를 시뮬레이션하기 위한 ref. 실제 앱에서는 상태 관리 라이브러리나 props를 통해 관리될 수 있습니다.
// const isLoggedIn = ref(false);

// // 사용자 프로필 사진 URL. 로그인 한 사용자에 대한 정보입니다.
// const userProfilePic = 'https://cdn.quasar.dev/img/boy-avatar.png';

const authStore = useAuthStore();

// 모달창 표시 여부 
const showDialog = ref(false);

/**
 * 소셜 로그인 URL을 가져오는 함수
 * @param {*} socialType 
 */
async function fetchSocialLoginUrl(socialType) {
    await authStore.fetchSocialLoginUrl(socialType);
}

</script>

<template>
    <q-toolbar class="bg-purple glossy text-white">
        <!-- 메뉴 버튼 -->
        <!-- <q-btn flat round dense icon="menu" class="q-mr-sm" /> -->

        <!-- 로고 및 랭킹 버튼 -->
        <q-avatar class="q-mr-sm">
            <img src="src\assets\img\logo.png">
        </q-avatar>
        <q-btn flat label="랭킹" class="text-white q-mr-sm" />


        <!-- 가운데 정렬을 위한 공간 배분 -->
        <q-space />

        <!-- 검색창 -->
        <div class="search-container">
            <q-input color="orange-12" bg-color="white" rounded outlined dense v-model="search" placeholder="검색..."
                class="search-input" light />
        </div>

        <!-- 로그인 상태에 따른 조건부 렌더링 -->
        <q-space /> <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
        <div v-if="authStore.isLoggedIn">
            <q-avatar>
                <img :src="userProfilePic">
            </q-avatar>
        </div>
        <q-btn v-else flat label="로그인" class="text-white" @click="showDialog = true" />
        <!--로그인 모달 -->
        <q-dialog v-model="showDialog">
            <q-card class="q-pa-md" style="width: 300px; max-width: 80vw;">
                <q-card-section class="row items-center q-pb-none">
                    <q-space />
                    <q-btn icon="close" flat round dense @click="showDialog = false" />
                </q-card-section>

                <q-card-section class="q-pt-none">
                    <div class="text-h6 text-center q-mb-md">소셜 로그인</div>
                    <img class="login-btn" src="src\assets\img\google_login_btn.png" @click="fetchSocialLoginUrl('google')">
                    <img class="login-btn" src="src\assets\img\naver_login_btn.png" @click="fetchSocialLoginUrl('naver')">
                    <img class="login-btn" src="src\assets\img\kakao_login_btn.png" @click="fetchSocialLoginUrl('kakao')">
                </q-card-section>
            </q-card>
        </q-dialog>
    </q-toolbar>
</template>

<style scoped>
.search-container {
    flex: 1;
    max-width: 800px;
    /* 검색창의 최대 넓이를 조정합니다. */
}

.search-input {
    width: 100%;
}

.login-btn {
    width: 100%;
    margin-bottom: 10px;
}
</style>
