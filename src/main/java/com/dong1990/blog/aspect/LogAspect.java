package com.dong1990.blog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect // 把当前类标识为一个切面供容器读取
@Component // 开启组件扫描
public class LogAspect {

    // 日志记录器对象
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.dong1990.blog.web.*.*(..))")
    public void log() {}

    // 只要所有的请求，web下的controller都会在方法返回之前被执行
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        logger.info("--------doBefore--------");
        // url和ip可以通过HttpServletRequest来获取，那如何来获取HttpServletRequest呢
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();//StringBuffer类型，toString()转换成String类型
        String ip = request.getRemoteAddr();
        // 方法和参数只能通过切面的对象来获取，在参数上传递一个JoinPoint对象
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
        logger.info("Request : {}", requestLog);
    }

    @After("log()")
    public void doAfter() {
        logger.info("--------doAfter--------");
    }

    // 执行完返回的内容
    @AfterReturning(returning = "result",pointcut = "log()")
    public void doAfterRuturn(Object result) {
        logger.info("Result : {}", result);
    }

    private class RequestLog {
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }

}
