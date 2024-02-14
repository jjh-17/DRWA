<script setup>
import { QPage, QCard, QCardSection, QSeparator, QAvatar } from 'quasar'
import HeaderComponent from '@/components/common/HeaderComponent.vue'
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'

const router = useRouter()
const authStore = useAuthStore()

// 이미지를 동적으로 불러오기 위한 함수
const loadImage = (path) => {
  return new URL(`../assets/${path}`, import.meta.url).href;
};

// 예시 기록 데이터
const gameRecords = ref([
  { word1: '초콜릿', word2: '사탕', result: '승', points: '10p', img1: loadImage('img/chocolate.png'), img2: loadImage('img/candy.png') },
  { word1: '책', word2: '영화', result: '패', points: '10p', img1: loadImage('img/book.png'), img2: loadImage('img/movie.png') },
  { word1: '커피', word2: '녹차', result: '승', points: '10p', img1: loadImage('img/coffee.png'), img2: loadImage('img/tea.png') },
  { word1: '바다', word2: '산', result: '패', points: '10p', img1: loadImage('img/beach.png'), img2: loadImage('img/mountain.png') },
  { word1: '고양이', word2: '강아지', result: '승', points: '10p', img1: loadImage('img/cat.png'), img2: loadImage('img/dog.png') },
]);

// 예시 칭호 데이터, 실제로는 DB에서 받아온 데이터를 사용하기
const titles = ref([
  { name: '논리왕' },
  { name: '청렴한 배심원' },
  { name: '매너왕' },
  // 추가 칭호들...
]);

const state = reactive({
  visibleCount: 3
});

const onViewMore = () => {
  state.visibleCount += 3; // 더보기 클릭시 보여질 기록 수 증가
};

const onEditClick = () => {
  router.push({ name: 'ProfileEdit' })
}

const onPointClick = () => {
  router.push({ name: 'RankingView' })
}

</script>

<template>
  <header>
    <HeaderComponent />
  </header>
  <q-page-container>
    <q-page class="flex flex-center">
      <q-card class="my-page-card q-pa-md">
        <q-card-section class="text-center">
          <div class="text-h4 text-deep-purple" style="font-weight:bold;">마이 페이지</div>
          <div class="text-subtitle2 text-grey q-mt-xs">내 정보를 확인하세요</div>
        </q-card-section>

        <q-card-section class="flex flex-center q-gutter-sm justify-around">
          <div class="flex flex-center justify-between">
            <q-avatar size="100px">
              <img v-if="authStore.profileImage" :src="authStore.profileImage" />
              <img v-else src="https://cdn.quasar.dev/img/avatar.png" alt=" 디폴트 프로필 이미지" />
            </q-avatar>
            <div class="nickname">
              닉네임 <q-icon name="edit" class="cursor-pointer" @click="onEditClick" />
              <div class="text-caption">{{ authStore.nickname }}</div>
            </div>
          </div>
          <div>{{ authStore.point }}p <q-icon name="arrow_forward_ios" class="cursor-pointer" @click="onPointClick" />
          </div>
        </q-card-section>

        <q-card-section class="flex flex-center q-gutter-sm justify-around" style=" margin-top:20px;">
          <div>
            전적
            <div> {{ authStore.winCount }}승 {{ authStore.tieCount }}무 {{ authStore.loseCount }}패</div>
          </div>
          <div style="padding-right:10px; width:120px; text-align:right;">
            승률
            <div>{{ authStore.winRate }}%</div>
          </div>
        </q-card-section>

        <q-card-section>
          <div class="game-records">
            <div class="buttons-container">
              <div class="left-button" style="padding-left:20px;">
                <q-btn flat label="기록" class="record-button" />
              </div>
              <div class="right-button">
                <q-btn flat label="더보기" @click="onViewMore" class="view-more-button" />
              </div>
            </div>
            <div v-for="(record, index) in gameRecords.slice(0, state.visibleCount)" :key="index" class="record-item">
              <div class="record-container">
                <div class="record">
                  <div class="record-box">
                    <q-avatar class="record-avatar">
                      <img :src="record.img1" alt="제시어 1 이미지">
                    </q-avatar>
                    <div class="record-text">{{ record.word1 }}</div>
                  </div>
                  <div class="vs">vs</div>
                  <div class="record-box">
                    <div class="record-text">{{ record.word2 }}</div>
                    <q-avatar class="record-avatar">
                      <img :src="record.img2" alt="제시어 2 이미지">
                    </q-avatar>
                  </div>
                </div>
              </div>
              <div :class="{ 'result-win': record.result === '승', 'result-lose': record.result === '패' }"
                class="record-result">
                <strong>{{ record.result }}</strong> {{ record.points }}
              </div>
            </div>
          </div>
        </q-card-section>

        <q-card-section class="titles-section">
          <div class="titles-header">칭호</div>
          <div class="titles-container">
            <q-chip v-for="title in titles" :key="title.name" class="title-chip">
              {{ title.name }}
            </q-chip>
          </div>
        </q-card-section>

      </q-card>
    </q-page>
  </q-page-container>
</template>

<style scoped>
.my-page-card {
  background: #f8f8ff;
  /* 연보라색 배경 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  /* 그림자 효과 */
}

.nickname {
  padding-left: 20px;
}

.result-win {
  color: blue;
}

.result-lose {
  color: red;
}


/* 칭호 */
.titles-section {
  padding: 20px;
  border-radius: 8px;
  display:flex;
  flex-direction: column;
  text-align:left;
  margin-left: 270px;
  /* 둥근 모서리 */
}

.titles-header {
  color: #888;
  /* 헤더 글자색 */
  font-weight: bold;
  margin-bottom: 15px;
  text-align: left;
}

.titles-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: left;
  /* 중앙 정렬 */
  gap: 10px;
  /* 칭호 사이의 간격 */
}

.title-chip {
  background-color: #34227C;
  /* 예쁜 보라색 배경 */
  color: white;
  /* 글자색 */
  font-weight: bold;
  padding: 10px 15px;
  border-radius: 15px;
}
.my-page-card{
  width: 90%;
}
.buttons-container {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin: 20px 0 10px 0;
}

.left-button {
  width: 55%; 
  text-align:left;
  margin-left: 240px;
}
.right-button {
  width: 45%; /* 버튼들이 차지할 너비를 반으로 나눕니다 */
  text-align:center;
  margin-right:70px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin: 20px 0;
}

.record-container {
  display: flex;
  justify-content: space-between;
  width: 55%;
  text-align:center;
  
}
.vs {
  margin: 0 20px;
  text-align:center;
  line-height:63px;
} 


.record {
  /* 필요한 스타일링 추가 */
  width: 100%; /* record가 차지할 너비 */
  display:flex;
  justify-content: center;
  margin-left:170px;
}
.record-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 30%;
  padding-left: 20px;
  padding-right: 20px;
}
.record-text {
  width: 100px;

  text-align: center;
}

.record-result {
  width: 45%; 
  text-align:center;
  line-height:63px;
}


</style>
