import MyAxios from '@/utils/MyAxios.js'
export function listMessageNotify(userId,page,size) {
    return MyAxios.get("/messageNotify/list/", { params: { userId: userId, page, size } })
}

export const allReadMessageNotify = (userId) => {
    return MyAxios.post("/messageNotify/allRead/", { userId: userId })
}
export const readMessageNotify = (messageNotifyId) => {
    return MyAxios.post("/messageNotify/read/?messageNotifyId="+messageNotifyId)
}
export const totalMessageNotify = () => {
    return MyAxios.get("/messageNotify/total/")
}