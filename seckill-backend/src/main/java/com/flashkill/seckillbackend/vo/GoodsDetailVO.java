package com.flashkill.seckillbackend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsDetailVO {

    private Long activityId;

    private Long goodsId;

    private String name;

    private String image;

    private String detail;

    private BigDecimal originalPrice;

    private BigDecimal seckillPrice;

    private Integer stock;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    /** 服务器当前时间，前端用来校准倒计时 */
    private LocalDateTime serverTime;
}