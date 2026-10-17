package com.flashkill.seckillbackend.controller;

import com.flashkill.seckillbackend.common.Result;
import com.flashkill.seckillbackend.util.AliyunOSSOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件为空");
        }
        try {
            String url = aliyunOSSOperator.upload(
                    file.getBytes(),
                    file.getOriginalFilename()
            );
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }
}