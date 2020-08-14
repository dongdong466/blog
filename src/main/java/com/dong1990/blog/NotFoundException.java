package com.dong1990.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// exception 最终作为资源找不到的状态
// 需要重点注意的是不管该方法是不是发生了异常，将@ResponseStatus注解加在目标方法上，一定会抛出异常。但是如果没有发生异常的话方法会正常执行完毕。
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    // ctrl+o 快速重写父类方法
    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
