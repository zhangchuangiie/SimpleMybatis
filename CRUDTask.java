package com.example.demo.trigger.schedule;


import com.example.demo.mapper.BaseDAO;
import com.example.demo.mapper.BaseMapper;
import com.example.demo.trigger.filter.ApiAccessFilter;
import com.example.demo.util.JDBCUtil;
import com.example.demo.util.ParamUtil;
import com.example.demo.util.TimeUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class CRUDTask {

    @Resource
    private BaseMapper baseMapper;

    //3.添加定时任务
    @Scheduled(cron = "0/60 * * * * ?")
    //@Scheduled(cron = "0 5 0 * * ?")每天00:05:00执行
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {

        System.err.println("1执行静态定时任务时间: " + TimeUtil.getCurrentDateStringMillisecond());
        System.out.println("----------定时器2：>" +  ApiAccessFilter.getProcessID() +" "+Thread.currentThread().getId() + " " + Thread.currentThread().getName());

        try {
            long start = System.currentTimeMillis();   //获取开始时间
            String currentDateString = TimeUtil.getCurrentDateString();

            String name = "王五";
            Integer number = 70;
            String password = "sss";

            Integer id = 3;
            Integer id1 = 10;

            int result = 0 ;


/*            //直接使用baseMapper
            result = baseMapper.insert("INSERT INTO user(name,password,number,time) " +
                    " VALUES(#{args[0]},#{args[1]},#{args[2]},#{args[3]})",name,password,number,currentDateString);

            String sql = "INSERT INTO user(name,password,number,time) " +
                    " VALUES(#{args[0]},#{args[1]},#{args[2]},#{args[3]})";
            Map<String, Object> map = new HashMap<String, Object>();

            result = baseMapper.insertForID(sql,map, name, password, number, currentDateString);
            System.out.println("id = " + map.get("id"));

            result = baseMapper.update("update user set name=#{args[0]},password=#{args[1]},number=#{args[2]} where id=#{args[3]}",name,password,number,id1);

            result = baseMapper.delete("delete from user where id=#{args[0]}",id);

            LinkedHashMap<String, Object> resultObject =  baseMapper.get("SELECT  * FROM user where  id=#{args[0]}",id1);

            List<LinkedHashMap<String, Object>> resultList =  baseMapper.select("SELECT * FROM user where 1=1 and name=#{args[0]} and password=#{args[1]} and number=#{args[2]}  ORDER BY #{args[3]} asc LIMIT 2,2",name,password,number,"time");

            long resultCount =  baseMapper.count("SELECT count(*) FROM user where 1=1 and name=#{args[0]} and password=#{args[1]} and number=#{args[2]}",name,password,number);

            Map<String, Object> map1 = new HashMap<String, Object>();
            Integer a = 2;
            Integer b = 4;
            List<LinkedHashMap<String, Object>> resultList1 = baseMapper.call("add_num(#{args[0]},#{args[1]},#{map.c,mode=OUT,jdbcType=BIGINT})",map1,a,b);
            System.out.println("map1 = " + map1);
            System.out.println("resultList1 = " + resultList1);

            result = baseMapper.execute("Truncate Table log");
            System.out.println("result = " + result);*/


            //使用baseMapper(LogTrackAspect)
            result = baseMapper.insert("INSERT INTO user(name,password,number,time) " +
                    " VALUES(?,?,?,?)",name,password,number,currentDateString);

            Map<String, Object> map = new HashMap<String, Object>();
            result = baseMapper.insertForID("INSERT INTO user(name,password,number,time) " +
                    " VALUES(?,?,?,?)",map, name,password,number,currentDateString);
            System.out.println("id = " + map.get("id"));

            result = baseMapper.update("update user set name=?,password=?,number=? where id=?",name,password,number,id1);

            result = baseMapper.delete("delete from user where id=?",id);

            LinkedHashMap<String, Object> resultObject =  baseMapper.get("SELECT  * FROM user where  id=?",id1);

            List<LinkedHashMap<String, Object>> resultList =  baseMapper.select("SELECT * FROM user where 1=1 and name=? and password=? and number=?  ORDER BY ? asc LIMIT 2,2",name,password,number,"time");

            long resultCount =  baseMapper.count("SELECT count(*) FROM user where 1=1 and name=? and password=? and number=?",name,password,number);

            Map<String, Object> map1 = new HashMap<String, Object>();
            Integer a = 2;
            Integer b = 4;
            List<LinkedHashMap<String, Object>> resultList1 = baseMapper.call("add_num(?,?,#{map.c,mode=OUT,jdbcType=BIGINT})",map1,a,b);
            System.out.println("map1 = " + map1);
            System.out.println("resultList1 = " + resultList1);


            List<String> sql = new ArrayList<String>();
            for (int i = 0; i < 10; i++) {
                sql.add("INSERT INTO user(name,password,number,time) VALUES('王五','sss',70,'" + currentDateString + "')");

            }
//            String sql = "INSERT INTO user(name,password,number,time) VALUES('王五','sss',70,'" + currentDateString + "')";
//            for (int i = 0; i < 20000; i++) {
//                sql += ",('王五','sss',70,'" + currentDateString + "')";
//
//            }
            System.out.println("size:" + sql.size());
            int re = baseMapper.executeBatch(sql);
            System.out.println("re = " + re);



            result=  baseMapper.execute("Truncate Table log");
            System.out.println("result = " + result);



            long end = System.currentTimeMillis(); //获取结束时间
            System.out.println("111程序运行时间： " + (end - start) + "ms");


            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
