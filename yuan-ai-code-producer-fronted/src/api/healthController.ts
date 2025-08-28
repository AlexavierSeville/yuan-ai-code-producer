// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 健康检查 返回 OK 表示服务可用 GET /health */
export async function health(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/health', {
    method: 'GET',
    ...(options || {}),
  })
}
