import MyAxios from '@/utils/MyAxios.js'
export const followUserRequest = (userId,followUserId)=>{
    return MyAxios.post('/user/follow/add/',{userId,followUserId})
}
export const followCancelUserRequest = (userId,followUserId)=>{
    return MyAxios.post('/user/follow/cancel/',{userId,followUserId})
}