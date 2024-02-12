import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useChattingStore = defineStore('chattingStore', () => {
  const messagesLeft = ref([]);
  const messagesRight = ref([]);
  const messagesAll = ref([]);
  const messagesLeftComputed = computed(() => messagesLeft);
  const messagesRightComputed = computed(() => messagesRight);
  const messagesAllComputed = computed(() => messagesAll);

  return {
    messagesLeft, messagesRight, messagesAll,
    messagesLeftComputed, messagesRightComputed, messagesAllComputed,
  }
})