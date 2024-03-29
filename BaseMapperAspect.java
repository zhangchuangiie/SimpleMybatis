package com.example.demo.trigger.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println("-aop BaseMapperAspect环绕阶段-" + new Date());


        String METHOD = pjp.getSignature().getName();
        System.out.println("METHOD = " + METHOD);

        Object[] args = pjp.getArgs();
        Object o = args[0];
        if (o.getClass().getName().equals("java.lang.String")){
//            if(METHOD.equals("insertForID") || METHOD.equals("call")){
//                //args = nullFinderBuilderInsertForID(args);
//            }else{
//                args = nullFinderBuilder(args);
//            }
            String sql = (String) o;
            if (METHOD.equals("select") || METHOD.equals("count") || METHOD.equals("update") || METHOD.equals("insert") || METHOD.equals("insertForID")) {
                String _sql = sql.replace("count(*)","*");
//                if (_sql.contains("(") || _sql.contains(")") || _sql.contains(" or ") || _sql.contains(" OR ") ||
//                        _sql.contains(" between ") || _sql.contains(" BETWEEN ") ||
//                        _sql.contains(" is ") || _sql.contains(" IS ") ||
//                        _sql.contains(" in ") || _sql.contains(" IN ")) {
//                    System.out.println("sql = " + sql);
//                    System.out.println("复杂SQL不进行空值过滤");
//                }
//
                if (1==2) {
                    System.out.println("sql = " + sql);
                    System.out.println("复杂SQL不进行空值过滤");
                } else {
                    //args = nullFinderBuilder(args);
                    if (METHOD.equals("insertForID")) {
                        args = nullFinderBuilderInsertForID(args);
                    } else {
                        args = nullFinderBuilder(args);
                    }
                    //o = args[0];
                    sql = (String) args[0];
                    System.out.println("sql = " + sql);
                    sql = nullFilterBuilder(METHOD, sql);
                    System.out.println("sql = " + sql);
                }
            }
            sql = paramReplace(sql);
            args[0]= sql;

        }else{
            List<String> sql = (List<String>) o;
            sql = paramReplace(sql);
            args[0]= sql;

        }

        Object result = pjp.proceed(args);
        System.out.println("result = " + result);
        if(result == null){
            if(METHOD.equals("get")) {
                return new LinkedHashMap<String,Object>();
            }else if(METHOD.equals("count")){
                return Long.valueOf(0);
            }
        }

        System.out.println("result.getClass().getName() = " + result.getClass().getName());

        if (result.getClass().getName().equals("java.util.ArrayList")){

            formatTimeOfListMap((List<LinkedHashMap<String, Object>>) result);
        }else if(result.getClass().getName().equals("java.util.LinkedHashMap")) {
            formatTimeOfObjectMap((LinkedHashMap<String, Object>) result);

        }else{

        }

        return result;

    }


    private int getKeyStringCount(String str, String key) {//方法
        int count = 0;
        int index = 0;
        while((index = str.indexOf(key,index))!=-1){
            index = index + key.length();
            count++;
        }
        return count;
    }


    private String paramReplace(String param) {

        int mun = getKeyStringCount(param,"?");
        //System.out.println("mun = " + mun);

        for (int i = 0; i < mun; i++) {
            param = param.replaceFirst("\\?","#{args["+i+"]}");
            //System.out.println("param = " + param);
        }


        return param;

    }


    private List<String> paramReplace(List<String> paramList) {

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

    private String nullFilterBuilder(String METHOD,String sqlStr) {
        if(METHOD.equals("select") || METHOD.equals("count")) {
            //清洗查询SQL中的空条件

            //sqlStr = sqlStr.replaceAll("\\s+\\)", ")");
            sqlStr = sqlStr + " ";

            sqlStr = sqlStr.replaceAll("\\s*and\\s+\\w[-\\w.+]*\\s*(=|>|<|>=|<=|<>|!=)\\s*null\\s+(?!\\))", " ");
            sqlStr = sqlStr.replaceAll("\\s*and\\s+\\w[-\\w.+]*\\s*(=|>|<|>=|<=|<>|!=)\\s*'null'\\s+(?!\\))", " ");
            sqlStr = sqlStr.replaceAll("\\s*and\\s+\\w[-\\w.+]*\\s*like\\s*'%null%'\\s+(?!\\))", " ");
            sqlStr = sqlStr.replaceAll("\\s+", " ");
            sqlStr = sqlStr.trim();
        }
        if(METHOD.equals("update")) {
            //清洗更新SQL中的空值
            sqlStr = sqlStr.replaceAll("\\w[-\\w.+]*\\s*=\\s*null\\s*,?", "");
            sqlStr = sqlStr.replaceAll("\\w[-\\w.+]*\\s*=\\s*'null'\\s*,?", "");
            sqlStr = sqlStr.replaceAll(",\\s*where", " where");
        }
        if(METHOD.equals("insert") || METHOD.equals("insertForID")) {
            //清洗插入SQL中的空值
           // System.out.println("1111METHOD = " + METHOD);
            //System.out.println("sqlStr = " + sqlStr);
            sqlStr = nullFilterForInsert(sqlStr);
            //System.out.println("sqlStr = " + sqlStr);
        }


        return sqlStr;
    }


    private Object[] nullFinderBuilder(Object[] args) {
        Object o = args[0];
        String sql = (String) o;
        Object[] argsP = (Object[]) args[1];
        int j=0;
        for(int i=0;i<argsP.length;i++){
            if(argsP[i]==null){
                sql = sql.replaceFirst("\\?","null");
                j++;
            }else if("%null%".equals(argsP[i])){
                sql = sql.replaceFirst("like\\s+\\?","=null");
                argsP[i]=null;
                j++;
            }else{
                sql = sql.replaceFirst("\\?","~=~=~=~");
            }
        }
        if(j>0){
            //System.out.println("清洗空值" + j);
            argsP = Arrays.stream(argsP).filter(x -> x != null).toArray();
        }
        //System.out.println("sql = " + sql);
        sql = sql.replaceAll("~=~=~=~","?");
        args[0]= sql;
        args[1]= argsP;
        return args;
    }

    private Object[] nullFinderBuilderInsertForID(Object[] args) {
        Object o = args[0];
        String sql = (String) o;
        Object[] argsP = (Object[]) args[2];
        int j=0;
        for(int i=0;i<argsP.length;i++){
            if(argsP[i]==null){
                sql = sql.replaceFirst("\\?","null");
                j++;
            }else if("%null%".equals(argsP[i])){
                sql = sql.replaceFirst("like\\s*\\?","=null");
                argsP[i]=null;
                j++;
            }else{
                sql = sql.replaceFirst("\\?","~=~=~=~");
            }
        }
        if(j>0){
            //System.out.println("清洗空值" + j);
            argsP = Arrays.stream(argsP).filter(x -> x != null).toArray();
        }
        //System.out.println("sql = " + sql);
        sql = sql.replaceAll("~=~=~=~","?");
        args[0]= sql;
        args[2]= argsP;
        return args;
    }



//////////mybitis数据库连接串serverTimezone=Asia/Shanghai，，，数据库设置set time_zone='+8:00';就没问题
    private String timeStamp2DateString(Timestamp timeStamp) {
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //fm.setTimeZone(TimeZone.getTimeZone("UTC"));
        return fm.format(timeStamp);
    }

    private String timeStamp2DateString(LocalDateTime localDateTime) {

        Timestamp timeStamp = new Timestamp(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return fm.format(timeStamp);
    }

    private int formatTimeOfListMap(List<LinkedHashMap<String, Object>> result) {

        int n = 0;
        for (LinkedHashMap<String, Object> m : result) {
            //System.out.println("m = " + m);
            //if(m==null) continue;

            n+=formatTimeOfObjectMap(m);

        }
        return n;
    }


    private int formatTimeOfObjectMap(LinkedHashMap<String, Object> result) {

        int n = 0;
        if(result==null) return 0;
        for (String k : result.keySet()) {
            //System.out.println(k + " : " + result.get(k));
            //System.out.println(result.get(k).getClass().getName());

            if (result.get(k) != null && "java.sql.Timestamp".equals(result.get(k).getClass().getName())) {
                result.put(k, timeStamp2DateString((Timestamp) result.get(k)));
                n++;
            }
            if (result.get(k) != null && "java.sql.Date".equals(result.get(k).getClass().getName())) {
                result.put(k, result.get(k).toString());
                n++;
            }
            if (result.get(k) != null && "java.sql.Time".equals(result.get(k).getClass().getName())) {
                result.put(k, result.get(k).toString());
                n++;
            }
            if (result.get(k) != null && "java.time.LocalDateTime".equals(result.get(k).getClass().getName())) {
                result.put(k, timeStamp2DateString((LocalDateTime) result.get(k)));
                n++;
            }
        }


        return n;
    }


    public String nullFilterForInsert(String str) {
        String[] list1= str.split("\\)\\,\\s*\\(");
        String[] list2= str.split("\\)\\s+(values|VALUES)");
        if(!(list1.length ==1 && list2.length==2)){return str;}

        String[] strOldList = str.trim().split("[(|)]",-1);
        if(strOldList.length!=5){return str;}

        String[] columnList = strOldList[1].trim().split("\\s*\\,\\s*");
        String[] valuesList = strOldList[3].trim().split("\\s*\\,\\s*");
        if(columnList.length != valuesList.length){return str;}
        String columnsNew = "";
        String valuesNew = "";
        for (int i = 0; i < columnList.length; i++) {
            if(!valuesList[i].equals("null")){
                columnsNew+=columnList[i]+",";
                valuesNew+=valuesList[i]+",";
            }
        }
        columnsNew = columnsNew.substring(0, columnsNew.length() - 1);
        valuesNew = valuesNew.substring(0, valuesNew.length() - 1);
        String reStr = strOldList[0] + "("+columnsNew+") "+strOldList[2]+"("+valuesNew+")";
        return reStr;
    }

}
