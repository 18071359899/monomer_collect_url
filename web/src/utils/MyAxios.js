import axios from 'axios';
import { ElMessage } from 'element-plus'
const intance = axios.create({
    baseURL: process.env.VUE_APP_BASE_URL,
    timeout: 1000000,
    headers: {}
});


// 添加请求拦截器
intance.interceptors.request.use(function (config) {
    //在全局设置认证token
    const token = localStorage.getItem('COLLECT_URL_TOKEN'); // 从localStorage中取出token
    if (token) {
        config.headers.Authorization = `Bearer ${token}`; // 设置请求头中的Authorization字段
    }
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
intance.interceptors.response.use(function (response) {
    if (response.data.code !== 0) {
        if (response.data.message != null) {
            ElMessage({
                message: response.data.message,
                type: 'warning',
            })
        }

        return response.data;
    }
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    return response.data.data;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    ElMessage.error('访问错误')

    return Promise.reject(error);
});

export default intance;