package com.flashkill.seckillbackend.controller;

import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.service.GoodsService;
import com.flashkill.seckillbackend.vo.GoodsDetailVO;
import com.flashkill.seckillbackend.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /** 秒杀商品列表 */
    @GetMapping("/seckillList")
    public Result<List<GoodsVO>> seckillList(@RequestParam(required = false) String keyword) {
        return Result.success(goodsService.seckillList(keyword));
    }

    /** 商品详情 */
    @GetMapping("/detail/{activityId}")
    public Result<GoodsDetailVO> detail(@PathVariable Long activityId) {
        return Result.success(goodsService.detail(activityId));
    }
}