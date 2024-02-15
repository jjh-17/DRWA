<script setup>
import { QInput, QBtn, QAvatar, QFile, QChip } from 'quasar';
import { ref, onMounted } from 'vue';
import { categories as importedCategories } from '@/components/category/Category';
import { httpService } from '@/api/axios';
import HeaderComponent from '@/components/common/HeaderComponent.vue'
import { useAuthStore } from '@/stores/useAuthStore'; // authStore 가져오기

const authStore = useAuthStore();



// '전체'와 '기타' 카테고리를 제외합니다.
const filteredCategories = importedCategories.filter(category => category.english !== 'ALL' && category.english !== 'ETC');

// 필터링된 카테고리로 categories 데이터를 반응형으로 만듭니다.
const categories = ref(filteredCategories.map(category => ({
    ...category,
    selected: false
})));


const nickname = ref(authStore.nickname);

// 프로필 이미지의 데이터 URL을 저장하는 ref
const profileImage = ref(authStore.profileImage);
// 선택된 파일 객체를 저장하는 ref
const selectedFile = ref(null);

// 컴포넌트 마운트 시 관심 카테고리를 확인하여 선택 상태 업데이트
onMounted(() => {
    categories.value.forEach(category => {
        // if (authStore.interests.includes(category.english)) {
        //     category.selected = true;
        // }
        // authStore.interests 배열 내에서 현재 카테고리와 일치하는 debateCategory 값을 가진 객체가 있는지 확인합니다.
        const isInterested = authStore.interests.some(interest => interest.debateCategory === category.english.toUpperCase());

        // 일치하는 객체가 있으면 해당 카테고리의 selected 상태를 true로 설정합니다.
        category.selected = isInterested;
    });
});

async function checkNickname() {
    if (!nickname.value) {
        alert('닉네임을 입력해주세요.');
        return;
    }
    try {
        const response = await httpService.get(`/profile/check/nickname?nickname=${nickname.value.toString()}`);
        if (response.data) {
            alert('사용 가능한 닉네임입니다.');
        } else {
            alert('이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요.');
        }
    } catch (error) {
        console.error('닉네임 중복 확인 중 오류 발생:', error);
        alert('닉네임 중복 확인 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
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
    console.log('파일 선택:', file);
    if (file) {
        // 파일 객체 저장
        selectedFile.value = file;
        const reader = new FileReader();
        reader.onload = (e) => {
            profileImage.value = e.target.result;
        };
        reader.readAsDataURL(file);
    } else {
        // 프로필 사진이 없을 때 디폴트 이미지로 설정
        profileImage.value = '/path/to/default/profile/image.png'; // 실제 경로로 대체해야 합니다.
        selectedFile.value = null; // 파일 선택 취소 시 null로 설정

    }
}

/**
 * 프로필 수정 완료 버튼 클릭 시 실행되는 메소드
 */
async function submitProfile() {
    const selectedCategories = categories.value
        .filter(category => category.selected)
        .map(category => category.english.toUpperCase());

    // 관심 카테고리를 UpdateInterestRequestDto 객체 형식에 맞게 구성
    const updateInterestRequestDto = {
        debateCategories: selectedCategories,
    };

    const updateProfileRequest = {
        profileId: authStore.profileId,
        nickname: nickname.value,
    }

    try {
        // 여기서 실제로 API 요청을 보냅니다. 예시로는 console.log로 대체합니다.
        console.log('보낼 데이터:', selectedCategories);
        let response = await httpService.post('/member/interests', updateInterestRequestDto);
        if (response.status === 200) {
            // 성공적으로 관심 카테고리가 업데이트되면 Pinia 스토어의 상태를 직접 업데이트
            authStore.interests = selectedCategories;
        }

        response = await httpService.post('profile/update', updateProfileRequest);

        if (response.status === 200) {
            // 성공적으로 프로필 정보가 업데이트되면 Pinia 스토어의 상태를 직접 업데이트
            authStore.nickname = nickname.value;
        }

        if (selectedFile.value) {
            // 프로필 이미지가 변경된 경우에만 업로드
            await uploadProfileImage(selectedFile.value);

        }

        alert('정보 수정이 완료되었습니다.');
    } catch (error) {
        console.error('정보 수정 실패:', error);
        alert('정보 수정에 실패하였습니다.');
    }
}

/**
 * 프로필 이미지 업로드
 * @param {*} file 
 */
async function uploadProfileImage(file) {
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await httpService.post('/profile/upload/image', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
        });
        if (response.status === 200) {
            // alert('프로필 이미지가 성공적으로 업로드되었습니다.');
            profileImage.value = response.data; // 업로드된 이미지의 URL로 업데이트
            // 성공적으로 프로필 이미지가 업데이트되면 Pinia 스토어의 상태를 직접 업데이트
            authStore.profileImage = response.data;

        } else {
            alert('이미지 업로드에 실패했습니다.');
        }
    } catch (error) {
        console.error('이미지 업로드 중 오류 발생:', error);
        alert('이미지 업로드 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
}
</script>

<template>
    <header>
        <HeaderComponent />
    </header>
    <div class="q-pa-md" style="max-width: 700px; margin:auto;">
        <q-card class="q-pa-md" style="background: #E8EBF9; color: #34227C; width:100%;">
            <div class="text-h5 text-center q-mb-md" style="color: #34227C; margin-top:40px; font-weight:bold;">정보 수정</div>
            <div class="text-subtitle2 text-center q-mb-md" style="color: grey;margin-bottom:20px;">회원 가입에 필요한 정보를 입력해주세요.
            </div>

            <q-separator />



            <div class="q-mt-md" style="margin-bottom:20px; margin-top:30px;">
                <div class="text-bold q-mb-xs" style="padding:  0 12px;">닉네임</div>
                <div class="q-input q-ma-none q-pa-none" style="display: flex; align-items: center;">
                    <q-input rounded outlined v-model="nickname" class="nickname-input"
                        style="flex-grow: 1; border-right: none;" />
                    <q-btn flat label="중복확인" color="#34227C" @click="checkNickname" class="q-ml-md rounded-borders"
                        style="max-height: 54px;" />
                </div>
            </div>

            <div class="q-mt-md" style="margin-top:20px; margin-bottom:20px;">
                <div class="text-bold q-mb-xs" style="padding:  0 12px;">관심 카테고리</div>
                <div class="q-gutter-sm q-mt-xs">
                    <q-chip v-for="category in categories" :key="category.english" clickable
                        :color="category.selected ? 'purple' : 'grey'" @click="toggleCategory(category)">
                        {{ category.name }}
                    </q-chip>
                </div>
            </div>

            <div style="margin-top:20px; margin-bottom:20px;">
                <div class="text-bold q-mb-xs" style="padding:  0 12px;">프로필 이미지(선택)</div>
                <div class="q-mt-md flex items-center profile-section" style="padding:  0 12px;">
                    <q-avatar size="100px" class="q-mr-md">
                        <img v-if="profileImage" :src="profileImage" />
                        <img v-else src="https://cdn.quasar.dev/img/avatar.png" alt=" 디폴트 프로필 이미지" />
                    </q-avatar>
                    <q-file filled label="파일 선택" accept="image/*" @update:model-value="onFileChange" class="file-input" />
                </div>
            </div>

            <q-btn label="수정 완료" class="full-width q-mt-md"
                style="background-color: #34227C; color: #E8EBF9; margin-top:25px; margin-bottom:30px;"
                @click="submitProfile" rounded />
        </q-card>
    </div>
</template>

<style scoped>
.full-width {
    width: 100%;
}

.nickname-input {
    border-color: #34227C;
}

.profile-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
