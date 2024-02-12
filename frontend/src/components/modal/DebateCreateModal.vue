<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
    <div class="modal-container">
      <div class="modal-header">
        <h3>방 생성</h3>
        <button class="close-button" @click="closeModal">✕</button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label for="category">카테고리</label>
            <select id="category">
              <option v-for="category in filteredCategories" :key="category.english" :value="category.english">
                {{ category.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="title">제목</label>
            <input type="text" id="title" placeholder="예시: 당신의 음식 취향은?" />
          </div>
          <div class="form-group">
            <label for="kewords">제시어</label>
            <input type="text" id="keywordA" placeholder="입력하세요" />
            <span>vs</span>
            <input type="text" id="keywordB" placeholder="입력하세요" />
          </div>
          <div class="form-group">
            <label for="category">인원수</label>
            <select id="category">
              <option>1:1</option>
              <option>2:2</option>
              <option>3:3</option>
            </select>
            <label for="category">배심원수</label>
            <input type="text" id="title" placeholder="입력하세요" />
          </div>
          <div class="form-group">
            <label for="isPublic">공개 여부</label>
            <input
              type="radio"
              id="public"
              name="visibility"
              value="public"
              v-model="isPublic"
              checked
            />
            <label for="public" class="btn">공개</label>
            <input type="radio" id="private" name="visibility" value="private" v-model="isPublic" />
            <label for="private" class="btn">비공개</label>
            <!-- 비공개 선택시 비밀번호 입력란 표시 -->
            <span v-if="isPublic === 'private'">
              <label for="password">비밀번호:</label>
              <input type="password" id="password" placeholder="비밀번호 입력" />
            </span>
          </div>
          <div class="form-group">
            <label for="category">발언시간</label>
            <select id="category">
              <option>1분</option>
              <option>3분</option>
              <option>5분</option>
            </select>
            <label for="category">준비시간</label>
            <select id="category">
              <option>1분</option>
              <option>5분</option>
              <option>10분</option>
            </select>
            <label for="category">질문시간</label>
            <select id="category">
              <option>1분</option>
              <option>2분</option>
              <option>3분</option>
            </select>
          </div>
          <div class="form-group">
            <label for="thumnbnailA">제시어A 썸네일</label>
            <input type="text" id="thumbnailA" placeholder="썸네일A" />
          </div>
          <div class="form-group">
            <label for="thumnbnailB">제시어B 썸네일</label>
            <input type="text" id="thumbnailB" placeholder="썸네일B" />
          </div>

          <!-- 여기에 더 많은 폼 필드를 추가하세요 -->
          <button type="submit" class="submit-button">방 만들기</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed } from 'vue'
import { categories } from '@/components/category/Category.js'
import axios from 'axios' // Axios 라이브러리 임포트


const filteredCategories = computed(() => {
  return categories.filter((c) => c.english !== 'all')
})

const props = defineProps({
  isVisible: Boolean
})
const emits = defineEmits(['update:isVisible'])

const closeModal = () => {
  emits('update:isVisible', false)
}

// 폼 데이터를 관리하는 반응형 변수들
const category = ref('')
const title = ref('')
const keywordA = ref('')
const keywordB = ref('')
const juryCount = ref('')
const debateTime = ref('1분') // 초기값 설정
const preparationTime = ref('1분') // 초기값 설정
const questionTime = ref('1분') // 초기값 설정
const thumbnailA = ref('')
const thumbnailB = ref('')
const isPublic = ref('public')

const submitForm = async () => {
  // 폼 데이터를 객체로 구성
  const RoomInfo = {
    category: category.value,
    title: title.value,
    keywords: [keywordA.value, keywordB.value],
    juryCount: juryCount.value,
    debateTime: debateTime.value,
    preparationTime: preparationTime.value,
    questionTime: questionTime.value,
    thumbnails: [thumbnailA.value, thumbnailB.value],
    isPublic: isPublic.value === 'public'
  }

  try {
    const response = await axios.post('/debate/create', RoomInfo)
    console.log('방 생성 응답:', response.data)
    // 모달 닫기
    closeModal()
  } catch (error) {
    console.error('방 생성 에러:', error)
  }
}
</script>


<style scoped>
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
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.form-group {
  margin-bottom: 1rem;
}

.submit-button {
  background-color: #5b3cc4;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 0.3rem;
  cursor: pointer;
  font-size: 1rem;
}
</style>
