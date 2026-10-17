package com.flashkill.seckillbackend.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.entity.Goods;
import com.flashkill.seckillbackend.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/goods")
public class AdminGoodsController {

    @Autowired
    private GoodsMapper goodsMapper;

    /** 商品列表 */
    @GetMapping("/list")
    public Result<Page<Goods>> list(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(Goods::getName, keyword)
                    .or().like(Goods::getId, keyword)
            );
        }
        wrapper.orderByDesc(Goods::getCreateTime);
        return Result.success(goodsMapper.selectPage(new Page<>(page, size), wrapper));
    }
    /** 新增商品 */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Goods goods) {
        goodsMapper.insert(goods);
        return Result.success();
    }

    /** 编辑商品 */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Goods goods) {
        goodsMapper.updateById(goods);
        return Result.success();
    }

    /** 删除商品 */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        goodsMapper.deleteById(id);
        return Result.success();
    }
}