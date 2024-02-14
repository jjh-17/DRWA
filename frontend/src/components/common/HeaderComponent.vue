<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/useAuthStore';
import { useRoomStore } from '@/stores/useRoomStore';

// // 로그인 상태를 시뮬레이션하기 위한 ref. 실제 앱에서는 상태 관리 라이브러리나 props를 통해 관리될 수 있습니다.
// const isLoggedIn = ref(false);

// // 사용자 프로필 사진 URL. 로그인 한 사용자에 대한 정보입니다.
// const userProfilePic = 'https://cdn.quasar.dev/img/boy-avatar.png';

const authStore = useAuthStore();
const searchQuery = ref('');
const router = useRouter();
const roomStore = useRoomStore();
const menu = ref(false);
const profileMenu = ref(false); // 마이페이지 메뉴 토글 상태
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

const goToRanking = () => {
    router.push({ name: 'RankingView' });
}

//방 검색
const type = ref('');
const cond = ref('검색 조건');

const setType = (newType) => {
    type.value = newType;
    cond.value = newType === 'title' ? '방 제목 ' : '방 제시어';
};

async function searchRooms() {
    if (!searchQuery.value.trim()) {
        console.warn('검색어를 입력해주세요.');
        return;
    }
    await roomStore.searchRooms(searchQuery.value, type.value);
    if (!type.value) {
        alert('검색 유형을 선택해주세요.');
        return;
    }

    // `type`과 `query`를 URL의 동적 세그먼트로 전달
    router.push({ name: 'SearchResults', params: { type: type.value, query: searchQuery.value } });
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
        <q-avatar class="logo" @click="onLogoClick" style="cursor: pointer;">
            <img src="@\assets\img\logo.png">
        </q-avatar>
        <q-btn flat label="랭킹" class="ranking" @click="goToRanking" />

        <!-- 가운데 정렬을 위한 공간 배분 -->
        <q-space />

        <!-- 검색창 -->
        <div class="search-container">
            <div class="search">
                <q-btn-dropdown v-model="menu" class="dropdown" :label="cond">
                    <q-list>
                        <q-item clickable v-close-popup @click="setType('title')">
                            <q-item-section>
                                <q-item-label>방 제목</q-item-label>
                            </q-item-section>
                        </q-item>
                        <q-item clickable v-close-popup @click="setType('keyword')">
                            <q-item-section>
                                <q-item-label>방 제시어</q-item-label>
                            </q-item-section>
                        </q-item>
                    </q-list>
                </q-btn-dropdown>
                <input v-model="searchQuery" class="input" placeholder="   검색어를 입력하세요">
                <q-btn label="검색" @click="searchRooms" />
            </div>
        </div>

        <!-- 로그인 상태에 따른 조건부 렌더링 -->
        <q-space /> <!-- 이것은 나머지 요소들을 오른쪽으로 밀어냅니다 -->

        <div v-if="authStore.isLoggedIn">
            <q-btn flat round @click="profileMenu = !profileMenu">
                <q-avatar>
                    <img v-if="authStore.profileImage" :src="authStore.profileImage" />
                    <img v-else src="https://cdn.quasar.dev/img/avatar.png" alt=" 디폴트 프로필 이미지" />
                </q-avatar>
            </q-btn>
            <q-menu auto-close>
                <q-list>
                    <q-item clickable v-close-popup>
                        <q-item-section>{{ authStore.nickname }}</q-item-section>
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
        <q-btn v-else flat label="로그인" class="login" @click="showDialog = true" />
        <!--로그인 모달 -->
        <q-dialog v-model="showDialog">
            <q-card class="q-pa-md bg-indigo-1 " style="width: 400px; max-width: 80vw; height: 35erd0px;">
                <q-card-section class="row items-center q-pb-none">
                    <q-space />
                    <q-btn icon="close" flat round dense @click="showDialog = false" />
                </q-card-section>

                <q-card-section class="q-pt-none flex flex-center column">
                    <div class="text-h6 text-center q-mb-md">소셜 로그인</div>
                    <img class="login-btn cursor-pointer" src="@\assets\img\google_login_btn.png"
                        @click="fetchSocialLoginUrl('google')">
                    <img class="login-btn cursor-pointer" src="@\assets\img\naver_login_btn.png"
                        @click="fetchSocialLoginUrl('naver')">
                    <img class="login-btn cursor-pointer" src="@\assets\img\kakao_login_btn.png"
                        @click="fetchSocialLoginUrl('kakao')">
                </q-card-section>
            </q-card>
        </q-dialog>
    </q-toolbar>
</template>

<style scoped>
.custom-toolbar {
    background-color: #34227C;
    color: white;
    height: 70px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.logo {
    margin-left: 40px;
    margin-right: 40px;
    width: 45px;
    height: 45px;
}

.-avatar .logo {
    padding: 0px 10px 0px 10px;
}

.ranking {
    font-color: white;
}

.text-white.logo_ranking {
    padding: 0px 10px 0px 10px;
}

.input {
    flex: 1;
    text-align: left;
    background-color: white;
    width: 450px;
    height: 40px;
}

.search {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 80px;
}

.search-container {
    flex: 1;
    max-width: 800px;
    /* 그림자 효과 */
    border-radius: 2px;
    /* 모서리 둥글게 만듦 */
    height: 35px;
}

/* placeholder의 위치를 조정합니다. */
.q-input input::placeholder {
    text-align: right;
    font-size: 1em;
    line-height: 35px;
    /* padding-top: 10px; 위쪽으로 조정 */
}

.q-btn-dropdown {
    color: white;
    width: 120px;
    background-color: #34227C;
}

.login-btn {
    width: 13rem;
    margin-bottom: 10px;
    position: relative;
}

.login {
    margin-right: 25px;
}
</style>
