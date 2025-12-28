import request from '../utils/request';
import {
  ApiResponse,
  BorrowRecord,
  BorrowRequest,
  PageRequest,
  PageResponse,
  RenewRequest,
} from '../types';

// 借阅图书
export const borrowBook = (data: BorrowRequest): Promise<ApiResponse<BorrowRecord>> => {
  return request.post('/api/borrow', data);
};

// 归还图书
export const returnBook = (id: number): Promise<ApiResponse<void>> => {
  return request.post('/api/return', { borrowId: id });
};

// 续借图书
export const renewBook = (id: number, data: RenewRequest): Promise<ApiResponse<BorrowRecord>> => {
  return request.post('/api/renew', { borrowId: id, ...data });
};

// 查询我的借阅
export const getMyBorrows = (params?: { page?: number; size?: number; status?: number }): Promise<ApiResponse<PageResponse<BorrowRecord>>> => {
  return request.get('/api/borrow/my', {
    params: {
      pageNum: params?.page || 1,
      pageSize: params?.size || 10,
      status: params?.status,
    },
  });
};

// 查询借阅记录（分页）
export const getBorrowRecords = (
  params: PageRequest
): Promise<ApiResponse<PageResponse<BorrowRecord>>> => {
  return request.get('/api/borrow/all', { params });
};
