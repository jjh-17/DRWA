import { createRouter, createWebHistory } from 'vue-router'
import MainView from '../views/MainView.vue'
import SearchResults from '@/views/SearchResults.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: MainView
    },
    {
      path: '/member/login',
      name: 'AuthCallback',
      component: () => import('../views/AuthCallback.vue')
    },
    {
      path: '/member/mypage',
      name: 'MyPageView',
      component: () => import('../views/MyPageView.vue')
    },
    {
      path: '/search-results/:type/:query',
      name: 'SearchResults',
      component: SearchResults
    },
    {
      path: '/member/profile-edit',
      name: 'ProfileEdit',
      component: () => import('../views/ProfileEditView.vue')
    }
  ]
})

export default router
