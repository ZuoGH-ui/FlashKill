package com.flashkill.seckillbackend.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统错误"),

    STOCK_EMPTY(1001, "库存不足"),
    REPEAT_ORDER(1002, "请勿重复下单"),
    ACTIVITY_NOT_START(1003, "活动未开始"),
    ACTIVITY_END(1004, "活动已结束"),
    CODE_ERROR(1005, "验证码错误"),
    TOO_FREQUENT(1006, "请求过于频繁");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}