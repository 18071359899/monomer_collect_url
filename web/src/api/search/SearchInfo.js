import MyAxios from '@/utils/MyAxios.js'
export const searchRequest = (type, search) => {
    return MyAxios.get("/search/get/", { params: { type: type, search: search, page: 1, size: 10 } })
}