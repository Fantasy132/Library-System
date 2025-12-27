import request from '../utils/request';
import { ApiResponse, Book, BookRequest, Category, PageRequest, PageResponse } from '../types';

// 获取图书列表
export const getBooks = (params: PageRequest): Promise<ApiResponse<PageResponse<Book>>> => {
  return request.get('/api/books', { params });
};

// 获取图书详情
export const getBookById = (id: number): Promise<ApiResponse<Book>> => {
  return request.get(`/api/books/${id}`);
};

// 添加图书（管理员）
export const addBook = (data: BookRequest): Promise<ApiResponse<Book>> => {
  return request.post('/api/books', data);
};

// 更新图书（管理员）
export const updateBook = (id: number, data: BookRequest): Promise<ApiResponse<Book>> => {
  return request.put(`/api/books/${id}`, data);
};

// 删除图书（管理员）
export const deleteBook = (id: number): Promise<ApiResponse<void>> => {
  return request.delete(`/api/books/${id}`);
};

// 获取图书分类
export const getCategories = (): Promise<ApiResponse<Category[]>> => {
  return request.get('/api/categories');
};
