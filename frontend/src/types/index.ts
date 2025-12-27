// 用户相关类型
export interface User {
  id: number;
  username: string;
  email: string;
  phone?: string;
  realName?: string;
  role: 'ADMIN' | 'USER';
  status: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
  email: string;
  phone?: string;
  realName?: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

// 图书相关类型
export interface Book {
  id: number;
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  publishDate: string;
  categoryId: number;
  categoryName?: string;
  price: number;
  totalStock: number;
  availableStock: number;
  coverUrl?: string;
  description?: string;
  status: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface BookRequest {
  isbn: string;
  title: string;
  author: string;
  publisher: string;
  publishDate: string;
  categoryId: number;
  price: number;
  totalStock: number;
  coverUrl?: string;
  description?: string;
  status: number;
}

export interface Category {
  id: number;
  name: string;
  parentId?: number;
  description?: string;
  children?: Category[];
}

// 借阅相关类型
export interface BorrowRecord {
  id: number;
  userId: number;
  username?: string;
  bookId: number;
  bookTitle?: string;
  bookIsbn?: string;
  borrowTime: string | number[];  // 可能是字符串或数组格式
  dueTime: string | number[];
  returnTime?: string | number[];
  renewCount: number;
  status: number; // 0-借阅中，1-已归还，2-已逾期，3-已续借
  statusDesc?: string;
  overdue?: boolean;
  overdueDays?: number;
  quantity?: number;
  remark?: string;
  createTime?: string | number[];
  updatedAt?: string;
}

export interface BorrowRequest {
  bookId: number;
  quantity: number;
  borrowDays: number;
}

export interface RenewRequest {
  renewDays: number;
}

// 分页相关类型
export interface PageRequest {
  page: number;
  size: number;
  keyword?: string;
  categoryId?: number;
  status?: number;
  userId?: number;
}

export interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// API 响应类型
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}
