<script setup>
// import { ref, onMounted, computed } from 'vue';
import { computed } from 'vue';
import OvVideo from "./OvVideo.vue";
const props = defineProps({
  streamManager: Object,
})

// clientData는 computed로 진행됨
const clientData = computed(() => {
  const { clientData } = getConnectionData();
  return clientData;
});

function getConnectionData() {
  const { connection } = props.streamManager.stream;
  return JSON.parse(connection.data);
}
</script>

<template>
  <div v-if="streamManager">
    <!-- {{ clientData }}이게 내 현재 이름임 -->
    <!-- <div><p>{{ clientData }}</p></div> -->
    <ov-video :stream-manager="streamManager"/>
  </div>
</template>

<script>
  export default {
    name: 'UserVideo',
  }
</script>