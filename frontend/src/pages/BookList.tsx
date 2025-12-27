import React, { useState, useEffect } from 'react';
import {
  Table,
  Input,
  Button,
  Space,
  Tag,
  Modal,
  message,
  Select,
  Card,
  Row,
  Col,
  Statistic,
  InputNumber,
  Radio,
} from 'antd';
import { SearchOutlined, BookOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { Book, Category } from '../types';
import * as bookApi from '../api/book';
import * as borrowApi from '../api/borrow';
import { useAuth } from '../contexts/AuthContext';
import dayjs from 'dayjs';

const { Search } = Input;
const { Option } = Select;

const BookList: React.FC = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [categoryId, setCategoryId] = useState<number | undefined>();
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [detailVisible, setDetailVisible] = useState(false);
  const [borrowVisible, setBorrowVisible] = useState(false);
  const [borrowDays, setBorrowDays] = useState(30);
  const [customDays, setCustomDays] = useState<number | undefined>();
  const [isCustomDays, setIsCustomDays] = useState(false);
  const { isAdmin } = useAuth();

  useEffect(() => {
    fetchBooks();
    fetchCategories();
  }, [page, pageSize, keyword, categoryId]);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const response = await bookApi.getBooks({
        page,
        size: pageSize,
        keyword,
        categoryId,
        status: 1, // 只显示上架的图书
      });
      setBooks(response.data.records);
      setTotal(response.data.total);
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

  const handleSearch = (value: string) => {
    setKeyword(value);
    setPage(1);
  };

  const handleCategoryChange = (value: number | undefined) => {
    setCategoryId(value);
    setPage(1);
  };

  const showBookDetail = async (book: Book) => {
    try {
      const response = await bookApi.getBookById(book.id);
      setSelectedBook(response.data);
      setDetailVisible(true);
    } catch (error) {
      // 错误消息已在request拦截器中显示
    }
  };

  const handleBorrow = (book: Book) => {
    setSelectedBook(book);
    setBorrowVisible(true);
  };

  const confirmBorrow = async () => {
    if (!selectedBook) return;

    // 验证自定义天数
    const days = isCustomDays ? customDays : borrowDays;
    if (!days || days <= 0 || !Number.isInteger(days)) {
      message.error('请输入有效的借阅天数（必须是正整数）');
      return;
    }

    try {
      await borrowApi.borrowBook({
        bookId: selectedBook.id,
        quantity: 1,  // 默认借阅1本
        borrowDays: days,
      });
      message.success('借阅成功');
      setBorrowVisible(false);
      setIsCustomDays(false);
      setCustomDays(undefined);
      fetchBooks();
    } catch (error) {
      // 错误消息已在request拦截器中显示
    }
  };

  const columns: ColumnsType<Book> = [
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
      render: (text, record) => (
        <a onClick={() => showBookDetail(record)}>{text}</a>
      ),
    },
    {
      title: '作者',
      dataIndex: 'author',
      key: 'author',
      width: 150,
    },
    {
      title: '出版社',
      dataIndex: 'publisher',
      key: 'publisher',
      width: 150,
    },
    {
      title: '分类',
      dataIndex: 'categoryName',
      key: 'categoryName',
      width: 100,
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
      render: (_, record) => (
        <span>
          {record.availableStock} / {record.totalStock}
        </span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'availableStock',
      key: 'status',
      width: 100,
      render: (stock) => (
        <Tag color={stock > 0 ? 'green' : 'red'}>
          {stock > 0 ? '可借阅' : '已借完'}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => showBookDetail(record)}>
            详情
          </Button>
          {record.availableStock > 0 && (
            <Button type="primary" size="small" onClick={() => handleBorrow(record)}>
              借阅
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          <Col span={8}>
            <Statistic
              title="图书总数"
              value={total}
              prefix={<BookOutlined />}
            />
          </Col>
        </Row>
      </Card>

      <Card>
        <Space style={{ marginBottom: 16 }} size="middle">
          <Search
            placeholder="搜索书名、作者、ISBN"
            allowClear
            enterButton={<SearchOutlined />}
            style={{ width: 300 }}
            onSearch={handleSearch}
          />
          <Select
            placeholder="选择分类"
            allowClear
            style={{ width: 200 }}
            onChange={handleCategoryChange}
          >
            {categories.map((cat) => (
              <Option key={cat.id} value={cat.id}>
                {cat.name}
              </Option>
            ))}
          </Select>
          <Button icon={<ReloadOutlined />} onClick={fetchBooks}>
            刷新
          </Button>
        </Space>

        <Table
          columns={columns}
          dataSource={books}
          rowKey="id"
          loading={loading}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => {
              setPage(page);
              setPageSize(pageSize);
            },
          }}
        />
      </Card>

      {/* 图书详情弹窗 */}
      <Modal
        title="图书详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailVisible(false)}>
            关闭
          </Button>,
          selectedBook && selectedBook.availableStock > 0 && (
            <Button
              key="borrow"
              type="primary"
              onClick={() => {
                setDetailVisible(false);
                handleBorrow(selectedBook);
              }}
            >
              借阅
            </Button>
          ),
        ]}
        width={600}
      >
        {selectedBook && (
          <div>
            <p><strong>ISBN:</strong> {selectedBook.isbn}</p>
            <p><strong>书名:</strong> {selectedBook.title}</p>
            <p><strong>作者:</strong> {selectedBook.author}</p>
            <p><strong>出版社:</strong> {selectedBook.publisher}</p>
            <p><strong>出版日期:</strong> {selectedBook.publishDate}</p>
            <p><strong>分类:</strong> {selectedBook.categoryName}</p>
            <p><strong>价格:</strong> ¥{selectedBook.price.toFixed(2)}</p>
            <p>
              <strong>库存:</strong> {selectedBook.availableStock} / {selectedBook.totalStock}
            </p>
            {selectedBook.description && (
              <p><strong>简介:</strong> {selectedBook.description}</p>
            )}
          </div>
        )}
      </Modal>

      {/* 借阅确认弹窗 */}
      <Modal
        title="借阅图书"
        open={borrowVisible}
        onOk={confirmBorrow}
        onCancel={() => setBorrowVisible(false)}
      >
        {selectedBook && (
          <div>
            <p><strong>书名:</strong> {selectedBook.title}</p>
            <p><strong>作者:</strong> {selectedBook.author}</p>
            <p>
              <strong>借阅天数:</strong>
              <div style={{ marginTop: 8 }}>
                <Radio.Group
                  value={isCustomDays ? 'custom' : borrowDays}
                  onChange={(e) => {
                    const val = e.target.value;
                    if (val === 'custom') {
                      setIsCustomDays(true);
                    } else {
                      setIsCustomDays(false);
                      setBorrowDays(val);
                    }
                  }}
                >
                  <Space direction="vertical">
                    <Radio value={7}>7天</Radio>
                    <Radio value={14}>14天</Radio>
                    <Radio value={30}>30天</Radio>
                    <Radio value={60}>60天</Radio>
                    <Radio value="custom">
                      自定义天数：
                      {isCustomDays && (
                        <InputNumber
                          min={1}
                          max={365}
                          precision={0}
                          value={customDays}
                          onChange={(val) => setCustomDays(val || undefined)}
                          placeholder="请输入天数"
                          style={{ width: 120, marginLeft: 8 }}
                          onClick={(e) => e.stopPropagation()}
                        />
                      )}
                    </Radio>
                  </Space>
                </Radio.Group>
              </div>
            </p>
            <p>
              <strong>应还日期:</strong>{' '}
              {dayjs().add(isCustomDays ? (customDays || 0) : borrowDays, 'day').format('YYYY-MM-DD')}
            </p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default BookList;
