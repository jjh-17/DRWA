<script setup>
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';

// route는 현재 라우트의 정보를 접근하기 위해 사용됩니다.
const route = useRoute()
// router는  라우트를 변경하기 위해 사용합니다.
const router = useRouter()
// useAuthStore는 인증 관련 상태와 메서드를 가져옵니다.
const authStore = useAuthStore()

onMounted(() => {
    authStore.processLoginCallback(route.query)
})



</script>

<template>
    <div>

    </div>
</template>

<style scoped></style>