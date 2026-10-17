package com.flashkill.seckillbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flashkill.seckillbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}