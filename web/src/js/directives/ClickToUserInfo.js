import router from "@/router"

//自定义指令：点击某个用户头像，跳转到某个用户的个人中心页面
export const vClickToUserInfoFocus = {
    mounted: (el,binding) => {
        el.addEventListener('click',(event)=>{
            router.push({name: 'userInfo',params:{ userInfoId: binding.value}})
        })
    }
}