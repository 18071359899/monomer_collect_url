import { defineStore } from 'pinia'
import { ref } from 'vue'
import MyAxios from '@/utils/MyAxios.js'

export const useCounterStore = defineStore('user', () => {
    const loginUserObject = ref({
        id: 0, username:'',photo:''
    })
    function getInfoByToken(success,fail) {
        MyAxios.get('/user/account/info/').then(res => {
            if(res.error_message == 'successfully'){
                loginUserObject.value = {
                    id: res.id,
                    username : res.username,
                    photo: res.photo
                }
                success()
            }else{
                fail()
            }
        }).catch(()=>{
            fail()
        })
    }
    function loginOut() {
        loginUserObject.value = {
            id: 0, username:'',photo:''
        }
    }
  
    return { loginUserObject, getInfoByToken,loginOut }
  })