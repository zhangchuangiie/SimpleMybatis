package com.example.demo.util;

import org.apache.kafka.common.protocol.types.Field;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;

public class SQLBuilderUtil {


    static List<LinkedHashMap<String, Object>> result =  JDBCUtil.select("desc user");
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

    public static String columns(String table) {

        String sql = "";

        for (LinkedHashMap<String, Object> m : result) {
            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            sql += ""+f+",";
            //System.out.println("@RequestParam(name=\""+f+"\",required = false)"+type+" "+f+",");
        }

        return sql.substring(0, sql.length()-1);

    }

    public static String typeColumns(String table) {

        String sql = "";

        for (LinkedHashMap<String, Object> m : result) {
            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            System.out.println("t = " + t);
            String type = "";
            if(t.contains("bigint")){
                type = "Long";
            }else if(t.contains("smallint")){
                type = "Short";
            }else if(t.contains("int")){
                type = "Integer";
            }else if(t.contains("double")){
                type = "Double";
            }else if(t.contains("float")){
                type = "Float";
            }else if(t.contains("varchar")){
                type = "String";
            }else if(t.contains("text")){
                type = "String";
            }else if(t.contains("json")){
                type = "String";
            }else if(t.contains("datetime")){
                type = "String";
            }else{

            }

            sql += type+" "+f+",";
            //System.out.println("@RequestParam(name=\""+f+"\",required = false)"+type+" "+f+",");
        }

        return sql.substring(0, sql.length()-1);
    }

    public static String deleteSQL(String table) {

        String sql = "delete from "+table+" where id=?";
        return sql;
    }
    public static String insertSQL(String table, String columns) {

        String sql = "INSERT INTO "+table+"("+columns+") VALUES("+columns.replaceAll("\\w[-\\w.+]*","?")+")";
        return sql;
    }

    public static String insertSQL(String table) {

        String columns = columns(table);
        String sql = insertSQL(table, columns);
        return sql;
    }
    public static String updateSQL(String table, String columns) {

        String sql = "update user set "+columns.replaceAll(",","=?,")+"=? where id=?";
        return sql;
    }

    public static String updateSQL(String table) {
        String columns = columns(table);
        String sql = updateSQL(table, columns);
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
