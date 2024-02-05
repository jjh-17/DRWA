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
      path: '/about',
      name: 'about'
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      // component: () => import('../views/AboutView.vue')
    },
    {
      path: '/search-results/:type/:query',
      name: 'SearchResults',
      component: SearchResults
    },
    {
      path: '/member/mypage',
      name: 'MyPageView',
      component: () => import('../views/MyPageView.vue')
    }
  ]
})

export default router
