package com.library.borrow.config;

import com.library.borrow.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 *
 * @author Library System
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleConfig {

    private final BorrowService borrowService;

    /**
     * 每天凌晨1点检查逾期记录
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkOverdue() {
        log.info("开始执行逾期检查任务...");
        try {
            int count = borrowService.updateOverdueStatus();
            log.info("逾期检查任务执行完成，更新了 {} 条记录", count);
        } catch (Exception e) {
            log.error("逾期检查任务执行失败", e);
        }
    }
}