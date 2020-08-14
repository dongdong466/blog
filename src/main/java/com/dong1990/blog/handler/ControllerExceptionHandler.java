package com.dong1990.blog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

    // 获取日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // ModelAndView 返回页面并附带一些后台输出到前端的信息
    // request 获取访问的url，e还要处理异常
    // ExceptionHandler 标识该方法可以做异常处理的，只要是exception级别的都可以
    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHander(HttpServletRequest request, Exception e) throws Exception {
        logger.error("Requst URL : {}，Exception : {}", request.getRequestURL(),e);

        // 逻辑判断，返回的异常信息标识状态码不要处理，交给spring boot本身去处理异常，它会根据状态码去返回对应的页面
        // 通过注解工具去查找注解，异常类和状态码类
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;// 如果不存在，就抛出exception
        }

        ModelAndView mv = new ModelAndView();
        mv.addObject("url",request.getRequestURL());
        mv.addObject("exception", e);
        mv.setViewName("error/error");
        return mv;
    }
}
