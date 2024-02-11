<script setup>
import { QInput, QBtn, QAvatar, QFile, QChip } from 'quasar';
import { ref, onMounted } from 'vue';
import { categories as importedCategories } from '@/components/category/Category';
import { httpService } from '@/api/axios';
import HeaderComponent from '@/components/common/HeaderComponent.vue'
import { useAuthStore } from '@/stores/useAuthStore'; // authStore 가져오기

const authStore = useAuthStore();

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

// 컴포넌트 마운트 시 관심 카테고리를 확인하여 선택 상태 업데이트
onMounted(() => {
    categories.value.forEach(category => {
        if (authStore.interests.map(interest => interest.toLowerCase()).includes(category.english)) {
            category.selected = true;
        }
    });
});

function checkNickname() {
    alert('중복 확인 로직을 구현하세요.');
}

/**
 * 카테고리 선택/해제 토글
 * @param {*} category 선택한 카테고리
 */
function toggleCategory(category) {
    const selectedCount = categories.value.filter(cat => cat.selected).length;
    if (!category.selected && selectedCount >= 3) {
        alert('관심 카테고리는 최대 3개까지 선택 가능합니다.');
        return;
    }
    category.selected = !category.selected;
}

function onFileChange(file) {
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            profileImage.value = e.target.result;
        };
        reader.readAsDataURL(file);
    } else {
        // 프로필 사진이 없을 때 디폴트 이미지로 설정
        profileImage.value = '/path/to/default/profile/image.png'; // 실제 경로로 대체해야 합니다.
    }
}

/**
 * 프로필 수정 완료 버튼 클릭 시 실행되는 메소드
 */
async function submitProfile() {
    const selectedCategories = categories.value
        .filter(category => category.selected)
        .map(category => category.english);

    // 예시: 닉네임, 프로필 이미지, 선택된 관심 카테고리 데이터를 서버로 전송
    const profileData = {
        nickname: nickname.value,
        profileImage: profileImage.value,
        categories: selectedCategories,
    };

    try {
        // 여기서 실제로 API 요청을 보냅니다. 예시로는 console.log로 대체합니다.
        console.log('보낼 데이터:', selectedCategories);
        const response = await httpService.post('/member/update/interests', selectedCategories);
        if (response.status === 200) {
            // 성공적으로 관심 카테고리가 업데이트되면 Pinia 스토어의 상태를 직접 업데이트
            authStore.interests = selectedCategories;
        }
        alert('정보 수정이 완료되었습니다.');
    } catch (error) {
        console.error('정보 수정 실패:', error);
        alert('정보 수정에 실패하였습니다.');
    }
}
</script>

<template>
    <header>
        <HeaderComponent />
    </header>
    <div class="q-pa-md" style="max-width: 700px; margin: auto;">
        <q-card class="q-pa-md" style="background: #f8f8ff; color: purple;">
            <div class="text-h5 text-center q-mb-md" style="color: purple;">정보 수정</div>
            <div class="text-subtitle2 text-center q-mb-md" style="color: grey;">회원 가입에 필요한 정보를 입력해주세요.</div>

            <q-separator />



            <div class="q-mt-md">
                <div class="text-bold q-mb-xs">닉네임</div>
                <div class="q-input q-ma-none q-pa-none" style="display: flex; align-items: center;">
                    <q-input rounded outlined v-model="nickname" placeholder="닉네임을 입력해주세요" class="nickname-input"
                        style="flex-grow: 1; border-right: none;" />
                    <q-btn flat label="중복확인" color="purple" @click="checkNickname" class="q-ml-md rounded-borders"
                        style="max-height: 54px;" />
                </div>
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

            <div class="text-bold q-mb-xs">프로필 이미지(선택)</div>
            <div class="q-mt-md flex items-center profile-section">
                <q-avatar size="100px" class="q-mr-md">
                    <img v-if="profileImage" :src="profileImage" />
                    <img v-else src="/path/to/default/profile/image.png" alt="디폴트 프로필 이미지" />
                </q-avatar>
                <q-file filled label="파일 선택" @update:model-value="onFileChange" class="file-input" />
            </div>

            <q-btn color="dark" label="수정 완료" class="full-width q-mt-md" @click="submitProfile" rounded />
        </q-card>
    </div>
</template>

<style scoped>
.full-width {
    width: 100%;
}

.nickname-input {
    border-color: purple;
}

.profile-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
