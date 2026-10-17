package com.flashkill.seckillbackend.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flashkill.seckillbackend.common.BusinessException;
import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.common.ResultCode;
import com.flashkill.seckillbackend.entity.SeckillActivity;
import com.flashkill.seckillbackend.mapper.SeckillActivityMapper;
import com.flashkill.seckillbackend.redis.RedisKey;
import com.flashkill.seckillbackend.redis.RedisService;
import com.flashkill.seckillbackend.util.ActivityStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/activity")
public class AdminActivityController {

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private RedisService redisService;

    /** 活动列表 */
    @GetMapping("/list")
    public Result<Page<SeckillActivity>> list(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(required = false) Integer status) {
        List<SeckillActivity> all = activityMapper.selectList(
                new LambdaQueryWrapper<SeckillActivity>()
                        .orderByDesc(SeckillActivity::getCreateTime)
        );

        for (SeckillActivity a : all) {
            a.setStatus(ActivityStatusUtil.calc(a));
        }

        if (status != null) {
            all = all.stream()
                    .filter(a -> a.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        int from = (page - 1) * size;
        int to = Math.min(from + size, all.size());
        List<SeckillActivity> records = from >= all.size()
                ? Collections.emptyList()
                : all.subList(from, to);

        Page<SeckillActivity> result = new Page<>(page, size, all.size());
        result.setRecords(records);
        return Result.success(result);
    }

    /** 新增活动 */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody SeckillActivity activity) {
        if (activity.getStartTime() == null || activity.getEndTime() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "时间不能为空");
        }
        if (activity.getStartTime().isAfter(activity.getEndTime())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "开始时间不能晚于结束时间");
        }

        activityMapper.insert(activity);
        redisService.set(RedisKey.seckillStock(activity.getId()),
                String.valueOf(activity.getStock()));
        return Result.success();
    }

    /** 编辑活动 */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody SeckillActivity activity) {
        // 1. 更新数据库
        activityMapper.updateById(activity);

        // 2. 查最新数据
        SeckillActivity latest = activityMapper.selectById(activity.getId());
        if (latest == null) {
            return Result.success();
        }

        // 3. 算状态
        Integer status = ActivityStatusUtil.calc(latest);
        String key = RedisKey.seckillStock(latest.getId());

        // 4. 按状态处理 Redis
        if (status == 2) {
            // 已结束 → 删库存
            redisService.delete(key);
            redisService.delete(RedisKey.seckillUsers(latest.getId()));
        } else {
            // 未开始 / 进行中 → 设库存
            redisService.set(key, String.valueOf(latest.getStock()));
        }

        return Result.success();
    }

    /** 结束活动 */
    @PostMapping("/end/{id}")
    public Result<Void> end(@PathVariable Long id) {
        SeckillActivity activity = new SeckillActivity();
        activity.setId(id);
        activity.setStatus(2);
        activity.setEndTime(LocalDateTime.now());
        activityMapper.updateById(activity);

        redisService.delete(RedisKey.seckillStock(id));
        redisService.delete(RedisKey.seckillUsers(id));

        return Result.success();
    }
}