import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback, useMemo } from 'react';
import { User } from '../types';
import * as authApi from '../api/auth';
import { message } from 'antd';

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  isAdmin: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 从 localStorage 恢复用户信息
    const userInfo = localStorage.getItem('userInfo');
    const token = localStorage.getItem('accessToken');

    if (userInfo && token) {
      try {
        setUser(JSON.parse(userInfo));
      } catch (error) {
        console.error('Failed to parse user info:', error);
        localStorage.removeItem('userInfo');
      }
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    try {
      const response = await authApi.login({ username, password });
      const { accessToken, refreshToken, user } = response.data;

      // 保存到 localStorage
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userInfo', JSON.stringify(user));

      setUser(user);
      message.success('登录成功');
    } catch (error) {
      console.error('Login error:', error);
      // 错误已由拦截器处理
      throw error;
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // 清除本地存储
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userInfo');
      setUser(null);
      message.success('已退出登录');
    }
  }, []);

  const isAdmin = useCallback(() => {
    return user?.role === 'ADMIN';
  }, [user]);

  const value = useMemo(() => ({
    user,
    loading,
    login,
    logout,
    isAdmin
  }), [user, loading, login, logout, isAdmin]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
