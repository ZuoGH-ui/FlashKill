package com.flashkill.seckillbackend.controller;

import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.service.SeckillService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /** 执行秒杀 */
    @PostMapping("/execute/{activityId}")
    public Result<Map<String, String>> execute(@PathVariable Long activityId,
                                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String orderNo = seckillService.execute(userId, activityId);

        Map<String, String> data = new HashMap<>();
        data.put("orderNo", orderNo);
        return Result.success(orderNo == null ? "排队中" : "抢购成功", data);
    }

    /** 查询秒杀结果 */
    @GetMapping("/result/{activityId}")
    public Result<String> result(@PathVariable Long activityId,
                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(seckillService.queryResult(userId, activityId));
    }
}