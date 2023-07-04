package com.example.demo.controller;


import com.alibaba.fastjson.JSON;
import com.example.demo.commom.RequestValue;
import com.example.demo.commom.RespValue;
import com.example.demo.entity.User;
import com.example.demo.mapper.BaseMapper;
import com.example.demo.trigger.aspect.EmptyAlert;
import com.example.demo.trigger.aspect.EmptyString;
import com.example.demo.trigger.aspect.LogTrack;
import com.example.demo.util.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value="/user/",method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
public class APITemplate {

    @Resource
    private BaseMapper baseMapper;

    @PostMapping(value = "insert")
    public RespValue insert(@RequestParam("name")String name,
                            @RequestParam("number")Integer number,
                            @RequestParam("password")String password){

        String currentDateString = TimeUtil.getCurrentDateString();
        String sql = "INSERT INTO user(name,password,number,time) VALUES(?,?,?,?)";
        Map<String, Object> map = new HashMap<String, Object>();
        int result = baseMapper.insertForID(sql,map,name,password,number,currentDateString);
        System.out.println("id = " + map.get("id"));
        return new RespValue(0,"插入成功",map.get("id"));
    }

    @PostMapping(value = "insert2")
    public RespValue insert2(HttpServletRequest request){
        RequestValue r = new RequestValue(request);
        String currentDateString = TimeUtil.getCurrentDateString();
        String sql = "INSERT INTO user(name,password,number,time) VALUES(?,?,?,?)";
        Map<String, Object> map = new HashMap<String, Object>();
        int result = baseMapper.insertForID(sql,map,r.s("name"),r.s("password"),r.i("number"),currentDateString);
        System.out.println("id = " + map.get("id"));
        return new RespValue(0,"插入成功",map.get("id"));
    }


    @PostMapping("/statistics")
    public RespValue statistics() {
        long start = System.currentTimeMillis();   //获取开始时间
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        //总数
        result.put("total_num", baseMapper.count("SELECT COUNT(*) as num FROM user"));
        //每个分数的计数，按分数倒排
        result.put("number_num", baseMapper.select("SELECT number,COUNT(*) as num FROM user GROUP BY number ORDER BY number DESC"));
        //每个用户的最高分，按分数倒排
        result.put("user_max_number", baseMapper.select("SELECT name,MAX(number) as max_number FROM user GROUP BY name ORDER BY max_number DESC"));
        long end = System.currentTimeMillis(); //获取结束时间
        System.out.println("111程序运行时间： " + (end - start) + "ms");
        return new RespValue(0, result);
    }


    @PostMapping(value="delete")
    public RespValue delete(@RequestParam("id") Integer id){

        int result = baseMapper.delete("delete from user where id=?",id);
        return new RespValue(0,"删除成功",result);
    }

    @PostMapping(value="deletes")
    public RespValue deletes(@RequestParam(name="ids")String ids){

        int result = baseMapper.delete("delete from user where id in ("+ids+")");
        return new RespValue(0,"删除成功",result);
    }

    @PostMapping(value = "update")
    public RespValue update(@RequestParam("id") Integer id,
                            @RequestParam(name="name",required = false)String name,
                            @RequestParam(name="number",required = false)Integer number,
                            @RequestParam(name="password",required = false)String password){

        String sqlStr = "update user set name=?,password=?,number=? where id=?";
        int result = baseMapper.update(sqlStr,name,password,number,id);
        return new RespValue(0,"修改成功",result);
    }

    @PostMapping(value = "findObjectById")
    public RespValue findObjectById(@RequestParam("id") Integer id){

        LinkedHashMap<String, Object> result =  baseMapper.get("SELECT * FROM user where  id=?",id);
        return new RespValue(0,"",result);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="name",defaultValue="王五"),
            @ApiImplicitParam(name="number",defaultValue="70"),
            @ApiImplicitParam(name="password",defaultValue="sssss"),
            @ApiImplicitParam(name="orderColumn",defaultValue="time"),
            @ApiImplicitParam(name="orderDirection",defaultValue="asc"),
            @ApiImplicitParam(name="pageNum",defaultValue="1"),
            @ApiImplicitParam(name="pageSize",defaultValue="10")
    })
    //@EmptyString("")
    //@EmptyAlert("")
    @PostMapping(value="findListByCondition")
    public RespValue findListByCondition(@RequestParam(name="name",required = false)String name,
                                         @RequestParam(name="number",required = false)Integer number,
                                         @RequestParam(name="password",required = false)String password,
                                         @RequestParam(name="orderColumn",required = false)String orderColumn,
                                         @RequestParam(name="orderDirection",required = false)String orderDirection,
                                         @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                         @RequestParam(name = "pageSize", required = false)Integer pageSize){

        //String sqlStr = "SELECT * FROM user where 1=1 and name like CONCAT('%',IFNULL(?,''),'%') and password=? and number=? ";
        String sqlStr = "SELECT * FROM user where 1=1 and name like ? and password like ? and number=? ";
        sqlStr = SQLBuilderUtil.pageAndOrderBuilder(sqlStr,orderColumn,orderDirection,pageNum,pageSize);
        //sqlStr = SQLBuilderUtil.nullFilterBuilder(sqlStr);
        List<LinkedHashMap<String, Object>> result =  baseMapper.select(sqlStr,"%"+name+"%","%"+password+"%",number);    //"%"+name+"%"
        //System.out.println("result = " + JSON.toJSONString(result,true));
        return new RespValue(0,"",result);
    }


    @GetMapping("/countByCondition")
    public RespValue countByCondition(@RequestParam(name="name",required = false)String name,
                                @RequestParam(name="number",required = false)Integer number,
                                @RequestParam(name="password",required = false)String password){

        String sqlStr = "SELECT count(*)  FROM user where 1=1 and name like ? and password like ? and number=? ";
        long result =  baseMapper.count(sqlStr,"%"+name+"%","%"+password+"%",number);
        return new RespValue(0,"",result);
    }



}

