import { createRouter, createWebHistory } from 'vue-router'
import MainView from '../views/MainView.vue'
import AuthCallback from '../views/AuthCallback.vue'
import MyPageView from '../views/MyPageView.vue'
import SearchResults from '@/views/SearchResults.vue'
import ThumbnailImg from '../components/room/ThumbnailImg.vue'
import DebateView from '@/views/DebateView.vue'
import RankingView from '@/views/RankingView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: MainView
    },
    {
      path: 'api/member/login',
      name: 'AuthCallback',
      component: AuthCallback
    },
    {
      path: '/member/mypage',
      name: 'MyPageView',
      component: MyPageView
    },
    {
      path: '/search-results/:type/:query',
      name: 'SearchResults',
      component: SearchResults
    },
    // '/member/mypage' 경로가 중복되어 있으므로, 한 개를 제거합니다.
    {
      path: '/image',
      name: 'ThumbnailImg', // 라우트에 이름을 추가합니다.
      component: ThumbnailImg
    },
    {
      path: '/debate/:debateId',
      name: 'DebateView',
      component: DebateView
    },
    {
      path: '/member/profile-edit',
      name: 'ProfileEdit',
      component: () => import('../views/ProfileEditView.vue')
    },
    {
      path: '/ranking',
      name: 'RankingView',
      component: RankingView
    }
  ]
})

export default router
