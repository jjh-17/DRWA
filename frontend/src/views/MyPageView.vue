<script setup>
import { QPage, QCard, QCardSection, QSeparator, QAvatar } from 'quasar'
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const gameRecords = ref([
    { word1: '치킨', word2: '피자', result: '승', points: '10p', img1: '치킨이미지URL', img2: '피자이미지URL' },
    { word1: '고양이', word2: '강아지', result: '패', points: '5p', img1: '고양이이미지URL', img2: '강아지이미지URL' },
    { word1: '커피', word2: '차', result: '승', points: '15p', img1: '커피이미지URL', img2: '차이미지URL' },
    // 추가 기록들...
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
    <q-page-container>
        <q-page class="flex flex-center">
            <q-card class="my-page-card q-pa-md" style="width: 90%;">
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
                    <div class="text-left">
                        <q-btn flat label="기록" />
                    </div>
                    <div class="text-right">
                        <q-btn flat label="더보기" @click="onViewMore" />
                    </div>
                    <div v-for="(record, index) in gameRecords.slice(0, state.visibleCount)" :key="index" class="q-my-md">
                        <div class="flex justify-between items-center">
                            <div class="flex items-center">
                                <q-avatar>
                                    <img :src="record.img1" alt="제시어 1 이미지">
                                </q-avatar>
                                <span class="q-ml-md">{{ record.word1 }}</span>
                            </div>
                            vs
                            <div class="flex items-center">
                                <span class="q-mr-md">{{ record.word2 }}</span>
                                <q-avatar>
                                    <img :src="record.img2" alt="제시어 2 이미지">
                                </q-avatar>
                            </div>
                            <strong>{{ record.result }}</strong> {{ record.points }}
                        </div>
                    </div>
                </q-card-section>

                <q-card-section class="flex flex-center">
                    <div>칭호: 초보 탈출</div>
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
</style>