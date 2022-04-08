package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;

public class ParamUtil {


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
            System.out.println("mun = " + mun);

            for (int i = total; i < mun+total; i++) {
                param = param.replaceFirst("\\?","#{args["+i+"]}");
                System.out.println("param = " + param);
            }
            total = total+mun;
            paramList.set(j,param);
        }

        return paramList;

    }

    public static void main(String[] args) {
        List<String> sql = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            sql.add("INSERT INTO user(name,password,?) VALUES('王五','sss',70)");

        }

        List<String> sql1 = ParamUtil.paramReplace(sql);
        System.out.println("sql1 = " + sql1);

    }



}
