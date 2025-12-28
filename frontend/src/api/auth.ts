import request from '../utils/request';
import {
  ApiResponse,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  User,
  PageResponse,
} from '../types';

// 用户登录
export const login = (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
  return request.post('/api/auth/login', data);
};

// 用户注册
export const register = (data: RegisterRequest): Promise<ApiResponse<void>> => {
  return request.post('/api/auth/register', data);
};

// 退出登录
export const logout = (): Promise<ApiResponse<void>> => {
  return request.post('/api/auth/logout');
};

// 验证 Token
export const verifyToken = (token: string): Promise<ApiResponse<boolean>> => {
  return request.post('/api/auth/verify', { token });
};

// 刷新 Token
export const refreshToken = (): Promise<ApiResponse<LoginResponse>> => {
  return request.post('/api/auth/refresh');
};

// ========== 用户管理API（管理员） ==========

// 获取用户列表
export const getUsers = (params: { page?: number; size?: number; keyword?: string }): Promise<ApiResponse<PageResponse<User>>> => {
  return request.get('/api/users', {
    params: {
      pageNum: params.page || 1,
      pageSize: params.size || 10,
      keyword: params.keyword,
    },
  });
};

// 获取用户详情
export const getUserById = (id: number): Promise<ApiResponse<User>> => {
  return request.get(`/api/users/${id}`);
};

// 修改用户密码
export const updateUserPassword = (userId: number, newPassword: string): Promise<ApiResponse<void>> => {
  return request.put(`/api/users/${userId}/password`, { newPassword });
};

// 修改用户角色
export const updateUserRole = (userId: number, role: string): Promise<ApiResponse<void>> => {
  return request.put(`/api/users/${userId}/role`, { role });
};

// 修改用户状态
export const updateUserStatus = (userId: number, status: number): Promise<ApiResponse<void>> => {
  return request.put(`/api/users/${userId}/status`, null, { params: { status } });
};
