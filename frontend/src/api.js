// // src/api.js
// import axios from 'axios'
//
// // 创建 axios 实例
// const api = axios.create({
//     baseURL: 'http://localhost:8080',  // 后端API的基准URL
//     timeout: 10000,
// })
//
// // 获取回答时间数据
// export const getAnswerTime = () => api.get('/answer/time')
//
// // 获取回答声誉数据
// export const getAnswerReputation = () => api.get('/answer/reputation')
//
// // 获取自定义回答数据
// export const getAnswerCustom = () => api.get('/answer/custom')
//
// // 获取指定话题的数据
// export const getTopic = (topic) => api.get('/topic', { params: { topic } })
//
// // 获取热门话题
// export const getTopTopic = (size) => api.get('/topic/top', { params: { size } })
//
// // 获取异常数据
// export const getTopException = (size) => api.get('/exception/top', { params: { size } })
// export const getException = (exceptionName) => api.get('/exception', { params: { exceptionName } })
