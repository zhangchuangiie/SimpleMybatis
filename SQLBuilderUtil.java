package com.example.demo.util;

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


}
