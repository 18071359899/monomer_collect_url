import MyAxios from '@/utils/MyAxios.js'

export function previewFileApi(filePath, responseType,transformData) {
    const responseObject = { responseType }
    if(transformData)
    responseObject.transformResponse = [(async function (data) {
        return await transformData(data);
    }) ]
    //此请求需要设置responseType来解析返回的响应体，否则默认会解析成json，由于这里是获取文件流数据，必须指定
    return MyAxios.get("/uploadFile/preview/" + filePath, responseObject)
}