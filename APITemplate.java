package com.example.demo.controller;

import com.example.demo.commom.RequestValue;
import com.example.demo.commom.RespValue;
import com.example.demo.mapper.BaseMapper;
import com.example.demo.util.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;

import static com.example.demo.util.AutoValueFromSqlUtil.*;
import static com.example.demo.util.SQLBuilderUtil.*;


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
        String sql = insertSQL("user","name,password,number,time");
        Map<String, Object> map = new HashMap<String, Object>();
        int result = baseMapper.insertForID(sql,map,name,password,number,TimeUtil.getCurrentDateString());
        return new RespValue(0,"插入成功",map.get("id"));
    }


    @PostMapping(value = "insert1")
    public RespValue insert1(HttpServletRequest request) throws JSQLParserException {
        String sql = insertSQL("user","name,password,number,time");

        long start = System.currentTimeMillis();   //获取开始时间
        List<Object> args = insertValue(sql,request);
        long end = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： " + (end - start) + "ms");

        args.add(TimeUtil.getCurrentDateString());
        int result = baseMapper.insert(sql,args.toArray());
        return new RespValue(0,"插入成功",result);
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
        int result = baseMapper.delete(deleteSQL("user"),id);
        return new RespValue(0,"删除成功",result);
    }

    @PostMapping(value="delete1")
    public RespValue delete1(HttpServletRequest request) throws JSQLParserException {
        int result = baseMapper.delete(deleteSQL("user"),idValue(request));
        //int result = baseMapper.delete(deleteSQL("user"),fillValueByName(request,"Integer id").toArray());
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
        String sqlStr = updateSQL("user","name,password,number");
        int result = baseMapper.update(sqlStr,name,password,number,id);
        return new RespValue(0,"修改成功",result);
    }

    @PostMapping(value = "update1")
    public RespValue update1(HttpServletRequest request) throws JSQLParserException {
        String sqlStr = updateSQL("user","name,password,number");
        long start = System.currentTimeMillis();   //获取开始时间
        List<Object> args = updateValueById(sqlStr,request);
        long end = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： " + (end - start) + "ms");
        int result = baseMapper.update(sqlStr,args.toArray());
        return new RespValue(0,"修改成功",result);
    }


    @PostMapping(value = "findObjectById")
    public RespValue findObjectById(@RequestParam("id") Integer id){
        LinkedHashMap<String, Object> result =  baseMapper.get(getSQL("user"),id);
        return new RespValue(0,"",result);
    }

    @PostMapping(value="findListByCondition")
    public RespValue findListByCondition(@RequestParam(name="name",required = false)String name,
                                         @RequestParam(name="number",required = false)Integer number,
                                         @RequestParam(name="password",required = false)String password,
                                         @RequestParam(name="orderColumn",required = false)String orderColumn,
                                         @RequestParam(name="orderDirection",required = false)String orderDirection,
                                         @RequestParam(name = "pageNum", required = false) Integer pageNum,
                                         @RequestParam(name = "pageSize", required = false)Integer pageSize){

        String sqlStr = showSQL("user","name like ? and password like ? and number=?");
        sqlStr = pageAndOrderBuilder(sqlStr,orderColumn,orderDirection,pageNum,pageSize);
        List<LinkedHashMap<String, Object>> result =  baseMapper.select(sqlStr,"%"+name+"%","%"+password+"%",number);    //"%"+name+"%"
        //System.out.println("result = " + JSON.toJSONString(result,true));
        return new RespValue(0,"",result);
    }


    @PostMapping(value = "findListByCondition1")
    public RespValue findListByCondition1(HttpServletRequest request) throws ParseException {
        //String sqlStr = showSQL("user","(name = '王五' or name = ?) and name like ? and password like ? and number =? and name in ('王五','王五') and abs(id)>0 and id between 1 and 100000 and id is not null");
        String sqlStr = showSQL("user","name like ? and password like ? and number=?");
        sqlStr = pageAndOrderBuilder(sqlStr,request);
        //List<Object> args = selectValue(sqlStr,request);
        List<Object> args = fillValueByName(request,"name","password","Integer number");
        //args.set(2,Long.valueOf(args.get(2).toString()));

        //args.add(null);
        //args.set(4,0);
        System.out.println("args = " + args);

        //args.stream().forEach(i -> System.out.println(i.getClass().getSimpleName()));

        List<LinkedHashMap<String, Object>> result =  baseMapper.select(sqlStr,args.toArray());
        return new RespValue(0,"",result);
    }


    @GetMapping("/countByCondition")
    public RespValue countByCondition(@RequestParam(name="name",required = false)String name,
                                @RequestParam(name="number",required = false)Integer number,
                                @RequestParam(name="password",required = false)String password){
        String sqlStr = countSQL("user","name like ? and password like ? and number=?");
        long result =  baseMapper.count(sqlStr,"%"+name+"%","%"+password+"%",number);
        return new RespValue(0,"",result);
    }


    @GetMapping("/countByCondition1")
    public RespValue countByCondition1(HttpServletRequest request) throws ParseException {
        String sqlStr = countSQL("user","name like ? and password like ? and number=?");
        List<Object> args = selectValue(sqlStr,request);
        long result =  baseMapper.count(sqlStr,args.toArray());
        return new RespValue(0,"",result);
    }

}

