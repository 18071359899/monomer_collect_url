import MyAxios from '@/utils/MyAxios.js'
export function uploaderFileApi(Data,handleOnUploadProgress) {
    const { file, fileMd5, chunkIndex, chunks } = Data
    const formData = new FormData();
    formData.append('multipartFile', file);
    // if(fileCode !== undefined && fileCode !== null)
    //     formData.append('fileCode', fileCode);
    formData.append('fileMd5', fileMd5);
    formData.append('chunkIndex', chunkIndex);
    formData.append('chunks', chunks);
    // formData.append('fileName', fileName);
    return MyAxios.post("/file/common/uploadFile", formData,{
        onUploadProgress: function(progressEvent) {
          if(handleOnUploadProgress)
          handleOnUploadProgress(progressEvent)
        }
      })
}

export function queryUploadFileApi(fileMd5,pid,fileSize,fileName) {
    return MyAxios.post(`/file/query/uploadFile`,{params:{fileMd5,pid,fileSize,fileName}})
}

export function unionUploadFileApi(Data){
    const { fileName,fileMd5 } = Data
    return MyAxios.post(`/file/union/uploadFile?fileName=${fileName}&fileMd5=${fileMd5}`)
}