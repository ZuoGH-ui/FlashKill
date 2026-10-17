package com.flashkill.seckillbackend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsVO {

    private Long activityId;

    private Long goodsId;

    private String name;

    private String image;

    private BigDecimal originalPrice;

    private BigDecimal seckillPrice;

    private Integer stock;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /** 0未开始 1进行中 2已结束 */
    private Integer status;
}