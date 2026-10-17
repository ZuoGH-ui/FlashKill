package com.flashkill.seckillbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashkill.seckillbackend.common.BusinessException;
import com.flashkill.seckillbackend.common.ResultCode;
import com.flashkill.seckillbackend.entity.Goods;
import com.flashkill.seckillbackend.entity.SeckillActivity;
import com.flashkill.seckillbackend.mapper.GoodsMapper;
import com.flashkill.seckillbackend.mapper.SeckillActivityMapper;
import com.flashkill.seckillbackend.service.GoodsService;
import com.flashkill.seckillbackend.util.ActivityStatusUtil;
import com.flashkill.seckillbackend.vo.GoodsDetailVO;
import com.flashkill.seckillbackend.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVO> seckillList(String keyword) {
        // 查所有活动（不限制 status 和 end_time）
        LambdaQueryWrapper<SeckillActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SeckillActivity::getCreateTime);

        List<SeckillActivity> activities = activityMapper.selectList(wrapper);

        List<GoodsVO> result = new ArrayList<>();
        for (SeckillActivity a : activities) {
            Goods goods = goodsMapper.selectById(a.getGoodsId());
            if (goods == null) continue;

            // 关键字过滤
            if (keyword != null && !keyword.isEmpty()
                    && !goods.getName().contains(keyword)) {
                continue;
            }

            GoodsVO vo = new GoodsVO();
            vo.setActivityId(a.getId());
            vo.setGoodsId(a.getGoodsId());
            vo.setName(goods.getName());
            vo.setImage(goods.getImage());
            vo.setOriginalPrice(goods.getPrice());
            vo.setSeckillPrice(a.getSeckillPrice());
            vo.setStock(a.getStock());
            vo.setStartTime(a.getStartTime());
            vo.setEndTime(a.getEndTime());
            vo.setStatus(ActivityStatusUtil.calc(a));   // ← 实时算状态
            result.add(vo);
        }
        return result;
    }

    /** 计算活动状态 */
    private Integer calcStatus(SeckillActivity a) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(a.getStartTime())) return 0;
        if (now.isAfter(a.getEndTime())) return 2;
        return 1;
    }

    @Override
    public GoodsDetailVO detail(Long activityId) {
        SeckillActivity a = activityMapper.selectById(activityId);
        if (a == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        Goods goods = goodsMapper.selectById(a.getGoodsId());
        if (goods == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        GoodsDetailVO vo = new GoodsDetailVO();
        vo.setActivityId(a.getId());
        vo.setGoodsId(a.getGoodsId());
        vo.setName(goods.getName());
        vo.setImage(goods.getImage());
        vo.setDetail(goods.getDetail());
        vo.setOriginalPrice(goods.getPrice());
        vo.setSeckillPrice(a.getSeckillPrice());
        vo.setStock(a.getStock());
        vo.setStartTime(a.getStartTime());
        vo.setEndTime(a.getEndTime());
        vo.setStatus(calcStatus(a));
        vo.setServerTime(LocalDateTime.now());
        return vo;
    }
}