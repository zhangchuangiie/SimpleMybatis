package com.example.demo.util;

import com.gbase.jdbc.StatementImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JDBCUtilHikari {
    public static HikariDataSource dataSource = initConnection();



    public static String poolState() {
        String poolName = dataSource.getPoolName();
        HikariPoolMXBean mx = dataSource.getHikariPoolMXBean();
        String format = String.format("%s - stats (total=%d, active=%d, idle=%d, waiting=%d)",poolName,mx.getTotalConnections(), mx.getActiveConnections(), mx.getIdleConnections(), mx.getThreadsAwaitingConnection());
        return format;
    }


    //////Hikari连接池版本//////////////
    public static HikariDataSource initConnection() {
        HikariDataSource ds = null;
        try {
            long start = System.currentTimeMillis();   //获取开始时间
            System.out.println("数据库驱动加载成功！！");
            String url = "jdbc:mysql://192.168.*.*:3306/user?serverTimezone=UTC&useSSL=false&allowMultiQueries=true"; // 定义与连接数据库的url
            String user = "root"; // 定义连接数据库的用户名     上面  不加 ?useSSL=false  会有警告 大概的意思就是说建立ssl连接，但是服务器没有身份认证，这种方式不推荐使用。
            String passWord = ""; // 定义连接数据库的密码
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            //设置url
            hikariConfig.setJdbcUrl(url);
            //数据库帐号
            hikariConfig.setUsername(user);
            //数据库密码
            hikariConfig.setPassword(passWord);
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            //hikariConfig.setIdleTimeout(1000);
            hikariConfig.setAutoCommit(true);
            //hikariConfig.setMaxLifetime(5000);
            hikariConfig.setMinimumIdle(10);
            hikariConfig.setMaximumPoolSize(1000);
            ds = new HikariDataSource(hikariConfig);
            //conn = ds.getConnection();
            System.out.println("已成功的与Gabse 8A数据库建立连接！！");
            long end = System.currentTimeMillis(); //获取结束时间
            System.out.println("程序运行时间(连接)： " + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }


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
                    if("java.time.LocalDateTime".equals(rs.getObject(i).getClass().getName())){
                        tmp=localDateTime2TimeStamp((LocalDateTime) rs.getObject(i));

                    }else{
                        tmp=rs.getObject(i);
                    }
                    //tmp=rs.getObject(i);
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


    public static Connection connection() {
        Connection conn = null;

        try {
            conn = dataSource.getConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;

    }

    public static List<LinkedHashMap<String, Object>> select(String sql,Object ...args){
        List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            result = rs2list(rs);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }



        return result;
    }

    public static LinkedHashMap<String, Object> get(String sql,Object ...args){
        LinkedHashMap<String, Object> result = new LinkedHashMap<String,Object>();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {

            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            result = rs2list(rs).get(0);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return result;
    }

    public static long count(String sql,Object ...args){
        Connection conn = null;
        PreparedStatement stmt = null;
        long count = 0;
        try {

            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                count = rs.getLong(1);
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return count;
    }

    public static int insert(String sql,Object ...args){
        Connection conn = null;
        PreparedStatement stmt = null;
        int rs = 0;
        try {
            long start=System.currentTimeMillis();   //获取开始时间
            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            rs = stmt.executeUpdate();
            conn.close();
            long end=System.currentTimeMillis(); //获取结束时间
            System.out.println("11111程序运行时间： "+(end-start)+"ms");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return rs;
    }

    public static int insertForID(String sql,Map map, Object ...args){
        Connection conn = null;
        PreparedStatement stmt = null;
        int rs = 0;
        try {

            long start=System.currentTimeMillis();   //获取开始时间
            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
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
            conn.close();
            long end=System.currentTimeMillis(); //获取结束时间
            System.out.println("11111程序运行时间： "+(end-start)+"ms");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return rs;
    }

    public static int executeBatch(List<String> sql){
        Connection conn = null;
        Statement stmt = null;
        int[] rs = new int[0];
        int success = 0;
        try {

            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.createStatement();

            for (String s : sql) {
                stmt.addBatch(s);
                //System.out.println("addBatch:"+s);
            }
            rs = stmt.executeBatch();
            for (int r : rs) {
                success+=r;
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return success;
    }


    public static int executeTransaction(List<String> sql){
        Connection conn = null;
        Statement stmt = null;
        int rs = 0;
        try {

            conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            for (String s : sql) {
                stmt.execute(s);
                System.out.println("addExecute:"+s);
            }
            stmt.close();
            conn.commit();

            rs = 1;
            conn.close();
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
                try {
                    stmt.close();
                    System.out.println("关闭stmt连接");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    conn.close();
                    System.out.println("关闭conn连接");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

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
        Connection conn = null;
        PreparedStatement stmt = null;
        int rs = 0;
        try {
            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                stmt.setObject(i+1, args[i]);
            }
            boolean rs1 = stmt.execute();
            rs = rs1 ? 1 : 0;
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return rs;
    }



    public static List<LinkedHashMap<String, Object>> call(String sql,Map map,Object ...args){
        Connection conn = null;
        CallableStatement stmt = null;
        List<LinkedHashMap<String, Object>> result = new ArrayList<LinkedHashMap<String,Object>>();
        try {
            conn = dataSource.getConnection();
            System.out.println("conn = " + conn);
            stmt = conn.prepareCall("call "+sql);
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
        } finally {
            try {
                stmt.close();
                System.out.println("关闭stmt连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                conn.close();
                System.out.println("关闭conn连接");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return result;
    }


    public static void main(String[] args) {

        try {

            String currentDateString = TimeUtil.getCurrentDateString();
            System.out.println("poolState = " + JDBCUtilHikari.poolState());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
