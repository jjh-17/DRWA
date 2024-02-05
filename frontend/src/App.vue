<script setup>
import { RouterView } from 'vue-router'
import HeaderComponent from '@/components/common/HeaderComponent.vue'
</script>

<template>
  <q-layout>
    <header>
      <HeaderComponent />
    </header>
    <RouterView />
  </q-layout>
</template>

<style scoped></style>
