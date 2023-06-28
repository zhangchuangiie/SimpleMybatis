package com.example.demo.util;


public class SQLBuilderUtil {

    public static String pageAndOrderBuilder(String sql,String orderColumn,String orderDirection,
                                 Integer pageNum,Integer pageSize) {

        if(pageNum == null) pageNum = 1;
        if(pageSize == null) pageSize = 10;
        String sqlOrderStr = "";
        if (orderColumn != null && orderDirection != null) {
            sqlOrderStr = orderColumn + " " + orderDirection;
        } else {
            sqlOrderStr = "id desc";
        }
        if(sqlOrderStr.equals("id desc") || sqlOrderStr.equals("id asc")){
        }else{
            sqlOrderStr += ", id desc";
        }
        sql += " ORDER BY " + sqlOrderStr + " LIMIT " + (pageNum-1)*pageSize + ","+pageSize;
        return sql;
    }

    public static String nullFilterBuilder(String sql) {

        //清洗查询SQL中的空条件
        sql=sql.replaceAll("\\s+and\\s+\\w[-\\w.+]*\\s*=\\s*null","");
        sql=sql.replaceAll("\\s+and\\s+\\w[-\\w.+]*\\s*=\\s*'null'","");
        //清洗更新SQL中的空值
        sql=sql.replaceAll("\\w[-\\w.+]*\\s*=\\s*null\\s*,?","");
        sql=sql.replaceAll("\\w[-\\w.+]*\\s*=\\s*'null'\\s*,?","");
        sql=sql.replaceAll(",\\s*where"," where");
        return sql;
    }


}
