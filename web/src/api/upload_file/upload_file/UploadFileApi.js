import MyAxios from '@/utils/MyAxios.js'

export function addUploadFileRequest(data) {
    const { monthDay, realFileName, md5, fileName, pid,fileSize } = data
    return MyAxios.post("/file/add/uploadFile", { monthDay, realFileName, md5, fileName, pid,fileSize })
}

export function resetFileName(data) {
    const { pid,fileName,id } = data
    return MyAxios.post("/file/update/fileName", { pid, fileName,id })
}
export function selectCurrUserIsSpaceRequest(fileSize){
    return MyAxios.get("/file/get/space/success", { params: {fileSize} })
}

export function getFileTypeDataRequest(type){
    return MyAxios.get("/fileType/get/", { params: {type} })
}

export function downloadFileRequest(data){
    const { path,responseType } = data
    return MyAxios.get("/file/download/" + path, { responseType })
}

export function getDownloadCode(){
    return MyAxios.get("/file/get/download/code")
}
