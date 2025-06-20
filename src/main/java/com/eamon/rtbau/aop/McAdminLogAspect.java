package com.eamon.rtbau.aop;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class McAdminLogAspect {

    //定义切点 @Pointcut
    //在自定义注解的位置切入代码
//    @Pointcut("@annotation(com.pikpak.pikpakmove.aop.MyLog)")
    // com.pikpak.pikpakmove.controller 包下所有方法都是切入点
    @Pointcut("execution(* com.eamon.rtbau..*.*(..))")
    public void logPoinCut() {
    }

    //切面 配置通知
    @Around(value = "logPoinCut()")
    public Object saveSysLog(ProceedingJoinPoint joinPoint) throws Throwable {
        /*result为连接点的放回结果*/
        Object result = null;
        // 在joinPoint.proceed()前才可以执行
//        System.out.println("[joinPoint.getSignature()前] 切面进入");

        Signature signature = joinPoint.getSignature();
        /*执行目标方法*/
        try {
            result = joinPoint.proceed();
            /*返回通知方法*/
            // 过滤掉 getConfigs 方法的返回结果日志
            if (!"getConfigs".equals(signature.getName())) {
                log.info("返回通知方法>目标方法名:{},返回结果为:{}", signature.getName(), JSON.toJSONString(result));
            }
        } catch (Throwable e) {
            /*异常通知方法*/
            log.info("异常通知方法>目标方法名:{},异常为:{}", signature.getName(), e);
        }
        return result;
    }
}
