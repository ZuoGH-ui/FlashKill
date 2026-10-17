package com.flashkill.seckillbackend.controller;

import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.service.OrderService;
import com.flashkill.seckillbackend.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 我的订单列表 */
    @GetMapping("/list")
    public Result<List<OrderVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.list(userId));
    }

    /** 订单详情 */
    @GetMapping("/detail/{orderNo}")
    public Result<OrderVO> detail(@PathVariable String orderNo,
                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.detail(userId, orderNo));
    }

    /** 取消订单 */
    @PostMapping("/cancel/{orderNo}")
    public Result<Void> cancel(@PathVariable String orderNo,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancel(userId, orderNo);
        return Result.success();
    }

    /** 支付订单（模拟） */
    @PostMapping("/pay/{orderNo}")
    public Result<Void> pay(@PathVariable String orderNo,
                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.pay(userId, orderNo);
        return Result.success();
    }
}