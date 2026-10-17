package com.flashkill.seckillbackend;

import com.flashkill.seckillbackend.entity.User;
import com.flashkill.seckillbackend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        User user = userMapper.selectById(1L);
        System.out.println(user);
    }
}