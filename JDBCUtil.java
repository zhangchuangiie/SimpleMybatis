package com.example.demo.util;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import org.javatuples.Triplet;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class JDBCUtil {
    public static Connection conn = getConnection();

    public static Connection getConnection() {
        try {
            long start=System.currentTimeMillis();   //获取开始时间
            //Class.forName("com.mysql.jdbc.Driver"); // 加载MySQL数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver"); // 加载MySQL数据库驱动

            System.out.println("数据库驱动加载成功！！");
            String url = "jdbc:mysql://192.168.126.3:3306/user?serverTimezone=UTC&useSSL=false&allowMultiQueries=true"; // 定义与连接数据库的url
            String user = "root"; // 定义连接数据库的用户名     上面  不加 ?useSSL=false  会有警告 大概的意思就是说建立ssl连接，但是服务器没有身份认证，这种方式不推荐使用。
            String passWord = "SQL@cent110"; // 定义连接数据库的密码
            conn = DriverManager.getConnection(url, user, passWord); // 连接连接
            System.out.println("已成功的与MySQL数据库建立连接！！");
            long end=System.currentTimeMillis(); //获取结束时间
            System.out.println("程序运行时间(连接)： "+(end-start)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


//    public static Connection getConnection() {
//        try {
//            long start = System.currentTimeMillis();   //获取开始时间
//            //Class.forName("com.mysql.jdbc.Driver"); // 加载MySQL数据库驱动
////            Class.forName("com.mysql.cj.jdbc.Driver"); // 加载MySQL数据库驱动
////
//            System.out.println("数据库驱动加载成功！！");
//            String url = "jdbc:mysql://192.168.126.3:3306/user?serverTimezone=UTC&useSSL=false&allowMultiQueries=true"; // 定义与连接数据库的url
//            String user = "root"; // 定义连接数据库的用户名     上面  不加 ?useSSL=false  会有警告 大概的意思就是说建立ssl连接，但是服务器没有身份认证，这种方式不推荐使用。
//            String passWord = "SQL@cent110"; // 定义连接数据库的密码
//            HikariConfig hikariConfig = new HikariConfig();
//            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
//            //设置url
//            hikariConfig.setJdbcUrl(url);
//            //数据库帐号
//            hikariConfig.setUsername(user);
//            //数据库密码
//            hikariConfig.setPassword(passWord);
//            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//            HikariDataSource ds = new HikariDataSource(hikariConfig);
//            conn = ds.getConnection();
//            System.out.println("已成功的与MySQL数据库建立连接！！");
//            long end = System.currentTimeMillis(); //获取结束时间
//            System.out.println("程序运行时间(连接)： " + (end - start) + "ms");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return conn;
//    }

//    public static Connection getConnection() {
//        try {
//            long start = System.currentTimeMillis();   //获取开始时间
//            //Class.forName("com.mysql.jdbc.Driver"); // 加载MySQL数据库驱动
////            Class.forName("com.mysql.cj.jdbc.Driver"); // 加载MySQL数据库驱动
////
//            System.out.println("数据库驱动加载成功！！");
//            String url = "jdbc:mysql://192.168.126.3:3306/user?serverTimezone=UTC&useSSL=false&allowMultiQueries=true"; // 定义与连接数据库的url
//            String user = "root"; // 定义连接数据库的用户名     上面  不加 ?useSSL=false  会有警告 大概的意思就是说建立ssl连接，但是服务器没有身份认证，这种方式不推荐使用。
//            String passWord = "SQL@cent110"; // 定义连接数据库的密码
////            conn = DriverManager.getConnection(url, user, passWord); // 连接连接
//
//            BasicDataSource source = new BasicDataSource();
//
//            source.setDriverClassName("com.mysql.cj.jdbc.Driver");
//            source.setUrl(url);
//            source.setUsername(user);
//            source.setPassword(passWord);
//
//            //
//            //source.setInitialSize(10);
//
//
//            conn = source.getConnection();
//
//            System.out.println("已成功的与MySQL数据库建立连接！！");
//            long end = System.currentTimeMillis(); //获取结束时间
//            System.out.println("程序运行时间(连接)： " + (end - start) + "ms");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return conn;
//    }
    public static Timestamp localDateTime2TimeStamp(LocalDateTime localDateTime) {

        Timestamp timeStamp = new Timestamp(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        return timeStamp;

    }


    public static List<LinkedHashMap<String, Object>> rs2list(ResultSet rs) {
        List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
        try {
            ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            while (rs.next()) {
                LinkedHashMap<String, Object> rowData = new LinkedHashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    if(rs.getObject(i) == null) continue;
                    //System.out.println(md.getColumnName(i)+"*:"+md.getColumnTypeName(i)+":"+md.getColumnClassName(i));
                    //System.out.println(rs.getObject(i) + "*=" + rs.getObject(i).getClass().getName());
                    Object tmp = null;
                    if("java.sql.Timestamp".equals(md.getColumnClassName(i))){
                        tmp=localDateTime2TimeStamp((LocalDateTime) rs.getObject(i));
                    }else{
                        tmp=rs.getObject(i);
                    }
                    if(rowData.containsKey(md.getColumnName(i))){
                        if(rowData.containsKey(md.getColumnName(i)+"_rename")){
                            rowData.put(md.getColumnName(i)+"_rename_rename", tmp);
                        }else{
                            rowData.put(md.getColumnName(i)+"_rename", tmp);
                        }
                    }else{
                        rowData.put(md.getColumnName(i), tmp);
                    }

                }
                result.add(rowData);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static PreparedStatement prepareStatement(String sql){
        PreparedStatement pstmt = null;
        try {
            if(!conn.isValid(5)){
                getConnection();
            }
            pstmt = conn.prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pstmt;

    }

    public static List<LinkedHashMap<String, Object>> select(String sql,Object ...args){
        List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
        try {
            if(!conn.isValid(5)){
                getConnection();
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            result = rs2list(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static LinkedHashMap<String, Object> get(String sql,Object ...args){
        LinkedHashMap<String, Object> result = new LinkedHashMap<String,Object>();
        try {
            if(!conn.isValid(5)){
                getConnection();
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            result = rs2list(rs).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static long count(String sql,Object ...args){
        long count = 0;
        try {
            if(!conn.isValid(5)){

                getConnection();
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                count = rs.getLong(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static int insert(String sql,Object ...args){
        int rs = 0;
        try {
            if(!conn.isValid(5)){
                System.out.println("重建连接");
                getConnection();
            }
            long start=System.currentTimeMillis();   //获取开始时间
            PreparedStatement stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            rs = stmt.executeUpdate();
            long end=System.currentTimeMillis(); //获取结束时间
            System.out.println("11111程序运行时间： "+(end-start)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static int insertForID(String sql,Map map, Object ...args){
        int rs = 0;
        try {
            if(!conn.isValid(5)){
                System.out.println("重建连接");
                getConnection();
            }
            long start=System.currentTimeMillis();   //获取开始时间
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            rs = stmt.executeUpdate();

            ResultSet results = stmt.getGeneratedKeys();
            long id = -1;
            if(results.next())
            {
                id = results.getLong(1);
            }

            map.put("id",id);

            long end=System.currentTimeMillis(); //获取结束时间
            System.out.println("11111程序运行时间： "+(end-start)+"ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static int executeBatch(List<String> sql){
        int[] rs = new int[0];
        int success = 0;
        try {
            if(!conn.isValid(5)){
                getConnection();
            }
            Statement stmt = conn.createStatement();

            for (String s : sql) {
                stmt.addBatch(s);
                //System.out.println("addBatch:"+s);
            }
            rs = stmt.executeBatch();
            for (int r : rs) {
                success+=r;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }


    public static int executeTransaction(List<String> sql){
        int rs = 0;
        try {
            if(!conn.isValid(5)){
                getConnection();
            }
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            for (String s : sql) {
                stmt.execute(s);
                System.out.println("addExecute:"+s);
            }
            stmt.close();
            conn.commit();

            rs = 1;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                //遇到异常，则回滚事务
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            rs = 0;
        }finally {
            try {

                conn.setAutoCommit(true);
                //conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return rs;

    }

    public static int update(String sql,Object ...args){
        return insert(sql,args);
    }

    public static int delete(String sql,Object ...args){
        return insert(sql,args);
    }

    public static int execute(String sql,Object ...args){
        int rs = 0;
        try {
            if(!conn.isValid(5)){
                getConnection();
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            boolean rs1 = stmt.execute();
            rs = rs1 ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }



    public static List<LinkedHashMap<String, Object>> call(String sql,Map map,Object ...args){
        List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
        try {
            if(!conn.isValid(5)){
                getConnection();
            }

            CallableStatement stmt = conn.prepareCall("call "+sql);
            List<Triplet> tripletList = (List<Triplet>) map.get("tripletList");
            List<Integer> outIndex = new ArrayList<Integer>();
            if(tripletList == null || tripletList.size()==0){

                System.out.println("没有OUT参数");
            }else{
                for (Triplet triplet : tripletList) {
                    stmt.registerOutParameter((int)triplet.getValue(1), (int)triplet.getValue(2));
                    outIndex.add((int)triplet.getValue(1));
                }

            }
            for(int i=0;i<args.length;i++){
                if(outIndex.contains(i)){
                    continue;
                }
                stmt.setObject(i+1, args[i]);
            }

            boolean j = stmt.execute();
            ResultSet rs = stmt.getResultSet();
            if(rs !=null){
                result = rs2list(rs);
            }

            if(tripletList == null || tripletList.size()==0){

                System.out.println("没有OUT参数");
            }else{
                for (Triplet triplet : tripletList) {
                    map.put(triplet.getValue(0).toString(),stmt.getObject((int)triplet.getValue(1)));
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    public static void main(String[] args) {

        try {

            String currentDateString = TimeUtil.getCurrentDateString();
            long start = System.currentTimeMillis();   //获取开始时间
//////////////////////////////////可变参数PreparedStatement///////////////////////////////////////
        //List<LinkedHashMap<String, Object>> result = JDBCUtil.select("SELECT * FROM user where 1=1 and name = ? LIMIT 10", "武风华");
//            List<LinkedHashMap<String, Object>> result = JDBCUtil.select("SELECT * FROM user where 1=1 and name = '武风华' LIMIT 10");
//            //System.out.println("result = " + result);
//            JSONArray array = JSONUtil.parseArray(result);
//            System.out.println("array.toString() = " + array.toJSONString(4));


//            LinkedHashMap<String, Object> result = JDBCUtil.get("SELECT * FROM user where 1=1 and name = ? LIMIT 10", "武风华");
//            //System.out.println("result = " + result);
//            JSONObject array = JSONUtil.parseObj(result);
//            System.out.println("array.toString() = " + array.toJSONString(4));


//            long result = JDBCUtil.count("SELECT count(*) FROM user where 1=1 and name = ?","武风华");
//            System.out.println("result = " + result);

//            int result = JDBCUtil.delete("delete from user where id=?",3);
//            System.out.println("result = " + result);

//            int result=  JDBCUtil.update("update user set name=?  where id = ?","王五改",6);
//            System.out.println("result = " + result);

//            int result= JDBCUtil.insert("INSERT INTO user(name,password,number,time) VALUES(?,'sss',70,'"+currentDateString+"')","王五新");
//            System.out.println("result = " + result);

//            Map<String, Object> map = new HashMap<String, Object>();
//            int result = JDBCUtil.insertForID("INSERT INTO user(name,password,number,time) " +
//                    " VALUES(?,?,?,?)",map, "王五","sss",70, currentDateString);
//            System.out.println("id = " + map.get("id"));


//            int result=  JDBCUtil.execute("Truncate Table log");
//            System.out.println("result = " + result);


/*            List<String> sql = new ArrayList<String>();
            for (int i = 0; i < 1000; i++) {
                sql.add("INSERT INTO user(name,password,number,time) VALUES('王五','sss',70,'" + currentDateString + "')");

            }

//            String sql = "INSERT INTO user(name,password,number,time) VALUES('王五','sss',70,'" + currentDateString + "')";
//            for (int i = 0; i < 20000; i++) {
//                sql += ",('王五','sss',70,'" + currentDateString + "')";
//
//            }
            System.out.println("size:" + sql.size());
            int re = JDBCUtil.executeBatch(sql);
            System.out.println("re = " + re);*/




/*            List<Triplet> tripletList = new ArrayList<Triplet>();
            tripletList.add(Triplet.with("c", 3, Types.BIGINT));
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("tripletList",tripletList);
            Integer a = 2;
            Integer b = 4;
            List<LinkedHashMap<String, Object>> resultList1 = JDBCUtil.call("add_num(?,?,?)",map1,a,b);
            System.out.println("map1 = " + map1);
            System.out.println("resultList1 = " + resultList1);*/

//////////////////////////////////可变参数PreparedStatement///////////////////////////////////////




            //System.out.println("result:"+result);
            long end = System.currentTimeMillis(); //获取结束时间
            System.out.println("程序运行时间： " + (end - start) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("result:"+Arrays.toString(re));

    }
}
