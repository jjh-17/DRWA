import { defineStore } from 'pinia'
import { httpService } from '@/api/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    userProfilePic: null,
    socialLoginUrl: {
      google: null,
      naver: null,
      kakao: null
    }
  }),
  actions: {
    async fetchSocialLoginUrl(socialType) {
      try {
        const response = await httpService.get(`/member/authURL/${socialType}`)
        this.socialLoginUrl[socialType] = response.data.authorizationUrl
      } catch (error) {
        console.error('Error fetching social login URL:', error)
        // 에러 처리 로직을 추가할 수 있습니다.
      }
    }
  }
})
