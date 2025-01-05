package com.vrsoftware.checkout.transaction_manager.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.vrsoftware.checkout.transaction_manager.controller.*.*(..)) || " +
            "execution(* com.vrsoftware.checkout.transaction_manager.service.*.*(..)) || " +
            "execution(* com.vrsoftware.checkout.transaction_manager.repository.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // Obtém informações sobre o metodo sendo executado
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        logger.info("Executing {}.{} with arguments {}", className, methodName, args);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            logger.error("Exception in {}.{}: {}, Args: {}", className, methodName, e.getMessage(), args, e);
            throw e;
        }

        long executionTime = System.currentTimeMillis() - start;
        logger.info("Executed {}.{} in {} ms", className, methodName, executionTime);

        return result;
    }
}
