import SparkMD5 from 'spark-md5'
import { ElMessage } from 'element-plus'

const computeChunks = (totalSize, chunkSize) => {
    return Math.ceil(totalSize / chunkSize)
}
// 生成文件 hash
self.onmessage = e => {
    let { fileItem } = e.data
    let file = fileItem.file
    let blobSize = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice //获取file对象的slice方法，确保在每个浏览器都能获取到
    const fileSize = file.size
    const chunkSize = 1024 * 1024 * 10  //每个分片10M
    let chunks = computeChunks(fileSize, chunkSize)
    let currentChunkIndex = 0 //当前读到第几个分片，最开始为0
    let spark = new SparkMD5.ArrayBuffer();
    let fileReader = new FileReader();
    let loadNext = () => {
        let start = currentChunkIndex * chunkSize
        let end = start + chunkSize >= fileSize ? fileSize : start + chunkSize
        fileReader.readAsArrayBuffer(blobSize.call(file, start, end))
    }
    loadNext()
    fileReader.onload = (e) => {  //读取成功后调用
        spark.append(e.target.result)
        currentChunkIndex++
        if (currentChunkIndex < chunks) { //继续分片 
            let percent = Math.floor(currentChunkIndex / chunks * 100)
            self.postMessage({
                percentage: percent,
            });
            loadNext()
        } else {
            let md5 = spark.end()
            spark.destroy()
            self.postMessage({
                md5,
                percentage: 100
            });
            loadNext = null
            self.close(); // 关闭 worker 线程，线程如果不关闭，则会一直在后台运行着，
        }
    }
    fileReader.onerror = (e) => {  //读取出错调用
        console.log(e);
        self.close();
        ElMessage.error('读取文件出错')
        loadNext = null
    }
};