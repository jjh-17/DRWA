<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { QDialog, QIcon } from 'quasar';
import { useAuthStore } from '@/stores/useAuthStore';
import axios from 'axios';

// // 로그인 상태를 시뮬레이션하기 위한 ref. 실제 앱에서는 상태 관리 라이브러리나 props를 통해 관리될 수 있습니다.
// const isLoggedIn = ref(false);

// // 사용자 프로필 사진 URL. 로그인 한 사용자에 대한 정보입니다.
// const userProfilePic = 'https://cdn.quasar.dev/img/boy-avatar.png';

const authStore = useAuthStore();
const searchQuery = ref('');
const router = useRouter();

// 모달창 표시 여부 
const showDialog = ref(false);

/**
 * 소셜 로그인 URL을 가져오는 함수
 * @param {*} socialType 
 */
async function fetchSocialLoginUrl(socialType) {
    await authStore.fetchSocialLoginUrl(socialType);
}

/**
 * 로그아웃 함수
 */
const logout = () => {
    authStore.logout()
}

/**
 * 마이페이지로 이동하는 함수
 */
const goToMyPage = () => {
    router.push({ name: 'MyPageView' });
}



//방 검색
async function searchRooms(type) {
    if (!searchQuery.value.trim()) {
        console.warn('검색어를 입력해주세요.');
        return;
    }
    try {
        const response = await axios.get(`http://localhost:8080/search/${type}`, {
            params: { query: searchQuery.value }
        });
        router.push({ name: 'SearchResults', query: { type, query: searchQuery.value, rooms: JSON.stringify(response.data) } });
    } catch (error) {
        console.error('검색 중 오류 발생:', error);
    }
}

function onLogoClick() {
    router.push("/");
}

</script>

<template>
    <q-toolbar class="custom-toolbar">
        <!-- 메뉴 버튼 -->
        <!-- <q-btn flat round dense icon="menu" class="q-mr-sm" /> -->

        <!-- 로고 및 랭킹 버튼 -->
        <q-avatar class="q-mr-sm" @click="onLogoClick" style="cursor: pointer;">
            <img src="@\assets\img\logo.png">
        </q-avatar>
        <q-btn flat label="랭킹" class="text-white q-mr-sm" />


        <!-- 가운데 정렬을 위한 공간 배분 -->
        <q-space />

        <!-- 검색창 -->

        <q-input v-model="searchQuery" placeholder="검색어 입력" />
        <q-btn @click="searchRooms('title')" color="primary">제목 검색</q-btn>
        <q-btn @click="searchRooms('keyword')" color="primary">제시어 검색</q-btn>


        <!-- 로그인 상태에 따른 조건부 렌더링 -->
        <q-space /> <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->
        <div v-if="authStore.isLoggedIn">
            <q-btn flat round @click="menu = !menu">
                <q-avatar>
                    <img src=https://cdn.quasar.dev/img/avatar.png>
                </q-avatar>
            </q-btn>
            <q-menu v-model="menu" auto-close>
                <q-list>
                    <q-item clickable v-close-popup @click="goToUserProfile">
                        <q-item-section>UserID: {{ authStore.userId }}</q-item-section>
                    </q-item>
                    <q-item clickable v-close-popup @click="goToMyPage">
                        <q-item-section>마이페이지</q-item-section>
                    </q-item>
                    <q-item clickable v-close-popup @click="logout">
                        <q-item-section>로그아웃</q-item-section>
                    </q-item>
                </q-list>
            </q-menu>
        </div>
        <q-btn v-else flat label="로그인" class="text-white" @click="showDialog = true" />
        <!--로그인 모달 -->
        <q-dialog v-model="showDialog">
            <q-card class="q-pa-md bg-indigo-1 " style="width: 400px; max-width: 80vw; height: 35erd0px;">
                <q-card-section class="row items-center q-pb-none">
                    <q-space />
                    <q-btn icon="close" flat round dense @click="showDialog = false" />
                </q-card-section>

                <q-card-section class="q-pt-none flex flex-center column">
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
.custom-toolbar{
    background-color: #34227C;
    color: white;
    height:70px;
}
.search-container {
    flex: 1;
    max-width: 800px;
    /* 검색창의 최대 넓이를 조정합니다. */
}

.search-input {
    width: 100%;
}

.login-btn {
    width: 13rem;
    margin-bottom: 10px;
}
</style>
