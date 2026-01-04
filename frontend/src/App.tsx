import React, { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider, Spin } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import MainLayout from './components/MainLayout';
import 'dayjs/locale/zh-cn';
import './App.css';

// 懒加载页面组件
const Login = lazy(() => import('./pages/Login'));
const BookList = lazy(() => import('./pages/BookList'));
const MyBorrows = lazy(() => import('./pages/MyBorrows'));
const AdminPanel = lazy(() => import('./pages/AdminPanel'));

// 加载组件
const PageLoader = () => (
  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
    <Spin size="large" tip="加载中..." />
  </div>
);

// 受保护的路由组件
const ProtectedRoute: React.FC<{ children: React.ReactElement }> = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <PageLoader />;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

// 管理员路由组件
const AdminRoute: React.FC<{ children: React.ReactElement }> = ({ children }) => {
  const { user, loading, isAdmin } = useAuth();

  if (loading) {
    return <PageLoader />;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!isAdmin()) {
    return <Navigate to="/books" replace />;
  }

  return children;
};

function App() {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: '#4F46E5', // 现代靛蓝色
          borderRadius: 8,
          colorBgContainer: '#ffffff',
        },
        components: {
          Layout: {
            bodyBg: '#f3f4f6',
            headerBg: '#ffffff',
          },
          Card: {
            boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
          }
        }
      }}
    >
      <AuthProvider>
        <BrowserRouter>
          <Suspense fallback={<PageLoader />}>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <MainLayout />
                  </ProtectedRoute>
                }
              >
                <Route index element={<Navigate to="/books" replace />} />
                <Route path="books" element={<BookList />} />
                <Route path="my-borrows" element={<MyBorrows />} />
                <Route
                  path="admin"
                  element={
                    <AdminRoute>
                      <AdminPanel />
                    </AdminRoute>
                  }
                />
              </Route>
              <Route path="*" element={<Navigate to="/books" replace />} />
            </Routes>
          </Suspense>
        </BrowserRouter>
      </AuthProvider>
    </ConfigProvider>
  );
}

export default App;
