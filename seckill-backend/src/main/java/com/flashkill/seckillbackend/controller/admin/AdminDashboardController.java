package com.flashkill.seckillbackend.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.entity.SeckillOrder;
import com.flashkill.seckillbackend.mapper.SeckillOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private SeckillOrderMapper orderMapper;

    /** 数据看板 */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        // 总订单数
        Long totalOrder = orderMapper.selectCount(null);

        // 已支付订单
        LambdaQueryWrapper<SeckillOrder> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(SeckillOrder::getStatus, 1);
        Long paidOrder = orderMapper.selectCount(paidWrapper);

        // 总成交额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SeckillOrder o : orderMapper.selectList(paidWrapper)) {
            totalAmount = totalAmount.add(o.getAmount());
        }

        // 成功率
        double successRate = totalOrder == 0 ? 0 : (paidOrder * 1.0 / totalOrder);

        Map<String, Object> data = new HashMap<>();
        data.put("totalOrder", totalOrder);
        data.put("paidOrder", paidOrder);
        data.put("totalAmount", totalAmount);
        data.put("successRate", successRate);
        return Result.success(data);
    }
}