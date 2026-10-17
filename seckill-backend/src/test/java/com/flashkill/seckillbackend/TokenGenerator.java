package com.flashkill.seckillbackend;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.flashkill.seckillbackend.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TokenGenerator {

    @Autowired
    private RedisService redisService;

    @Test
    public void generate() throws Exception {
        FileWriter writer = new FileWriter("C:\\Users\\tokens.csv");
        writer.write("phone,token\n");

        for (int i = 1; i <= 1000; i++) {
            String phone = String.format("138%08d", i);

            // 1. 发验证码
            HttpUtil.post("http://localhost:8080/api/user/sendCode",
                    "{\"phone\":\"" + phone + "\"}");

            // 2. 从 Redis 读验证码
            String code = redisService.get("seckill:code:" + phone);
            if (code == null) {
                System.out.println("验证码为空：" + phone);
                continue;
            }

            // 3. 登录拿 token
            String loginResp = HttpUtil.post(
                    "http://localhost:8080/api/user/login",
                    "{\"phone\":\"" + phone + "\",\"code\":\"" + code + "\"}"
            );

            JSONObject json = JSONUtil.parseObj(loginResp);
            if (json.getInt("code") != 200) {
                System.out.println("登录失败：" + phone + " -> " + loginResp);
                continue;
            }

            String token = json.getJSONObject("data").getStr("token");
            writer.write(phone + "," + token + "\n");

        }

        writer.close();
        System.out.println("生成完成");
    }
}