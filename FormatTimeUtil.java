package com.example.demo.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;


public class FormatTimeUtil {



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
