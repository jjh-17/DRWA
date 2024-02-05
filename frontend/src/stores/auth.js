import { defineStore } from 'pinia'
import { httpService } from '@/api/axios'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    userProfilePic: null,
    accessToken: null,
    userId: null
  }),
  actions: {
    /**
     * 소셜로그인 URL을 가져오는 함수
     * @param {*} socialType
     */
    async fetchSocialLoginUrl(socialType) {
      try {
        const response = await httpService.get(`/member/authURL/${socialType}`)
        const loginUrl = response.data.authorizationUrl
        // 현재 페이지 URL을 state에 저장하거나 다른 방법으로 기억합니다.
        sessionStorage.setItem('redirectAfterLogin', window.location.pathname)
        window.location.href = loginUrl
      } catch (error) {
        console.error('Error fetching social login URL:', error)
      }
    },
    /**
     * 소셜로그인 후 redirect된 콜백 처리하는 함수
     * @param {*} query
     */
    async processLoginCallback(query) {
      try {
        const { provider, code } = query
        console.log('socialType:', provider, 'code:', code)
        console.log('HTTP Service Headers:', httpService.defaults.headers)
        const response = await httpService.post(`/member/login`, {
          socialType: provider,
          code: code
        })
        // 로그인 데이터를 저장하는 함수 호출
        this.setLoginData(response.data)

        const redirectAfterLogin = sessionStorage.getItem('redirectAfterLogin') || '/'
        sessionStorage.removeItem('redirectAfterLogin')
        router.push(redirectAfterLogin)
      } catch (error) {
        console.error('Error processing login callback:', error)
      }
    },

    /**
     * 로그인 후 로그인 데이터를 저장하는 함수
     * @param {*} loginData
     */
    setLoginData(loginData) {
      this.isLoggedIn = true
      this.accessToken = loginData.accessToken
      this.userId = loginData.userId
      // userProfilePic 업데이트 로직이 필요하다면 여기에 추가
      // 예: this.userProfilePic = `https://.../${loginData.userId}.png`

      // HTTP 서비스의 헤더에 토큰을 설정합니다.
      httpService.defaults.headers.common['Authorization'] = `Bearer ${this.accessToken}`

      console.log('Logged in:', this.accessToken, this.userId)
      console.log(httpService.defaults.headers.common['Authorization'])

      // 로그인 후 원하는 라우트로 리다이렉트. 예를 들어 홈으로 리다이렉트
      router.push('/')
    },
    /**
     * 로그아웃 함수
     */
    logout() {
      this.isLoggedIn = false
      this.accessToken = null
      this.userId = null
      this.userProfilePic = null
      // 필요하다면 HTTP 서비스의 헤더에서 토큰을 제거
      delete httpService.defaults.headers.common['Authorization']

      // 로그아웃 후 메인페이지로 리다이렉트
      router.push('/')
    }
  },
  persist: {
    enabled: true, // 상태를 로컬 스토리지에 저장합니다.
    strategies: [{ storage: localStorage }] // 로컬 스토리지에 저장합니다.
  }
})
