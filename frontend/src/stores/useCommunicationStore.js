import { defineStore } from "pinia";
import { ref } from "vue";

export const useCommunicationStore = defineStore('communicationStore', () => {
  // 마이크/카메라 켜짐 여부
  const isMicOn = ref(false);
  const isCameraOn = ref(false);
  const isShareOn = ref(false);

  // 마이크/카메라 핸들링 권한 여부
  const isMicHandleAvailable = ref(false);
  const isCameraHandleAvailable = ref(false);
  const isShareHandleAvailable = ref(false);



  return {
    isMicOn, isCameraOn, isShareOn,
    isMicHandleAvailable, isCameraHandleAvailable, isShareHandleAvailable,

  };
})