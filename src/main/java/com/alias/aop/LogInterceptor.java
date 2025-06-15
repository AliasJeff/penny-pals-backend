package com.alias.aop;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求响应日志 AOP
 *
 * 
 * 
 **/
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    /**
     * 执行拦截
     */
    @Around("execution(* com.alias.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 获取请求信息
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 请求唯一 ID
        String requestId = UUID.randomUUID().toString();

        // 请求方法
        String method = request.getMethod();

        // 构造完整请求 URL（含协议、IP、端口、URI、查询参数）
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        String fullUrl = queryString == null ? requestURL.toString() : requestURL.append("?").append(queryString).toString();

        // 获取方法参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";

        // 打印请求日志
        log.info("【Request Start】ID: {}, Method: {}, URL: {}, IP: {}, Params: {}",
                requestId, method, fullUrl, request.getRemoteHost(), reqParam);

        // 执行原方法
        Object result = point.proceed();

        // 停止计时
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();

        // 打印响应日志
        log.info("【Request End】ID: {}, Cost: {}ms", requestId, totalTimeMillis);

        return result;
    }
}

