package com.cstest.IndexTest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@org.aspectj.lang.annotation.Aspect
@Component
public class Aspect {

//    @Pointcut("@annotation(com.aop.annotation.ExeTimer)")
//    private void timer(){};
//
//    @Around("timer()")
//    public void AssumeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
//
//        StopWatch stopWatch = new StopWatch();
//
//        stopWatch.start();
//        joinPoint.proceed(); // 조인포인트의 메서드 실행
//        stopWatch.stop();
//
//        long totalTimeMillis = stopWatch.getTotalTimeMillis();
//
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String methodName = signature.getMethod().getName();
//
//        log.info("실행 메서드: {}, 실행시간 = {}ms", methodName, totalTimeMillis);
//    }
}
