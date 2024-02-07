<template>
  <header>
    <DebateHeaderBar :headerBarTitle="headerBarTitle"/>
  </header>
  <div>
    12
  </div>
  <footer>
    <DebateBottomBar />
  </footer>
</template>

<script setup>
import DebateHeaderBar from '@/components/debate/DebateHeaderBar.vue'
import DebateBottomBar from '@/components/debate/DebateBottomBar.vue'

import { ref, reactive, toRefs } from 'vue'
import { useDebateStore } from '@/stores/useDebateStore'
import { useRoute } from 'vue-router'

const route = useRoute()
const debateId = route.params.debateId
const debateStore = useDebateStore()
const debate = debateStore.getDebate(debateId)
const headerBarTitle = ref('[임시]제목입니다.')
// headerBarTitle = debate.getTitle();

</script>

<style scoped>
footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
}
</style>
