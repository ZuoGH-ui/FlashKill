package com.flashkill.seckillbackend.service;

import com.flashkill.seckillbackend.vo.GoodsDetailVO;
import com.flashkill.seckillbackend.vo.GoodsVO;

import java.util.List;

public interface GoodsService {

    /** 秒杀商品列表 */
    List<GoodsVO> seckillList(String keyword);

    /** 商品详情 */
    GoodsDetailVO detail(Long activityId);
}