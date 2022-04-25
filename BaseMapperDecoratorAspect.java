package com.example.demo.trigger.aspect;


import com.example.demo.util.FormatTimeUtil;
import com.example.demo.util.ParamUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Aspect
public class BaseMapperDecoratorAspect {
    //切入点表达式，路径一定要写正确了
    @Pointcut("execution( * com.example.demo.mapper.BaseMapper.*(..))")
    public void access() {
    }

    //环绕增强，是在before前就会触发
    @Around("access()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("-aop 环绕阶段-" + new Date());

        System.out.println("METHOD = " + pjp.getSignature().getName());

        Object[] args = pjp.getArgs();
        Object o = args[0];
        if (o.getClass().getName().equals("java.lang.String")){
            String sql = (String) o;
            sql = ParamUtil.paramReplace(sql);
            args[0]= sql;

        }else{
            List<String> sql = (List<String>) o;
            sql = ParamUtil.paramReplace(sql);
            args[0]= sql;

        }

        Object result = pjp.proceed(args);
        System.out.println("result.getClass().getName() = " + result.getClass().getName());

        if (result.getClass().getName().equals("java.util.ArrayList")){

            FormatTimeUtil.formatTimeOfListMap((List<LinkedHashMap<String, Object>>) result);
        }else if(result.getClass().getName().equals("java.util.ArrayList")) {
            FormatTimeUtil.formatTimeOfObjectMap((LinkedHashMap<String, Object>) result);

        }else{

        }

        return result;

    }

}
