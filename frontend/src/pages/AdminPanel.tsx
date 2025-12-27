import React, { useState, useEffect } from 'react';
import {
  Tabs,
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  DatePicker,
  message,
  Tag,
  Card,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { Book, BookRequest, BorrowRecord, Category } from '../types';
import * as bookApi from '../api/book';
import * as borrowApi from '../api/borrow';
import dayjs from 'dayjs';

const { TabPane } = Tabs;
const { Option } = Select;
const { TextArea } = Input;

// 辅助函数：将后端返回的日期数组或字符串转换为dayjs对象
const parseDate = (date: string | number[] | undefined | null): dayjs.Dayjs | null => {
  if (!date) return null;
  if (Array.isArray(date)) {
    // 数组格式: [2025, 12, 20, 10, 0, 0]
    const [year, month, day, hour = 0, minute = 0, second = 0] = date;
    return dayjs(new Date(year, month - 1, day, hour, minute, second));
  }
  return dayjs(date);
};

const AdminPanel: React.FC = () => {
  const [activeTab, setActiveTab] = useState('books');
  const [books, setBooks] = useState<Book[]>([]);
  const [borrows, setBorrows] = useState<BorrowRecord[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [form] = Form.useForm();
  const [bookPage, setBookPage] = useState(1);
  const [bookPageSize, setBookPageSize] = useState(10);
  const [bookTotal, setBookTotal] = useState(0);
  const [borrowPage, setBorrowPage] = useState(1);
  const [borrowPageSize, setBorrowPageSize] = useState(10);
  const [borrowTotal, setBorrowTotal] = useState(0);

  useEffect(() => {
    if (activeTab === 'books') {
      fetchBooks();
      fetchCategories();
    } else if (activeTab === 'borrows') {
      fetchBorrows();
    }
  }, [activeTab, bookPage, bookPageSize, borrowPage, borrowPageSize]);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const response = await bookApi.getBooks({
        page: bookPage,
        size: bookPageSize,
      });
      setBooks(response.data.records);
      setBookTotal(response.data.total);
    } catch (error) {
      // 错误消息已在request拦截器中显示
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await bookApi.getCategories();
      setCategories(response.data);
    } catch (error) {
      console.error('获取分类失败:', error);
    }
  };

  const fetchBorrows = async () => {
    setLoading(true);
    try {
      const response = await borrowApi.getBorrowRecords({
        page: borrowPage,
        size: borrowPageSize,
      });
      setBorrows(response.data.records);
      setBorrowTotal(response.data.total);
    } catch (error) {
      // 错误已由拦截器处理
    } finally {
      setLoading(false);
    }
  };

  const handleAddBook = () => {
    setEditingBook(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEditBook = (book: Book) => {
    setEditingBook(book);
    form.setFieldsValue({
      ...book,
      publishDate: dayjs(book.publishDate),
    });
    setModalVisible(true);
  };

  const handleDeleteBook = (book: Book) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除《${book.title}》吗？`,
      onOk: async () => {
        try {
          await bookApi.deleteBook(book.id);
          message.success('删除成功');
          fetchBooks();
        } catch (error) {
          // 错误已由拦截器处理
        }
      },
    });
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const bookData: BookRequest = {
        ...values,
        publishDate: values.publishDate.format('YYYY-MM-DD'),
      };

      if (editingBook) {
        await bookApi.updateBook(editingBook.id, bookData);
        message.success('更新成功');
      } else {
        await bookApi.addBook(bookData);
        message.success('添加成功');
      }

      setModalVisible(false);
      fetchBooks();
    } catch (error) {
      // 错误已由拦截器处理
    }
  };

  const bookColumns: ColumnsType<Book> = [
    {
      title: 'ISBN',
      dataIndex: 'isbn',
      key: 'isbn',
      width: 150,
      render: (text) => <span style={{ whiteSpace: 'nowrap' }}>{text}</span>,
    },
    {
      title: '书名',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '作者',
      dataIndex: 'author',
      key: 'author',
      width: 120,
    },
    {
      title: '出版社',
      dataIndex: 'publisher',
      key: 'publisher',
      width: 150,
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 100,
      render: (price) => `¥${price.toFixed(2)}`,
    },
    {
      title: '库存',
      key: 'stock',
      width: 120,
      render: (_, record) => `${record.availableStock} / ${record.totalStock}`,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '上架' : '下架'}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEditBook(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDeleteBook(record)}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  const borrowColumns: ColumnsType<BorrowRecord> = [
    {
      title: '用户',
      dataIndex: 'username',
      key: 'username',
      width: 120,
    },
    {
      title: '书名',
      dataIndex: 'bookTitle',
      key: 'bookTitle',
    },
    {
      title: 'ISBN',
      dataIndex: 'bookIsbn',
      key: 'bookIsbn',
      width: 150,
      render: (text) => <span style={{ whiteSpace: 'nowrap' }}>{text}</span>,
    },
    {
      title: '借阅日期',
      dataIndex: 'borrowTime',
      key: 'borrowTime',
      width: 120,
      render: (date) => {
        const dateObj = parseDate(date);
        return dateObj ? dateObj.format('YYYY-MM-DD') : '-';
      },
    },
    {
      title: '应还日期',
      dataIndex: 'dueTime',
      key: 'dueTime',
      width: 120,
      render: (date, record) => {
        const dateObj = parseDate(date);
        if (!dateObj) return '-';
        const isOverdue = dayjs().isAfter(dateObj) && record.status === 0;
        return (
          <span style={{ color: isOverdue ? 'red' : 'inherit' }}>
            {dateObj.format('YYYY-MM-DD')}
          </span>
        );
      },
    },
    {
      title: '归还日期',
      dataIndex: 'returnTime',
      key: 'returnTime',
      width: 120,
      render: (date) => {
        const dateObj = parseDate(date);
        return dateObj ? dateObj.format('YYYY-MM-DD') : '-';
      },
    },
    {
      title: '状态',
      key: 'status',
      width: 100,
      render: (_, record) => {
        const dueTimeObj = parseDate(record.dueTime);
        const isOverdue = dueTimeObj ? dayjs().isAfter(dueTimeObj) : false;
        if (record.status === 1) {
          return <Tag color="success">已归还</Tag>;
        } else if (record.status === 2 || isOverdue) {
          return <Tag color="error">已逾期</Tag>;
        } else {
          return <Tag color="processing">借阅中</Tag>;
        }
      },
    },
  ];

  return (
    <Card>
      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab="图书管理" key="books">
          <Space style={{ marginBottom: 16 }}>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleAddBook}
            >
              添加图书
            </Button>
          </Space>

          <Table
            columns={bookColumns}
            dataSource={books}
            rowKey="id"
            loading={loading}
            pagination={{
              current: bookPage,
              pageSize: bookPageSize,
              total: bookTotal,
              showSizeChanger: true,
              showTotal: (total) => `共 ${total} 条`,
              onChange: (page, pageSize) => {
                setBookPage(page);
                setBookPageSize(pageSize);
              },
            }}
          />
        </TabPane>

        <TabPane tab="借阅记录" key="borrows">
          <Table
            columns={borrowColumns}
            dataSource={borrows}
            rowKey="id"
            loading={loading}
            pagination={{
              current: borrowPage,
              pageSize: borrowPageSize,
              total: borrowTotal,
              showSizeChanger: true,
              showTotal: (total) => `共 ${total} 条`,
              onChange: (page, pageSize) => {
                setBorrowPage(page);
                setBorrowPageSize(pageSize);
              },
            }}
          />
        </TabPane>
      </Tabs>

      {/* 添加/编辑图书弹窗 */}
      <Modal
        title={editingBook ? '编辑图书' : '添加图书'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="isbn"
            label="ISBN"
            rules={[{ required: true, message: '请输入ISBN' }]}
          >
            <Input placeholder="978-7-111-12345-6" />
          </Form.Item>

          <Form.Item
            name="title"
            label="书名"
            rules={[{ required: true, message: '请输入书名' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="author"
            label="作者"
            rules={[{ required: true, message: '请输入作者' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="publisher"
            label="出版社"
            rules={[{ required: true, message: '请输入出版社' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="publishDate"
            label="出版日期"
            rules={[{ required: true, message: '请选择出版日期' }]}
          >
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="categoryId"
            label="分类"
            rules={[{ required: true, message: '请选择分类' }]}
          >
            <Select placeholder="请选择分类">
              {categories.map((cat) => (
                <Option key={cat.id} value={cat.id}>
                  {cat.name}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="price"
            label="价格"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <InputNumber
              min={0}
              precision={2}
              style={{ width: '100%' }}
              addonBefore="¥"
            />
          </Form.Item>

          <Form.Item
            name="totalStock"
            label="总库存"
            rules={[{ required: true, message: '请输入总库存' }]}
          >
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="coverUrl" label="封面URL">
            <Input placeholder="https://example.com/cover.jpg" />
          </Form.Item>

          <Form.Item name="description" label="简介">
            <TextArea rows={4} />
          </Form.Item>

          <Form.Item
            name="status"
            label="状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select>
              <Option value={1}>上架</Option>
              <Option value={0}>下架</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default AdminPanel;
