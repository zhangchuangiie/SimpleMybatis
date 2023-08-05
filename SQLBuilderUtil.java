package com.example.demo.util;

import org.apache.kafka.common.protocol.types.Field;

import javax.servlet.http.HttpServletRequest;

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

    public static String pageAndOrderBuilder(String sql, HttpServletRequest request) {

        String orderColumn = request.getParameter("orderColumn");
        String orderDirection = request.getParameter("orderDirection");

        Integer pageNum = null;
        if(request.getParameter("pageNum") !=null) pageNum = Integer.valueOf(request.getParameter("pageNum"));
        Integer pageSize = null;
        if(request.getParameter("pageSize") !=null) pageSize = Integer.valueOf(request.getParameter("pageSize"));

        System.out.println("111pageNum = " + pageNum);
        System.out.println("111pageSize = " + pageSize);
        return pageAndOrderBuilder(sql,orderColumn,orderDirection,pageNum,pageSize);
    }
    public static String deleteSQL(String table) {

        String sql = "delete from "+table+" where id=?";
        return sql;
    }
    public static String insertSQL(String table, String columns) {

        String sql = "INSERT INTO "+table+"("+columns+") VALUES("+columns.replaceAll("\\w[-\\w.+]*","?")+")";
        return sql;
    }
    public static String updateSQL(String table, String columns) {

        String sql = "update user set "+columns.replaceAll(",","=?,")+"=? where id=?";
        return sql;
    }
    public static String getSQL(String table) {

        String sql = "SELECT * FROM "+table+" where  id=?";
        return sql;
    }
    public static String countSQL(String table, String where) {

        String sql = "SELECT count(*) FROM "+table+" where 1=1 and "+where;
        return sql;
    }
    public static String showSQL(String table, String where) {

        String sql = "SELECT * FROM "+table+" where 1=1 and "+where;
        return sql;
    }
}
