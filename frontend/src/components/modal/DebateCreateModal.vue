<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
    <div class="modal-container">
      <div class="modal-body">
        <form @submit.prevent="submitForm">
          <!-- 카테고리 선택 -->
          <div class="form-group">
            <label for="category">카테고리</label>
            <select id="category" v-model="category">
              <option
                v-for="category in filteredCategories"
                :key="category.english"
                :value="category.english"
              >
                {{ category.name }}
              </option>
            </select>
          </div>

          <!-- 제목 입력 -->
          <div class="form-group">
            <label for="title">제목</label>
            <input type="text" id="title" v-model="title" placeholder="예시: 당신의 음식 취향은?" />
          </div>

          <!-- 제시어 입력 -->
          <div class="form-group">
            <label for="keywords">제시어</label>
            <input type="text" id="keywordA" v-model="keywordA" placeholder="입력하세요" />
            <span id="vs"> vs </span>
            <input type="text" id="keywordB" v-model="keywordB" placeholder="입력하세요" />
          </div>

          <!-- 인원수 선택 -->
          <div class="form-group">
            <label for="playerNum">인원수</label>
            <select id="playerNum" v-model="playerNum" :disabled="disableOptions">
              <option value="1">1:1</option>
              <option value="2">2:2</option>
              <option value="3">3:3</option>
            </select>
            <label for="jurorNum">배심원수</label>
            <input
              type="text"
              id="jurorNum"
              v-model="jurorNum"
              placeholder="예시 : 3"
              :disabled="disableOptions"
            />
          </div>

          <!-- 공개 여부 -->
          <div class="form-group" style="display: flex">
            <label for="isPublic">공개 여부</label>
            <q-toggle
              :label="`${isPublic ? 'public' : 'private'}`"
              v-model="isPublic"
              style="margin-right: 40px"
            />
            <div v-if="!isPublic">
              <input
                type="password"
                id="password"
                v-model="password"
                placeholder="비밀번호 입력"
                style="width: 150px"
              />
            </div>
          </div>

          <!-- 시간 설정 -->
          <div class="form-group">
            <label for="debateTime" id="times">발언시간</label>
            <select id="debateTime" v-model="debateTime">
              <option>1분</option>
              <option>3분</option>
              <option>5분</option>
            </select>
            <label for="preparationTime" id="times">준비시간</label>
            <select id="preparationTime" v-model="preparationTime">
              <option>1분</option>
              <option>5분</option>
              <option>10분</option>
            </select>
            <label for="questionTime" id="times">질문시간</label>
            <select id="questionTime" v-model="questionTime">
              <option>1분</option>
              <option>2분</option>
              <option>3분</option>
            </select>
          </div>

          <!-- 썸네일 설정 -->
          <div
            class="form-group"
            style="display: flex; justify-content: space-between; margin-right: 30px; height: 100px"
          >
            <label for="thumbnailA">제시어A<br />썸네일</label>
            <div class="thA" style="margin-right: 40px">
              <input
                type="text"
                id="thumbnailA"
                v-model="thumbnailASearchQuery"
                placeholder="썸네일A 검색어 입력"
                style="margin-bottom: 3px; width: 135px"
              />
              <div class="selectImgA" style="font-size: small; color: black">
                첨부 이미지<br />{{ thumbnailAId }}
              </div>
            </div>
            <ThumbnailImg :searchQuery="thumbnailASearchQuery" @selectImg="setThumbnailA" />
          </div>
          <div
            class="form-group"
            style="display: flex; justify-content: space-between; margin-right: 30px; height: 100px"
          >
            <label for="thumbnailB">제시어B<br />썸네일</label>
            <div class="thB" style="margin-right: 40px">
              <input
                type="text"
                id="thumbnailB"
                v-model="thumbnailBSearchQuery"
                placeholder="썸네일B 검색어 입력"
                style="margin-bottom: 3px; width: 135px"
              />
              <div class="selectImgB" style="font-size: small; color: black">
                첨부 이미지<br />{{ thumbnailBId }}
              </div>
            </div>
            <ThumbnailImg :searchQuery="thumbnailBSearchQuery" @selectImg="setThumbnailB" />
          </div>

          <!-- 폼 제출 버튼 -->
          <div class="btn">
            <button type="submit" class="submit-button">방 생성</button>
            <button class="close-button" @click="closeModal">취소</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed, watch } from 'vue'
import { categories } from '@/components/category/Category.js'
import { httpService } from '@/api/axios'
import { QToggle } from 'quasar'
import ThumbnailImg from '../room/ThumbnailImg.vue'
import { useAuthStore } from '@/stores/useAuthStore.js'
import {useRouter} from 'vue-router'
import { useDebateStore } from "@/stores/useDebateStore"
import { useGameStore } from "@/stores/useGameStore"
import { team } from "../common/Team"

const router = useRouter();
const debateStore = useDebateStore();
const authStore = useAuthStore()
const gameStore = useGameStore();

const memberId = authStore.memberId


const thumbnailASearchQuery = ref('')
const thumbnailBSearchQuery = ref('')
const title = ref('')
const keywordA = ref('')
const keywordB = ref('')
const playerNum = ref('1')
const jurorNum = ref('')
const debateTime = ref('1분')
const preparationTime = ref('1분')
const questionTime = ref('1분')
const thumbnailAUrl = ref('')
const thumbnailBUrl = ref('')
const thumbnailAId = ref('')
const thumbnailBId = ref('')
const isPublic = ref(true)
const password = ref('')

const filteredCategories = computed(() => {
  return categories.filter(category => category.english !== 'ALL')
})

const category = ref(filteredCategories.value.length > 0 ? filteredCategories.value[0].english : '')

// 검색 쿼리 변경에 따른 썸네일 갱신
watch(thumbnailASearchQuery, () => {
  // 썸네일 A 검색 로직
})
watch(thumbnailBSearchQuery, () => {
  // 썸네일 B 검색 로직
})

// 썸네일 선택 시 실행될 함수
const setThumbnailA = ({ imageId, imageUrl }) => {
  thumbnailAUrl.value = imageUrl
  thumbnailAId.value = imageId
}
const setThumbnailB = ({ imageId, imageUrl }) => {
  thumbnailBUrl.value = imageUrl
  thumbnailBId.value = imageId
}

const sessionId = ref();

const props = defineProps({
  isVisible: Boolean,
  disableOptions: Boolean
})
const emits = defineEmits(['update:isVisible'])

const closeModal = () => emits('update:isVisible', false)

const submitForm = async () => {
  // 입력 값 검증
  if (!category.value || !title.value || !keywordA.value || !keywordB.value ||
      !playerNum.value || !jurorNum.value || !debateTime.value ||
      !preparationTime.value || !questionTime.value || (!isPublic.value && !password.value) ||
      !thumbnailAUrl.value || !thumbnailBUrl.value) {
    alert("모든 필드를 채워주세요.");
    return; // 함수 실행 중단
  }
  let roomDto = {
    debateCategory: category.value,
    hostId: memberId,
    title: title.value,
    leftKeyword: keywordA.value,
    rightKeyword: keywordB.value,
    playerNum: parseInt(playerNum.value), // 1:1, 2:2, 3:3 형태를 숫자로 변환
    jurorNum: parseInt(jurorNum.value),
    isPrivate: !isPublic.value,
    password: isPublic.value ? '' : password.value,
    speakingTime: parseInt(debateTime.value) * 60, // 분을 초로 변환
    readyTime: parseInt(preparationTime.value) * 60, // 분을 초로 변환
    qnaTime: parseInt(questionTime.value) * 60, // 분을 초로 변환
    thumbnail1: thumbnailAUrl.value,
    thumbnail2: thumbnailBUrl.value
  }
  if (!isPublic.value) {
    roomDto = { ...roomDto, password: password.value }
  }
  try {
    // const response = await httpService.post('/openvidu/session', roomDto)
    const response = await debateStore.createRoom(roomDto);
    console.log('방 생성 응답:', response.data)
    sessionId.value = response.data.sessionId

    // 방 연결 설정
    gameStore.sessionId = sessionId.value;
    gameStore.team = team[0].english

    // await makeDebateRoom()

    router.push(`/debate/${sessionId.value}`);

    closeModal()
  } catch (error) {
    console.error('방 생성 에러:', error)
  }
}

const makeDebateRoom = async () => {
  try {
    // const response = await httpService.get(`/openvidu/session/${sessionId.value}`);
    const response = await debateStore.joinDebate(sessionId.value);
    console.log('연결 정보 응답:', response.data);
    gameStore.sessionId = sessionId.value;
    gameStore.token = response.data.connection.token;
    gameStore.team = team[0].eng
    router.push(`/debate/${sessionId.value}`);

    // const response = await debateStore.joinDebate(sessionId.useDalue)
    // const data = response.data
    // console.log(`연결 정보 응답: ${data.}`);
    // router.push(`/debate/${sessionId.value}`);
  } catch (error) {
    console.error('연결 정보 가져오기 에러:', error);
  }
};
</script>

<style scoped>
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 30px; /* 상하 패딩을 줄여 높이 감소 */
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 10px;
  width: 30rem;
  padding: 2rem;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
  height: 660px;
}
.form-group {
  margin-bottom: 1rem;
}

.form-group input,
.form-group select {
  border: 1px solid #ccc; /* 얇은 테두리 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 음영 효과 */
  padding: 0.5rem; /* 안쪽 여백 */
  border-radius: 4px; /* 모서리 둥글게 */
  box-sizing: border-box; /* 박스 크기 계산 방식 */
}

.form-group label {
  width: 100px; /* 고정된 너비로 설정 */
  display: inline-block; /* 라벨을 인라인 블록 요소로 만들어 줄 바꿈 없이 옆에 배치 가능하게 함 */
  margin-bottom: 0.5rem; /* 라벨과 입력 필드 사이의 여백 */
  color: black;
}
.radio {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.isPublic {
  display: flex;
  width: 100%;
}
.isPublic input {
  margin-right: 5px;
}
.privateb {
  margin-left: 20px;
  width: 100%;
}
.publicb {
  width: 100%;
}
#playerNum {
  margin-right: 30px;
}
#times {
  width: 60px;
}
#time {
  margin-right: 10px;
}
#preparationTime,
#debateTime,
#questionTime {
  margin-right: 10px;
}

.thA,
.thB {
  display: flex;
  flex-direction: column;
}
/* 공개/비공개 라디오 버튼 스타일 조정 */
input[type='radio'] + label {
  background-color: #f2f2f2; /* 배경색 */
  /* padding: 0.25rem 0.75rem; 안쪽 여백 */
  border-radius: 20px; /* 모서리 둥글게 */
  cursor: pointer; /* 마우스 커서 변경 */
  /* margin-right: 0.5rem; 오른쪽 여백 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 음영 효과 */
}

input[type='radio']:checked + label {
  background-color: #34227c; /* 선택된 라벨의 배경색 */
  color: white; /* 선택된 라벨의 글자색 */
}
#keywordA,
#keywordB {
  width: 130px;
}
#vs {
  font-size: 20px;
}
#jurorNum {
  width: 100px;
}
#thumbnailA,
#thumbnailB {
  width: 100px;
}
.btn {
  display: flex;
  justify-content: space-between;
}
.pbtn {
  text-align: center;
}

.submit-button {
  background-color: #34227c;
  color: #e8ebf9;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 120px;
  margin-left: 70px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.submit-button:hover {
  background-color: #28054b;
}

.close-button {
  background-color: #e8ebf9;
  color: #34227c;
  font-weight: bold;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  width: 120px;
  margin-right: 70px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.close-button:hover {
  background-color: #28054b;
}
</style>
