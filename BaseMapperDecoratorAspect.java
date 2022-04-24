package com.example.demo.trigger.aspect;


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
import java.util.List;

@Component
@Aspect
public class BaseMapperDecoratorAspect {

    //这个是将自己自定义注解作为切点的根据，路径一定要写正确了
    @Pointcut(value = "@annotation(com.example.demo.trigger.aspect.BaseMapperDecorator)")
    public void access() {
    }

    //环绕增强，是在before前就会触发
    @Around("access()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("-aop 环绕阶段-" + new Date());

        Object[] args = pjp.getArgs();
        Object o = args[0];
        if (o.getClass().getName().equals("java.lang.String")){
            String sql = (String) o;
            sql = ParamUtil.paramReplace(sql);
            args[0]= sql;
            System.out.println("1111111111111111111111111111111111111sql = " + sql);
        }else{
            List<String> sql = (List<String>) o;
            sql = ParamUtil.paramReplace(sql);
            args[0]= sql;

        }

        Object result = pjp.proceed(args);

        return result;

    }

}
