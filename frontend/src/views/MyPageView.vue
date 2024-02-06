<script setup>
import { QPage, QCard, QCardSection, QSeparator, QAvatar } from 'quasar'
import { useRouter } from 'vue-router'

const router = useRouter()


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

                <q-card-section class="flex flex-center">
                    <div>기록: 최고기록 - 100점</div>
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