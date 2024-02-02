<script setup>
import { RouterView } from 'vue-router'
import HeaderComponent from '@/components/common/HeaderComponent.vue'
</script>

<template>
  <header>
    <HeaderComponent />
  </header>
  <RouterView />
</template>

<style scoped></style>
