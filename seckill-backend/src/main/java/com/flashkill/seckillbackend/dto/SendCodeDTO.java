package com.flashkill.seckillbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCodeDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;
}