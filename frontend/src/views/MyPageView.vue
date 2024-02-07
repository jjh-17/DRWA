<script setup>
import { QPage, QCard, QCardSection, QSeparator, QAvatar } from 'quasar'
import HeaderComponent from '@/components/common/HeaderComponent.vue'
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 예시 기록 데이터
const gameRecords = ref([
  { word1: '초콜릿', word2: '사탕', result: '승', points: '10p', img1: '/images/chocolate.png', img2: '/images/candy.png' },
  { word1: '책', word2: '영화', result: '패', points: '10p', img1: '/images/book.png', img2: '/images/movie.png' },
  { word1: '커피', word2: '녹차', result: '승', points: '10p', img1: '/images/coffee.png', img2: '/images/greentea.png' },
  { word1: '바다', word2: '산', result: '패', points: '10p', img1: '/images/sea.png', img2: '/images/mountain.png' },
  { word1: '고양이', word2: '강아지', result: '승', points: '10p', img1: '/images/cat.png', img2: '/images/dog.png' },
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

</script>

<template>
  <header>
    <HeaderComponent />
  </header>
  <q-page-container>
    <q-page class="flex flex-center">
      <q-card class="my-page-card q-pa-md" style="width: 90%">
        <q-card-section class="text-center">
          <div class="text-h4 text-deep-purple">마이 페이지</div>
          <div class="text-subtitle2 text-grey q-mt-xs">내 정보를 확인하세요</div>
        </q-card-section>

        <q-card-section class="flex flex-center q-gutter-sm justify-around">
          <div class="flex flex-center justify-between">
            <q-avatar size="100px">
              <img src="https://cdn.quasar.dev/img/avatar.png">
            </q-avatar>
            <div class="nickname">
              닉네임 <q-icon name="edit" class="cursor-pointer" @click="onEditClick" />
              <div class="text-caption">사용자123</div>
            </div>
          </div>
          <div>1630p <q-icon name="arrow_forward_ios" class="cursor-pointer" /></div>
        </q-card-section>

        <q-card-section class="flex flex-center q-gutter-sm justify-around">
          <div>
            전적
            <div>10승 5패</div>
          </div>
          <div>
            승률
            <div>66%</div>
          </div>
        </q-card-section>

        <q-card-section>
          <div class="game-records">
            <div class="buttons-container">
              <q-btn flat label="기록" class="record-button" />
              <q-btn flat label="더보기" @click="onViewMore" class="view-more-button" />
            </div>
            <div v-for="(record, index) in gameRecords.slice(0, state.visibleCount)" :key="index" class="record-item">
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
  /* 왼쪽 패딩을 추가하여 간격을 벌립니다. */
}

.game-records {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.buttons-container {
  display: flex;
  justify-content: space-between;
  /* 버튼들을 양쪽 끝으로 정렬 */
  width: 50%;
  /* 컨테이너의 너비를 최대로 설정 */
  margin-bottom: 20px;
  /* 버튼과 기록 사이의 간격 */
}


.record-item {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin: 20px 0;
}

.record-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  width: 30%;
  /* 고정된 박스 크기 */
}

.record-avatar {
  width: 40px;
  /* 아바타 크기 조정 */
  height: 40px;
}

.record-text {
  width: 100px;
  /* 텍스트 공간 고정 */
  text-align: center;
}

.vs {
  margin: 0 20px;
}

.record-result {
  width: 100px;
  /* 결과 공간 고정 */
  text-align: center;
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
  margin-top: 20px;
  border-radius: 8px;
  /* 둥근 모서리 */
}

.titles-header {
  color: #888;
  /* 헤더 글자색 */
  font-weight: bold;
  margin-bottom: 15px;
  text-align: center;
}

.titles-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  /* 중앙 정렬 */
  gap: 10px;
  /* 칭호 사이의 간격 */
}

.title-chip {
  background-color: #9C27B0;
  /* 예쁜 보라색 배경 */
  color: white;
  /* 글자색 */
  font-weight: bold;
  padding: 10px 15px;
  border-radius: 15px;
}
</style>
