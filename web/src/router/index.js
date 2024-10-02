import { createRouter, createWebHistory } from 'vue-router'
// import Tree from '../views/TreeView.vue'
import ManageSortViews from '@/views/manage_sort/ManageSortViews'
import LoginView from "@/views/user/account/LoginView.vue";
import ShareView from '@/views/share/ShareView'
import UserInfoView from '@/views/user/info/UserInfoView.vue'
import { useCounterStore } from '@/store/UserStore.js'
import EditShareView from '@/views/share/EditShareView.vue'
import ShareContent from '@/views/share/ShareContent.vue';
import SearchView from '@/views/search/SearchView.vue';
import MessageNotifyView from '@/views/message_notify/MessageNotifyView.vue';
import MessageShareContent from '@/views/share/MessageShareContent.vue';
const routes = [
  {
    path: '/',
    name: 'home',
    component: ShareView,
    meta: {
      Authorize: true
    }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      Authorize: false
    }
  },
  {
    path: '/manageSort',
    name: 'manageSort',
    component: ManageSortViews,
    meta: {
      Authorize: true
    }
  },
  {
    path: '/share',
    name: 'share',
    component: ShareView,
    meta: {
      Authorize: true
    }
  },
  {
    path: '/share/:shareId',
    name: 'showShare',
    component: ShareContent,
    meta: {
      Authorize: true
    }
  },
  {
    path: '/share/:shareId/:commentId',
    name: 'messageShowShare',
    component: MessageShareContent,
    meta: {
      Authorize: true
    }
  },
  {
    path: '/user/:userInfoId',
    name: 'userInfo',
    component: UserInfoView,
    meta: {
      Authorize: true
    }
  },
  {
    path: '/search',
    name: 'search',
    component: SearchView,
    meta: {
      Authorize: true
    }
  },
  {
    path: '/share/update',
    name: 'shareUpdate',
    component: EditShareView,
    children:[
      {
        path: ':shareId',
        name: 'shareUpdateId',
        component: EditShareView,
      }
    ],
    meta: {
      Authorize: true
    }
  },
  {
    path: '/messageNotify',
    name: 'messageNotify',
    component: MessageNotifyView,
    meta: {
      Authorize: true
    }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
const setNextRouter = (to, from, next,loginUserObject) => {
    if(loginUserObject.id !== 0 && to.name === 'login'){  //当前已登录，但是访问了登录页面，重定向到首页
      next({ name:'home' })
    }
    else if(to.meta.Authorize === true && loginUserObject.id === 0){  //当前页面需要授权，但未登录
      next({ name: 'login' })
    }
    else{
      next()
    }
}
router.beforeEach((to, from, next) => {
  const UserStore = useCounterStore()
  const token = localStorage.getItem('COLLECT_URL_TOKEN');
    if(token && UserStore.loginUserObject.id === 0){
      UserStore.getInfoByToken(()=>{  //success的回调
        setNextRouter(to,from,next,UserStore.loginUserObject)
      },()=>{   //fail的回调
        localStorage.removeItem('COLLECT_URL_TOKEN');
        next({ name: 'login' })
      })
    }else{
      setNextRouter(to,from,next,UserStore.loginUserObject)
    }
})
router.afterEach(() => {
  
})


export default router
