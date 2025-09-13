package com.yuan.yuanaicodeproducer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

/**
 * @author Yuan
 * @version 1.0
 * @date 2025-09-12
 * @className ResourceMonitor
 * @description 资源监控类，监控JVM内存和线程使用情况
 */
@Slf4j
@Component
public class ResourceMonitor {
    
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    
    /**
     * 每30秒检查一次资源使用情况
     */
    @Scheduled(fixedRate = 30000)
    public void monitorResources() {
        try {
            // 检查内存使用情况
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            long usedMemory = heapUsage.getUsed();
            long maxMemory = heapUsage.getMax();
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            // 检查线程使用情况
            int threadCount = threadBean.getThreadCount();
            int peakThreadCount = threadBean.getPeakThreadCount();
            
            // 记录资源使用情况
            log.info("资源监控 - 内存使用率: {:.2f}% ({}/{} MB), 线程数: {}/{}", 
                    memoryUsagePercent,
                    usedMemory / 1024 / 1024,
                    maxMemory / 1024 / 1024,
                    threadCount,
                    peakThreadCount);
            
            // 内存使用率过高告警
            if (memoryUsagePercent > 80) {
                log.warn("⚠️ 内存使用率过高: {:.2f}%", memoryUsagePercent);
            }
            
            // 线程数过多告警
            if (threadCount > 100) {
                log.warn("⚠️ 线程数过多: {}", threadCount);
            }
            
            // 内存使用率超过90%时建议GC
            if (memoryUsagePercent > 90) {
                log.error("🚨 内存使用率严重过高: {:.2f}%，建议检查内存泄漏", memoryUsagePercent);
                // 可以在这里触发GC，但通常不推荐
                // System.gc();
            }
            
        } catch (Exception e) {
            log.error("资源监控异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取当前内存使用率
     */
    public double getMemoryUsagePercent() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long usedMemory = heapUsage.getUsed();
        long maxMemory = heapUsage.getMax();
        return (double) usedMemory / maxMemory * 100;
    }
    
    /**
     * 获取当前线程数
     */
    public int getThreadCount() {
        return threadBean.getThreadCount();
    }
}
