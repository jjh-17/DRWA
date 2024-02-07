// Axios Config
// https://axios-http.com/docs/config_defaults
import axios from 'axios'
import { httpStatusCode } from './http-status'

// 환경변수에서 서버의 기본 URL을 가져온다.
const { VITE_SERVER_URL } = import.meta.env

// CORS 정책을 허용한다. => 다른 주소의 요청을 방지
// https://axios-http.com/docs/req_config
export const httpService = axios.create({
  baseURL: VITE_SERVER_URL, // 서버의 기본 URL
  withCredentials: true // CORS 정책 허용. 쿠키를 전송할 수 있게 함.
})

// Request 발생 시 적용할 내용.
httpService.defaults.headers.common['Authorization'] = '' // 인증 토큰을 저장할 헤더
httpService.defaults.headers.post['Content-Type'] = 'application/json' // POST 요청 시, JSON 형식으로 변환
httpService.defaults.headers.put['Content-Type'] = 'application/json' // PUT 요청 시, JSON 형식으로 변환

// Request, Response 시 설정한 내용을 적용.
httpService.interceptors.request.use((config) => {
  return config
}),
  (error) => {
    return Promise.reject(error)
  }

// accessToken의 값이 유효하지 않은 경우,
// refreshToken을 이용해 재발급 처리.
// https://maruzzing.github.io/study/rnative/axios-interceptors%EB%A1%9C-%ED%86%A0%ED%81%B0-%EB%A6%AC%ED%94%84%EB%A0%88%EC%8B%9C-%ED%95%98%EA%B8%B0/

//토큰 리프레시 중복 요청을 방지하기 위한 플래그
let isTokenRefreshing = false

// 리프레시 토큰 요청 중 대기하는 요청들을 저장할 배열
let subscribers = []

// 새 토큰이 리프레시된 후 대기 중이던 요청들을 재실행하는 함수
function onTokenRefreshed(newAccessToken) {
  subscribers.forEach((callback) => callback(newAccessToken))
  subscribers = []
}

// 대기 중인 요청을 subscribes 배열에 추가하는 함수
function addSubscriber(callback) {
  subscribers.push(callback)
}

httpService.interceptors.response.use(
  //토근이 만료되지 않은 경우
  (response) => {
    return response
  },
  // 토큰이 만료된 경우
  async (error) => {
    // 주입된 에러 객체에서 config와 response를 가져온다.
    const { config, response } = error
    if (response) {
      const { status } = response

      // 원래 요청 정보를 저장
      const originalRequest = config

      // accessToken 자체가 만료되어 더 이상 진행할 수 없는 경우.
      if (status == httpStatusCode.UNAUTHORIZED) {
        // 토큰 리프레시 첫 시작이면
        if (!isTokenRefreshing) {
          // Token을 재발급하는 동안 다른 요청이 발생하는 경우 대기.
          // (다른 요청을 진행하면, 새로 발급 받은 Token이 유효하지 않게 됨.)
          isTokenRefreshing = true

          try {
            // 리프레시 토큰 요청을 서버에 보냅니다.
            const { data } = await httpService.post('/silent-refresh')
            // 새 엑세스 토큰을 응답에서 가져옵니다.
            const newAccessToken = data.accessToken

            // Axios 인스턴스와 원래 요청의 헤더에 새 엑세스 토큰을 설정합니다.
            httpService.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`
            originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`

            // 리프레시 토큰 요청이 완료되었으므로 플래그를 false로 설정합니다.
            isTokenRefreshing = false
            // 대기 중이던 요청들을 새 토큰으로 재실행합니다.
            onTokenRefreshed(newAccessToken)

            // 원래 요청을 새 토큰으로 재시도합니다
            return httpService(originalRequest)
          } catch (refreshError) {
            // 리프레시 토큰 요청이 실패한 경우
            isTokenRefreshing = false
            // 토큰 리프레시 실패 시 오류를 기록합니다.
            console.error('Unable to refresh token', refreshError)
            // 토큰 리프레시 실패 시 거부된 Promise 반환하여 오류 처리 가능하게 함
            return Promise.reject(refreshError)
          }
        } else if (isTokenRefreshing) {
          // 리프레시 토큰 요청이 이미 진행 중인 경우, 새 요청을 대기열에 추가합니다.
          return new Promise((resolve) => {
            addSubscriber((newToken) => {
              originalRequest.headers['Authorization'] = `Bearer ${newToken}`
              resolve(httpService(originalRequest)) // 대기 중이던 요청을 새 토큰으로 재실행합니다.
            })
          })
        }
      }
      return Promise.reject(error) // 401 외의 다른 오류에 대해서는 에러를 그대로 반환합니다.
    } else {
      // response 객체가 없는 경우의 에러 처리
      console.error('No response from server', error)
      return Promise.reject(error)
    }
  }
)
