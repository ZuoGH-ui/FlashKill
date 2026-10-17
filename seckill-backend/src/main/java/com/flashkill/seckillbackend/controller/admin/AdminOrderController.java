package com.flashkill.seckillbackend.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.entity.SeckillOrder;
import com.flashkill.seckillbackend.mapper.SeckillOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired
    private SeckillOrderMapper orderMapper;

    /** 订单列表 */
    @GetMapping("/list")
    public Result<Page<SeckillOrder>> list(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<SeckillOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(SeckillOrder::getStatus, status);
        }
        wrapper.orderByDesc(SeckillOrder::getCreateTime);
        return Result.success(orderMapper.selectPage(new Page<>(page, size), wrapper));
    }
}