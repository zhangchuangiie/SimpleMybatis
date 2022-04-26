package com.example.demo.trigger.aspect;



import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Aspect
public class BaseMapperAspect {
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
            sql = paramReplace(sql);
            args[0]= sql;

        }else{
            List<String> sql = (List<String>) o;
            sql = paramReplace(sql);
            args[0]= sql;

        }

        Object result = pjp.proceed(args);

        if(result == null){return Integer.valueOf(0);}

        System.out.println("result.getClass().getName() = " + result.getClass().getName());

        if (result.getClass().getName().equals("java.util.ArrayList")){

            formatTimeOfListMap((List<LinkedHashMap<String, Object>>) result);
        }else if(result.getClass().getName().equals("java.util.LinkedHashMap")) {
            formatTimeOfObjectMap((LinkedHashMap<String, Object>) result);

        }else{

        }

        return result;

    }


    public static int getKeyStringCount(String str, String key) {//方法
        int count = 0;
        int index = 0;
        while((index = str.indexOf(key,index))!=-1){
            index = index + key.length();
            count++;
        }
        return count;
    }


    public static String paramReplace(String param) {

        int mun = getKeyStringCount(param,"?");
        System.out.println("mun = " + mun);

        for (int i = 0; i < mun; i++) {
            param = param.replaceFirst("\\?","#{args["+i+"]}");
            System.out.println("param = " + param);
        }
        return param;

    }


    public static List<String> paramReplace(List<String> paramList) {

        int total = 0;

        for (int j = 0; j < paramList.size(); j++) {

            String param = paramList.get(j);
            int mun = getKeyStringCount(param,"?");
            //System.out.println("mun = " + mun);

            for (int i = total; i < mun+total; i++) {
                param = param.replaceFirst("\\?","#{args["+i+"]}");
                //System.out.println("param = " + param);
            }
            total = total+mun;
            paramList.set(j,param);
        }

        return paramList;

    }


    public static String timeStamp2DateString(Timestamp timeStamp) {


        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return fm.format(timeStamp);


    }

    public static int formatTimeOfListMap(List<LinkedHashMap<String, Object>> result,String formatKey) {

        int n = 0;
        for (LinkedHashMap<String, Object> m : result) {
            for (String k : m.keySet()) {
                //System.out.println(k + " : " + m.get(k));
                if (formatKey.equals(k)) {
                    m.put(k, timeStamp2DateString((Timestamp) m.get(k)));
                    n++;
                }
            }

        }
        return n;
    }


    public static int formatTimeOfListMap(List<LinkedHashMap<String, Object>> result) {

        int n = 0;
        for (LinkedHashMap<String, Object> m : result) {
            for (String k : m.keySet()) {
                //System.out.println(k + " : " + m.get(k));
                //System.out.println(m.get(k).getClass().getName());

                if (m.get(k) != null && "java.sql.Timestamp".equals(m.get(k).getClass().getName())) {
                    m.put(k, timeStamp2DateString((Timestamp) m.get(k)));
                    n++;
                }
            }

        }
        return n;
    }


    public static int formatTimeOfObjectMap(LinkedHashMap<String, Object> result) {

        int n = 0;

        for (String k : result.keySet()) {
            //System.out.println(k + " : " + result.get(k));
            //System.out.println(result.get(k).getClass().getName());

            if (result.get(k) != null && "java.sql.Timestamp".equals(result.get(k).getClass().getName())) {
                result.put(k, timeStamp2DateString((Timestamp) result.get(k)));
                n++;
            }
        }


        return n;
    }




}
