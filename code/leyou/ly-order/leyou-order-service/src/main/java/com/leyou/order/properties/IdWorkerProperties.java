package com.leyou.order.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: 98050
 * @create: 2018-10-27
 **/
@ConfigurationProperties(prefix = "leyou.worker")
public class IdWorkerProperties {

    /**
     * 当前机器id
     */
    private long workerId;

    /**
     * 序列号
     */
    private long dataCenterId;

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }
}
