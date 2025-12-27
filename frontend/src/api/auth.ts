import request from '../utils/request';
import {
  ApiResponse,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  User,
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
