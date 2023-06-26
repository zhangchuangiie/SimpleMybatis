package com.example.demo.controller;


import com.alibaba.fastjson.JSON;
import com.example.demo.commom.RespValue;
import com.example.demo.mapper.BaseMapper;
import com.example.demo.util.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value="/user/",method = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT})
public class APITemplate {

    @Resource
    private BaseMapper baseMapper;


    @ApiImplicitParams({
            @ApiImplicitParam(name="name",defaultValue="王五"),
            @ApiImplicitParam(name="number",defaultValue="70"),
            @ApiImplicitParam(name="password",defaultValue="sssss")
    })
    @PostMapping(value = "insert")
    public RespValue insert(@RequestParam("name")String name,
                            @RequestParam("number")Integer number,
                            @RequestParam("password")String password){

        String currentDateString = TimeUtil.getCurrentDateString();
        String sql = "INSERT INTO user(name,password,number,time) " +
                " VALUES(?,?,?,?)";
        Map<String, Object> map = new HashMap<String, Object>();
        int result = baseMapper.insertForID(sql,map,name,password,number,currentDateString);
        System.out.println("id = " + map.get("id"));
        return new RespValue(0,"插入成功",map.get("id"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="name",defaultValue="王五"),
            @ApiImplicitParam(name="number",defaultValue="70"),
            @ApiImplicitParam(name="password",defaultValue="sssss")
    })
    @PostMapping(value = "insert2")
    public RespValue insert2(@RequestParam("name")String name,
                            @RequestParam("number")Integer number,
                            @RequestParam("password")String password){

        String currentDateString = TimeUtil.getCurrentDateString();
        String sql = "INSERT INTO user(name,password,number,time) " +
                " VALUES('" + name + "','" + password + "'," + number + ",'" + currentDateString + "')";
        Map<String, Object> map = new HashMap<String, Object>();
        int result = baseMapper.insertForID(sql,map,name,password,number,currentDateString);
        System.out.println("id = " + map.get("id"));
        return new RespValue(0,"插入成功",map.get("id"));
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


    @ApiImplicitParams({
            @ApiImplicitParam(name="name",defaultValue="王五"),
            @ApiImplicitParam(name="number",defaultValue="70"),
            @ApiImplicitParam(name="password",defaultValue="sssss")
    })
    @PostMapping(value = "update")
    public RespValue update(@RequestParam("id") Integer id,
                            @RequestParam(name="name",required = false)String name,
                            @RequestParam(name="number",required = false)Integer number,
                            @RequestParam(name="password",required = false)String password){

        String sqlStr = "update user set ";
        if(name != null){sqlStr += "name='"+name+"',";}
        if(password != null){sqlStr += "password='"+password+"',";}
        if(number != null){sqlStr += "number="+number+",";}

        sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
        sqlStr += " where id="+String.valueOf(id);
        int result = baseMapper.update(sqlStr);
        return new RespValue(0,"修改成功",result);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="name",defaultValue="王五"),
            @ApiImplicitParam(name="number",defaultValue="70"),
            @ApiImplicitParam(name="password",defaultValue="sssss")
    })
    @PostMapping(value = "update2")
    public RespValue update2(@RequestParam("id") Integer id,
                            @RequestParam(name="name",required = false)String name,
                            @RequestParam(name="number",required = false)Integer number,
                            @RequestParam(name="password",required = false)String password){

        String sqlStr = "update user set ";
        List<Object> args = new ArrayList<Object>();
        if(name != null && !"".equals(name)){sqlStr += "name=?,";args.add(name);}
        if(password != null && !"".equals(password)){sqlStr += "password=?,";args.add(password);}
        if(number != null && number!=-1){sqlStr += "number=?,";args.add(number);}
        sqlStr = sqlStr.substring(0, sqlStr.length() - 1);
        sqlStr += " where id=?";args.add(id);
        int result = baseMapper.update(sqlStr,args.toArray());
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
    @PostMapping(value="findListByCondition")
    public RespValue findListByCondition(@RequestParam(name="name",required = false)String name,
                                          @RequestParam(name="number",required = false)Integer number,
                                          @RequestParam(name="password",required = false)String password,
                                          @RequestParam(name="orderColumn",required = false)String orderColumn,
                                          @RequestParam(name="orderDirection",required = false)String orderDirection,
                                          @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                          @RequestParam(name = "pageSize", required = false)Integer pageSize){


        if(pageNum == null) pageNum = 1;
        if(pageSize == null) pageSize = 10;

        String sqlStr = "SELECT * FROM user where 1=1 ";
        if(name != null && !"".equals(name)){sqlStr += "and name='"+name+"' ";}
        if(password != null && !"".equals(password)){sqlStr += "and password='"+password+"' ";}
        if(number != null && number!=-1){sqlStr += "and number="+number+" ";}

        if (orderColumn != null && orderDirection != null) {
            sqlStr += " ORDER BY "+ orderColumn + " " + orderDirection;
        } else {
            sqlStr += " ORDER BY id desc";
        }
        sqlStr += " LIMIT " + (pageNum-1)*pageSize + ","+pageSize;
        List<LinkedHashMap<String, Object>> result =  baseMapper.select(sqlStr);
        System.out.println("result = " + JSON.toJSONString(result,true));
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
    @PostMapping(value="findListByCondition2")
    public RespValue findListByCondition2(@RequestParam(name="name",required = false)String name,
                                         @RequestParam(name="number",required = false)Integer number,
                                         @RequestParam(name="password",required = false)String password,
                                         @RequestParam(name="orderColumn",required = false)String orderColumn,
                                         @RequestParam(name="orderDirection",required = false)String orderDirection,
                                         @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                         @RequestParam(name = "pageSize", required = false)Integer pageSize){


        if(pageNum == null) pageNum = 1;
        if(pageSize == null) pageSize = 10;

        List<Object> args = new ArrayList<Object>();
        String sqlStr = "SELECT * FROM user where 1=1 ";
        if(name != null && !"".equals(name)){sqlStr += "and name=? ";args.add(name);}
        if(password != null && !"".equals(password)){sqlStr += "and password=? ";args.add(password);}
        if(number != null && number!=-1){sqlStr += "and number=? ";args.add(number);}

        if(orderColumn != null && orderDirection != null){
            sqlStr += " ORDER BY ? "+orderDirection;args.add(orderColumn);
        }else{
            sqlStr += " ORDER BY id desc ";
        }
        sqlStr += " LIMIT ?,?";args.add((pageNum-1)*pageSize);args.add(pageSize);
        List<LinkedHashMap<String, Object>> result =  baseMapper.select(sqlStr,args.toArray());
        System.out.println("result = " + JSON.toJSONString(result,true));
        return new RespValue(0,"",result);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="name",defaultValue="王五"),
            @ApiImplicitParam(name="number",defaultValue="70"),
            @ApiImplicitParam(name="password",defaultValue="sssss")
    })
    @GetMapping("/countByCondition")
    public RespValue countByCondition(@RequestParam(name="name",required = false)String name,
                                @RequestParam(name="number",required = false)Integer number,
                                @RequestParam(name="password",required = false)String password){

        String sqlStr = "SELECT count(*) FROM user where 1=1 ";
        if(name != null && !"".equals(name)){sqlStr += "and name='"+name+"' ";}
        if(password != null && !"".equals(password)){sqlStr += "and password='"+password+"' ";}
        if(number != null && number!=-1){sqlStr += "and number="+number+" ";}
        System.out.println("sqlStr = " + sqlStr);
        long result =  baseMapper.count(sqlStr);
        return new RespValue(0,"",result);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="name",defaultValue="王五"),
            @ApiImplicitParam(name="number",defaultValue="70"),
            @ApiImplicitParam(name="password",defaultValue="sssss")
    })
    @GetMapping("/countByCondition2")
    public RespValue countByCondition2(@RequestParam(name="name",required = false)String name,
                                       @RequestParam(name="number",required = false)Integer number,
                                       @RequestParam(name="password",required = false)String password){

        String sqlStr = "SELECT count(*) FROM user where 1=1 ";
        List<Object> args = new ArrayList<Object>();
        if(name != null && !"".equals(name)){sqlStr += "and name=? ";args.add(name);}
        if(password != null && !"".equals(password)){sqlStr += "and password=? ";args.add(password);}
        if(number != null && number!=-1){sqlStr += "and number=? ";args.add(number);}
        System.out.println("sqlStr = " + sqlStr);
        long result =  baseMapper.count(sqlStr,args.toArray());
        return new RespValue(0,"",result);
    }

}

