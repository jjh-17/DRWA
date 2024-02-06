<script setup>
import { QInput, QBtn, QAvatar, QFile, QChip } from 'quasar';
import { ref, onMounted } from 'vue';
import { categories as importedCategories } from '@/components/category/Category';

// '전체'와 '기타' 카테고리를 제외합니다.
const filteredCategories = importedCategories.filter(category => category.english !== 'all' && category.english !== 'etc');

// 필터링된 카테고리로 categories 데이터를 반응형으로 만듭니다.
const categories = ref(filteredCategories.map(category => ({
    ...category,
    selected: false
})));

// 예시 데이터 및 메소드
const nickname = ref('');

const profileImage = ref('');

function checkNickname() {
    alert('중복 확인 로직을 구현하세요.');
}

function toggleCategory(category) {
    console.log("토글!", category)
    category.selected = !category.selected;
    // 배열 내 객체의 변경을 감지하기 위해 categories를 새로운 배열로 갱신
    // categories.value = [...categories.value];
}

function onFileChange(file) {
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            profileImage.value = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

function submitProfile() {
    alert('정보 수정 완료 로직을 구현하세요.');
}
</script>

<template>
    <div class="q-pa-md" style="max-width: 700px; margin: auto;">
        <q-card class="q-pa-md" style="background: #f8f8ff; color: purple;">
            <div class="text-h5 text-center q-mb-md" style="color: purple;">정보 수정</div>
            <div class="text-subtitle2 text-center q-mb-md" style="color: grey;">회원 가입에 필요한 정보를 입력해주세요.</div>

            <q-separator />

            <div class="q-mt-md">
                <div class="text-bold q-mb-xs">이름</div>
                <div>김싸피</div>
            </div>

            <div class="q-mt-md">
                <div class="text-bold q-mb-xs">닉네임</div>
                <q-input filled v-model="nickname" placeholder="닉네임을 입력해주세요" />
                <q-btn flat label="중복확인" color="purple" @click="checkNickname" class="q-mt-md" />
            </div>

            <div class="q-mt-md">
                <div class="text-bold q-mb-xs">관심 카테고리</div>
                <div class="q-gutter-sm q-mt-xs">
                    <q-chip v-for="category in categories" :key="category.english" clickable
                        :color="category.selected ? 'purple' : 'grey'" @click="toggleCategory(category)">
                        {{ category.name }}
                    </q-chip>
                </div>
            </div>

            <div class="q-mt-md">
                <div class="text-bold q-mb-xs">프로필 이미지(선택)</div>
                <div class="q-gutter-sm q-mt-xs">
                    <q-avatar>
                        <img v-if="profileImage" :src="profileImage" />
                    </q-avatar>
                    <q-file filled label="파일 선택" @update:model-value="onFileChange" class="q-mt-xs" />
                </div>
            </div>

            <q-btn color="dark" label="수정 완료" class="full-width q-mt-md" @click="submitProfile" />
        </q-card>
    </div>
</template>

<style scoped>
.full-width {
    width: 100%;
}
</style>
