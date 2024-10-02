import MyAxios from '@/utils/MyAxios.js'
export let getCommentRequest = (pageObject,props) => {
    return MyAxios.get("/comment/list/", { params: { page: pageObject.page, size: pageObject.size, shareId: props.shareId } })
}
export let getMessageCommentRequest = (props) => {
    return MyAxios.get("/comment/message/list/", { params: { shareId: props.shareId,commentId: props.findCommentId } })
}
export let addCommentRequest = (comment) => {
    return MyAxios.post("/comment/add/", comment)
}
export let deleteCommentRequest = (comment) => {
    return MyAxios.post("/comment/delete/",comment)
}

export let messageLastListCommentRequest = (pageObject,props) =>{
    return MyAxios.get("/comment/message/list/last/", { params: { page: pageObject.page, size: pageObject.size,
        shareId: props.shareId,commentId: props.findCommentId } })
}