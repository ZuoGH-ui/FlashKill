package com.flashkill.seckillbackend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {

    private String orderNo;

    private String goodsName;

    private String goodsImage;

    private BigDecimal amount;

    private Integer status;

    private LocalDateTime createTime;
}