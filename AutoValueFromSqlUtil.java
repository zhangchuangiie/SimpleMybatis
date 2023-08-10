package com.example.demo.util;

import com.example.demo.client.TestParseSQL;
import com.example.demo.commom.RequestValue;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoValueFromSqlUtil {

    private static Pattern pattern = Pattern.compile("^\\w[-\\w.+]*$");
    public static boolean isColumn(String input){

        //String regex = "^\\w[-\\w.+]*$"; // 替换成你的正则表达式
        //String input = "abs(id)"; // 替换成你的待匹配字符串
        //String input = "name"; // 替换成你的待匹配字符串
        //Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        boolean isMatched = matcher.matches();
        return isMatched;
    }



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
        columns.forEach(column -> args.add(r.getObject(column.getColumnName())));
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
//        //columns.forEach(column -> args.add(r.getObject(column.getColumnName())));
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
            updateSet.getColumns().forEach(column -> args.add(r.getObject(column.getColumnName())));
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
            updateSet.getColumns().forEach(column -> args.add(r.getObject(column.getColumnName())));
        }
        args.add(r.getLong("id"));
        return args;
    }

    public static long idValue(HttpServletRequest request) throws JSQLParserException {
        RequestValue r = new RequestValue(request);
        return r.getLong("id");
    }

    public static List<Object> selectValue(String sql, HttpServletRequest request) throws ParseException {
        RequestValue r = new RequestValue(request);
        List<String> columnList = new ArrayList<String>();
        List<Object> args = new ArrayList<Object>();
        args.add(r);
        args.add(columnList);
        /** 创建SQL语句解析器实例 */
        CCJSqlParser parser = CCJSqlParserUtil.newParser(sql);
        /** 解析SQL语句 */
        Statement stmt = parser.Statement();

        /** 使用 LogVisiter对象遍历AST的所有节点 */
        parser.getASTRoot().jjtAccept(new LogVisitor(), args);
        System.out.printf("sql--> %s\n",stmt);
        args.remove(0);
        args.remove(0);
        System.out.printf("Handle columns--> %s\n",columnList.toString());
        System.out.printf("Fill args--> %s\n",args.toString());
        return args;
    }

    public static List<Object> fillValueByName(HttpServletRequest request,String ...names) {
        RequestValue r = new RequestValue(request);
        List<Object> args = new ArrayList<Object>();
        //List<Object> argsType = new ArrayList<Object>();

        for(int i=0;i<names.length;i++){
            String[] list = names[i].split("\\s+");
            String name = "";
            String type = "";
            if(list.length == 2){
                name = list[1];
                type = list[0];
            }else{
                name = list[0];
                type = "String";
            }

            if(type.equals("Integer")){
                args.add(r.getInteger(name));
            } else if (type.equals("Long")) {
                args.add(r.getLong(name));
            }else if (type.equals("Short")) {
                args.add(r.getShort(name));
            }else if (type.equals("Float")) {
                args.add(r.getFloat(name));
            }else if (type.equals("Double")) {
                args.add(r.getDouble(name));
            }else if (type.equals("String")) {
                args.add(r.getString(name));
            }else{
                args.add(r.getString(name));
            }
        }
        System.out.printf("Handle columns--> %s\n", Arrays.toString(names));
        System.out.printf("Fill args--> %s\n",args.toString());
        //System.out.printf("Args type--> %s\n",argsType.toString());
        return args;
    }


    /**
     * 遍历所有节点的{@link CCJSqlParserVisitor} 接口实现类
     * @author
     *
     */
    static class LogVisitor extends CCJSqlParserDefaultVisitor {

        @Override
        public Object visit(SimpleNode node, Object data) {

            List<Object> args = (ArrayList<Object>) data;
            RequestValue r = (RequestValue) args.get(0);
            //args.remove(0);
            List<String> columnList = (List<String>) args.get(1);

            String value0=null;
            String value1=null;
            String value2=null;

            String[] strs=null;

            Object value = node.jjtGetValue();
            /** 根据节点类型找出表名和字段名节点，对名字加上双引号 */
            //1. COLUMN
            if (node.getId() == CCJSqlParserTreeConstants.JJTCOLUMN) {
                //System.out.printf("1. column: %s \n",value.toString());
                Column column = (Column)value;
                // column.setColumnName("\"" + column.getColumnName() + "\"");
                Table table = column.getTable();
                if(null != table){
                    //    table.setName("\"" + table.getName() + "\"");
                }
            }
            //2. TABLE
            else if (node.getId() == CCJSqlParserTreeConstants.JJTTABLE) {
                //System.out.printf("2. table: %s \n",value.toString());
                Table table = (Table)value;
                if(null != table){
                    //   table.setName("\"" + table.getName() + "\"");
                }
            }
            //3. REGULAR CONDITION-14
            else if(node.getId() == CCJSqlParserTreeConstants.JJTREGULARCONDITION) {

                System.out.printf("3. CONDITION: %s \n",value.toString());
                strs= value.toString().split(" ");
                if (strs.length>=3) {
                    value0 = strs[0].trim();
                    value1 = strs[1].trim();
                    value2 = strs[2].trim();

                    if(isColumn(value0) && value2.equals("?"))
                    {
                        //args.add(value0.toString());
                        columnList.add(value0);
                        System.out.println("Handle column = " + value0 + ", fill value = " + r.getObject(value0));
                        args.add(r.getObject(value0));
                    }
                    System.out.printf("3.1. CONDITION-COLUMN:%s\n",value0 );
                    System.out.printf("3.2. CONDITION-OPERATOR:%s\n",value1 );
                    System.out.printf("3.3. CONDITION-VALUE:%s\n",value2 );
//                if (node.getId() == CCJSqlParserTreeConstants.JJTCOLUMN) {
//                    System.out.printf("4.1. column: %s \n", value2.toString());
//                }

                }

            }
            //4. LIKE EXPRESSION -16
            else if(node.getId() == CCJSqlParserTreeConstants.JJTLIKEEXPRESSION)
            {
                System.out.printf("4. LIEK CONDITION: %s \n",value.toString());
                strs= value.toString().split(" ");
                if (strs.length>=3) {
                    value0 = strs[0].trim();
                    value1 = strs[1].trim();
                    value2 = strs[2].trim();
                    if(isColumn(value0) && value2.equals("?"))
                    {
                        //args.add(value0.toString());
                        columnList.add(value0);
                        System.out.println("Handle column = " + value0 + ", fill value = " + "%"+r.getString(value0)+"%");
                        args.add("%"+r.getString(value0)+"%");
                    }
                    System.out.printf("4.1.LIKE CONDITION-COLUMN:%s\n",value0 );
                    System.out.printf("4.2.LIKE CONDITION-OPERATOR:%s\n",value1 );
                    System.out.printf("4.3.LIKE CONDITION-VALUE:%s\n",value2 );

                }

            }
            //5. IN EXPRESSION-15
            else if(node.getId() == CCJSqlParserTreeConstants.JJTINEXPRESSION) {
                System.out.printf("5. IN CONDITION: %s    \n",value.toString()  );
                String express=value.toString();
                // String[] strs= value.toString().split(" ");
                if (express.contains("not") || express.contains("NOT"))
                {
                    //System.out.println("NOT IN");
                    strs= value.toString().replace("NOT IN","").split(" ",2);
//                    for(String str:strs) {
//                        System.out.printf("6.1. CONDITION1:%s\n", str);
//                    }
                    if(strs.length>=2) {
                        value0 = strs[0].trim();
                        value1 = "NOT IN";
                        value2 = strs[1].trim();
                        System.out.printf("5.1.IN CONDITION-COLUMN:%s\n",value0 );
                        System.out.printf("5.2.IN CONDITION-OPERATOR:%s\n",value1 );
                        System.out.printf("5.3.IN CONDITION-VALUE:%s\n",value2 );
                    }
                }
                else
                {
                    //System.out.println("IN");
                    strs= value.toString().replace("IN","").split(" ",2);
//                    for(String str:strs) {
//                        System.out.printf("6.2. CONDITION1:%s\n", str);
//                    }
                    if(strs.length>=2) {
                        value0 = strs[0].trim();
                        value1 = "IN";
                        value2 = strs[1].trim();
                        System.out.printf("5.1.IN CONDITION-COLUMN:%s\n",value0 );
                        System.out.printf("5.2.IN CONDITION-OPERATOR:%s\n",value1 );
                        System.out.printf("5.3.IN CONDITION-VALUE:%s\n",value2 );
                    }
                }
//                if(value2.toString().equals("?"))
//                {
//                    args.add(value0.toString());
//                }

            }
            //6. Function - 23
            else if(node.getId() == CCJSqlParserTreeConstants.JJTFUNCTION)
            {
                System.out.printf("6. FUNCITON CONDITION: %s \n",value.toString());
                strs= value.toString().split("\\(");
                if (strs.length>=2) {
                    value0 = strs[0].trim();
                    value1 = "("+strs[1].trim();
                    System.out.printf("6.1.LIKE CONDITION-FUNCITON:%s\n",value0 );
                    System.out.printf("6.2.LIKE CONDITION-VALUE:%s\n",value1 );
                }
//                if(value2.toString().equals("?"))
//                {
//                    args.add(value0.toString());
//                }
            }
            else if(null != value){
                /** 其他类型节点输出节点类型值，Java类型和节点值 */
                //System.out.printf("0. :%d :%s :%s \n",node.getId(),value.getClass().getSimpleName(),value.toString());
            }
            return super.visit(node, data);
        }
    }

    ///复杂条件不填充值，支持函数后手工按位置填充,,,也可以直接都用手工传值
//    public static List<Object> selectValue(String sql, HttpServletRequest request) throws JSQLParserException {
//        RequestValue r = new RequestValue(request);
//        List<Object> args = new ArrayList<Object>();
//        Select select = (Select)CCJSqlParserUtil.parse(sql);
//        //获取select对象
//        SelectBody selectBody = select.getSelectBody();
//        System.out.println(selectBody.toString());
//        PlainSelect plainSelect=(PlainSelect) selectBody;
//        //表名
//        Table table= (Table) plainSelect.getFromItem();
//        //表名称
//        System.out.println(table.getName());
//        AndExpression andExpression = (AndExpression) plainSelect.getWhere();
//        System.out.println("andExpression = " + andExpression);
//
//        //andExpression.accept();
//
//        String[] split = andExpression.toString().split(" AND ");
//        for (String s : split) {
//            System.out.println("s : " + s);
////            System.out.println("s.trim().split(\"\\\\s*\").length = " + s.trim().split("\\s+").length);
////            for (String s1 : s.trim().split("\\s+")) {
////                System.out.println("s1 = " + s1);
////            }
//            if(s.trim().split("\\s+").length>3)  continue;
//            if (s.contains("?")){
//                if (s.contains(" = ")){
//                    String column=s.trim().split(" = ")[0].trim();
//                    System.out.println("column = " + column);
//                    if(column.equals("1")) continue;
//                    args.add(r.getObject(column));
//                } else if (s.contains(" LIKE ")) {
//                    String column=s.trim().split(" LIKE ")[0].trim();
//                    System.out.println("column = " + column);
//                    args.add("%"+r.s(column)+"%");
//                }else{
//                    System.out.println("复杂表达式，跳过填充 = " + s);
//                    continue;
//                }
//            }else{
//                System.out.println("无占位符，跳过填充 = " + s);
//                continue;
//            }
//
//        }
//        return args;
//    }

    public static void main(String[] args) {


    }


}


