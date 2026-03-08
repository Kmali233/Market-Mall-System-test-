package com.kemali.handler;

import lombok.Getter;

/*
- 类型 ：自定义异常类（不是处理器）
- 作用 ：在业务逻辑中抛出业务异常
- 用途 ：当业务规则不满足时抛出错误，如"员工不存在"、"部门已删除"等
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
