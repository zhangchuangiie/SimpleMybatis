package com.example.demo.trigger.schedule;


import com.example.demo.mapper.BaseMapper;
import com.example.demo.trigger.filter.ApiAccessFilter;
import com.example.demo.util.TimeUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

            String currentDateString = TimeUtil.getCurrentDateString();

            String name = "王五";
            Integer number = 70;
            String password = "sss";

            Integer id = 3;
            Integer id1 = 10;

            int result = 0 ;

            result = baseMapper.insert("INSERT INTO user(name,password,number,time) " +
                    " VALUES(#{args[0]},#{args[1]},#{args[2]},#{args[3]})",name,password,number,currentDateString);

            result = baseMapper.update("update user set name=#{args[0]},password=#{args[1]},number=#{args[2]} where id=#{args[3]}",name,password,number,id1);

            result = baseMapper.delete("delete from user where id=#{args[0]}",id);

            LinkedHashMap<String, Object> resultObject =  baseMapper.get("SELECT  * FROM user where  id=#{args[0]}",id1);

            List<LinkedHashMap<String, Object>> resultList =  baseMapper.select("SELECT * FROM user where 1=1 and name=#{args[0]} and password=#{args[1]} and number=#{args[2]}  ORDER BY #{args[3]} asc LIMIT 2,2",name,password,number,"time");

            long resultCount =  baseMapper.count("SELECT count(*) FROM user where 1=1 and name=#{args[0]} and password=#{args[1]} and number=#{args[2]}",name,password,number);


//
//            List<String> sql = new ArrayList<String>();
//            for (int i = 0; i < 1000; i++) {
//                sql.add("INSERT INTO user(name,password,number,time) VALUES('王五','sss',70,'" + currentDateString + "')");
//
//            }
//
//
//            long start = System.currentTimeMillis();   //获取开始时间
//
//            int result = baseMapper.executeBatch(sql);
//            System.out.println("result:" + result);
//            long end = System.currentTimeMillis(); //获取结束时间
//            System.out.println("111程序运行时间： " + (end - start) + "ms");


            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
