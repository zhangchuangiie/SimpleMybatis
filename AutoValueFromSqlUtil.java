package com.example.demo.util;

import com.example.demo.commom.RequestValue;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AutoValueFromSqlUtil {

    public static List<Object> insertValue(String sql, HttpServletRequest request) throws JSQLParserException {
        RequestValue r = new RequestValue(request);
        Insert insert = (Insert) CCJSqlParserUtil.parse(sql);
        System.out.println("插入的表" + insert.getTable());
        System.out.println("插入的列" + insert.getColumns());
        System.out.println("插入的值" + insert.getItemsList());

        List<Column> columns = insert.getColumns();
        List<Object> args = new ArrayList<Object>();
//        for (int i = 0; i < columns.size(); i++) {
//            args.add(r.s(columns.get(i).getColumnName()));
//        }
        columns.forEach(column -> args.add(r.o(column.getColumnName())));
        return args;
    }

//    public static List<Object> insertValue(String sql, Object obj) throws JSQLParserException, InvocationTargetException, IllegalAccessException {
//        Insert insert = (Insert) CCJSqlParserUtil.parse(sql);
//        System.out.println("插入的表" + insert.getTable());
//        System.out.println("插入的列" + insert.getColumns());
//        System.out.println("插入的值" + insert.getItemsList());
//
//        List<Column> columns = insert.getColumns();
//        List<Object> args = new ArrayList<Object>();
////        for (int i = 0; i < columns.size(); i++) {
////            args.add(r.s(columns.get(i).getColumnName()));
////        }
//        //columns.forEach(column -> args.add(r.o(column.getColumnName())));
//
//
//        for (int i = 0; i < columns.size(); i++) {
//            Method m = obj.class.getMethod("get"+ StringUtils.capitalize(columns.get(i).getColumnName()));
//            args.add(m.invoke(obj,null));
//
//        }
//
//
//        return args;
//    }

    public static List<Object> updateValue(String sql, HttpServletRequest request) throws JSQLParserException {
        RequestValue r = new RequestValue(request);
        List<Object> args = new ArrayList<Object>();
        Update update = (Update) CCJSqlParserUtil.parse(sql);
        System.out.println("更惨的表" + update.getTable());
        for (UpdateSet updateSet : update.getUpdateSets()) {
            updateSet.getColumns().forEach(System.out::println);
            updateSet.getColumns().forEach(column -> args.add(r.o(column.getColumnName())));
        }
        return args;
    }

    public static List<Object> updateValueById(String sql, HttpServletRequest request) throws JSQLParserException {
        RequestValue r = new RequestValue(request);
        List<Object> args = new ArrayList<Object>();
        Update update = (Update) CCJSqlParserUtil.parse(sql);
        System.out.println("更惨的表" + update.getTable());
        for (UpdateSet updateSet : update.getUpdateSets()) {
            updateSet.getColumns().forEach(System.out::println);
            updateSet.getColumns().forEach(column -> args.add(r.o(column.getColumnName())));
        }
        args.add(r.i("id"));
        return args;
    }

    public static int idValue(HttpServletRequest request) throws JSQLParserException {
        RequestValue r = new RequestValue(request);

        return r.i("id");
    }

    ///复杂条件不填充值，支持函数后手工按位置填充,,,也可以直接都用手工传值
    public static List<Object> selectValue(String sql, HttpServletRequest request) throws JSQLParserException {
        RequestValue r = new RequestValue(request);
        List<Object> args = new ArrayList<Object>();
        Select select = (Select)CCJSqlParserUtil.parse(sql);
        //获取select对象
        SelectBody selectBody = select.getSelectBody();
        System.out.println(selectBody.toString());
        PlainSelect plainSelect=(PlainSelect) selectBody;
        //表名
        Table table= (Table) plainSelect.getFromItem();
        //表名称
        System.out.println(table.getName());
        AndExpression andExpression = (AndExpression) plainSelect.getWhere();
        System.out.println("andExpression = " + andExpression);

        String[] split = andExpression.toString().split(" AND ");
        for (String s : split) {
            System.out.println("s : " + s);
//            System.out.println("s.trim().split(\"\\\\s*\").length = " + s.trim().split("\\s+").length);
//            for (String s1 : s.trim().split("\\s+")) {
//                System.out.println("s1 = " + s1);
//            }
            if(s.trim().split("\\s+").length>3)  continue;
            if (s.contains("?")){
                if (s.contains(" = ")){
                    String column=s.trim().split(" = ")[0].trim();
                    System.out.println("column = " + column);
                    if(column.equals("1")) continue;
                    args.add(r.o(column));
                } else if (s.contains(" LIKE ")) {
                    String column=s.trim().split(" LIKE ")[0].trim();
                    System.out.println("column = " + column);
                    args.add("%"+r.s(column)+"%");
                }else{
                    System.out.println("复杂表达式，跳过填充 = " + s);
                    continue;
                }
            }else{
                System.out.println("无占位符，跳过填充 = " + s);
                continue;
            }

        }
        return args;
    }

    public static void main(String[] args) {


    }


}


