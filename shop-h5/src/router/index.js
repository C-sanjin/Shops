import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/Index.vue')
  },
  {
    path: '/product',
    name: 'ProductList',
    component: () => import('@/views/product/List.vue')
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/product/Detail.vue')
  },
  {
    path: '/store',
    name: 'StoreList',
    component: () => import('@/views/store/List.vue')
  },
  {
    path: '/store/:id',
    name: 'StoreDetail',
    component: () => import('@/views/store/Detail.vue')
  },
  {
    path: '/order/create',
    name: 'OrderCreate',
    component: () => import('@/views/order/Create.vue')
  },
  {
    path: '/order',
    name: 'OrderList',
    component: () => import('@/views/order/List.vue')
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/order/Detail.vue')
  },
  {
    path: '/payment/:orderNo',
    name: 'Payment',
    component: () => import('@/views/payment/Index.vue')
  },
  {
    path: '/verify/:orderId',
    name: 'VerifyCode',
    component: () => import('@/views/verification/Code.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue')
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/user/Center.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.name !== 'Login' && to.name !== 'Register' && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
