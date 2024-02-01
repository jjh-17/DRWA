<script setup>
import { ref } from 'vue';
import { QDialog, QIcon } from 'quasar';

// 로그인 상태를 시뮬레이션하기 위한 ref. 실제 앱에서는 상태 관리 라이브러리나 props를 통해 관리될 수 있습니다.
const isLoggedIn = ref(false);

// 사용자 프로필 사진 URL. 로그인 한 사용자에 대한 정보입니다.
const userProfilePic = 'https://cdn.quasar.dev/img/boy-avatar.png';

// 모달창 표시 여부 
const showDialog = ref(false);
</script>

<template>
    <q-toolbar class="bg-purple glossy text-white">
        <!-- 메뉴 버튼 -->
        <q-btn flat round dense icon="menu" class="q-mr-sm" />

        <!-- 로고 및 랭킹 버튼 -->
        <q-avatar class="q-mr-sm">
            <img src="https://cdn.quasar.dev/logo-v2/svg/logo-mono-white.svg">
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
        <div v-if="isLoggedIn">
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
                    <q-btn color="red" icon="favorite" label="구글 로그인" class="full-width q-mb-md" />
                    <q-btn color="green" icon="forest" label="네이버 로그인" class="full-width q-mb-md" />
                    <q-btn color="yellow" icon="emoji_people" label="카카오 로그인" class="full-width" />
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
</style>
