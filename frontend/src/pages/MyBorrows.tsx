import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, message, Card, Statistic, Row, Col, InputNumber, Radio } from 'antd';
import { ReloadOutlined, BookOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { BorrowRecord } from '../types';
import * as borrowApi from '../api/borrow';
import dayjs from 'dayjs';

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

const MyBorrows: React.FC = () => {
  const [borrows, setBorrows] = useState<BorrowRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [renewVisible, setRenewVisible] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState<BorrowRecord | null>(null);
  const [renewDays, setRenewDays] = useState(15);
  const [customRenewDays, setCustomRenewDays] = useState<number | undefined>();
  const [isCustomRenewDays, setIsCustomRenewDays] = useState(false);

  useEffect(() => {
    fetchBorrows();
  }, []);

  const fetchBorrows = async () => {
    setLoading(true);
    try {
      const response = await borrowApi.getMyBorrows();
      // 后端返回的是分页结果，需要取records字段
      if (response.data && response.data.records) {
        setBorrows(response.data.records);
      } else if (Array.isArray(response.data)) {
        setBorrows(response.data);
      } else {
        setBorrows([]);
      }
    } catch (error) {
      // 错误消息已在request拦截器中显示
    } finally {
      setLoading(false);
    }
  };

  const handleReturn = async (record: BorrowRecord) => {
    Modal.confirm({
      title: '确认归还',
      content: `确定要归还《${record.bookTitle}》吗？`,
      onOk: async () => {
        try {
          await borrowApi.returnBook(record.id);
          message.success('归还成功');
          fetchBorrows();
        } catch (error) {
          // 错误已由拦截器处理
        }
      },
    });
  };

  const handleRenew = (record: BorrowRecord) => {
    setSelectedRecord(record);
    setRenewVisible(true);
  };

  const confirmRenew = async () => {
    if (!selectedRecord) return;

    // 验证续借天数
    const days = isCustomRenewDays ? customRenewDays : renewDays;
    if (!days || days <= 0 || !Number.isInteger(days)) {
      message.error('请输入有效的续借天数（必须是正整数）');
      return;
    }

    try {
      await borrowApi.renewBook(selectedRecord.id, { renewDays: days });
      message.success('续借成功');
      setRenewVisible(false);
      setIsCustomRenewDays(false);
      setCustomRenewDays(undefined);
      fetchBorrows();
    } catch (error) {
      // 错误消息已在request拦截器中显示
    }
  };

  const getStatusTag = (status: number, dueTime: string | number[]) => {
    const dueDateObj = parseDate(dueTime);
    const isOverdue = dueDateObj ? dayjs().isAfter(dueDateObj) : false;

    if (status === 1) {
      return <Tag color="success">已归还</Tag>;
    } else if (status === 2 || isOverdue) {
      return <Tag color="error">已逾期</Tag>;
    } else {
      return <Tag color="processing">借阅中</Tag>;
    }
  };

  const columns: ColumnsType<BorrowRecord> = [
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
      title: '续借次数',
      dataIndex: 'renewCount',
      key: 'renewCount',
      width: 100,
    },
    {
      title: '状态',
      key: 'status',
      width: 100,
      render: (_, record) => getStatusTag(record.status, record.dueTime),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => {
        const dueTimeObj = parseDate(record.dueTime);
        const isOverdue = dueTimeObj ? dayjs().isAfter(dueTimeObj) : false;
        const canRenew = record.status === 0 && record.renewCount < 2 && !isOverdue;
        const canReturn = record.status === 0;

        return (
          <Space>
            {canReturn && (
              <Button
                type="primary"
                size="small"
                onClick={() => handleReturn(record)}
              >
                归还
              </Button>
            )}
            {canRenew && (
              <Button size="small" onClick={() => handleRenew(record)}>
                续借
              </Button>
            )}
          </Space>
        );
      },
    },
  ];

  const borrowingCount = borrows.filter((b) => b.status === 0).length;
  const overdueCount = borrows.filter((b) => {
    if (b.status !== 0) return false;
    const dueTimeObj = parseDate(b.dueTime);
    return dueTimeObj ? dayjs().isAfter(dueTimeObj) : false;
  }).length;

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          <Col span={8}>
            <Statistic
              title="借阅中"
              value={borrowingCount}
              prefix={<BookOutlined />}
            />
          </Col>
          <Col span={8}>
            <Statistic
              title="已逾期"
              value={overdueCount}
              valueStyle={{ color: overdueCount > 0 ? '#cf1322' : undefined }}
            />
          </Col>
          <Col span={8}>
            <Statistic title="总借阅次数" value={borrows.length} />
          </Col>
        </Row>
      </Card>

      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Button icon={<ReloadOutlined />} onClick={fetchBorrows}>
            刷新
          </Button>
        </Space>

        <Table
          columns={columns}
          dataSource={borrows}
          rowKey="id"
          loading={loading}
          pagination={{
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>

      {/* 续借确认弹窗 */}
      <Modal
        title="续借图书"
        open={renewVisible}
        onOk={confirmRenew}
        onCancel={() => {
          setRenewVisible(false);
          setIsCustomRenewDays(false);
          setCustomRenewDays(undefined);
        }}
      >
        {selectedRecord && (
          <div>
            <p><strong>书名:</strong> {selectedRecord.bookTitle}</p>
            <p>
              <strong>当前应还日期:</strong>{' '}
              {parseDate(selectedRecord.dueTime)?.format('YYYY-MM-DD') || '-'}
            </p>
            <p>
              <strong>续借天数:</strong>
              <div style={{ marginTop: 8 }}>
                <Radio.Group
                  value={isCustomRenewDays ? 'custom' : renewDays}
                  onChange={(e) => {
                    const val = e.target.value;
                    if (val === 'custom') {
                      setIsCustomRenewDays(true);
                    } else {
                      setIsCustomRenewDays(false);
                      setRenewDays(val);
                    }
                  }}
                >
                  <Space direction="vertical">
                    <Radio value={7}>7天</Radio>
                    <Radio value={15}>15天</Radio>
                    <Radio value={30}>30天</Radio>
                    <Radio value="custom">
                      自定义天数：
                      {isCustomRenewDays && (
                        <InputNumber
                          min={1}
                          max={90}
                          precision={0}
                          value={customRenewDays}
                          onChange={(val) => setCustomRenewDays(val || undefined)}
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
              <strong>续借后应还日期:</strong>{' '}
              {parseDate(selectedRecord.dueTime)
                ?.add(isCustomRenewDays ? (customRenewDays || 0) : renewDays, 'day')
                .format('YYYY-MM-DD') || '-'}
            </p>
            <p><strong>已续借次数:</strong> {selectedRecord.renewCount}</p>
            <p style={{ color: '#999', fontSize: 12 }}>
              注意：每本书最多可续借2次
            </p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MyBorrows;
