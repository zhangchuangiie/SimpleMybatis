package com.example.demo.controller;

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
    public RespValue insert(String name,Integer number,String password){
        String currentDateString = TimeUtil.getCurrentDateString();
        String sql = "INSERT INTO user(name,password,number,time) VALUES(?,?,?,?)";
        Map<String, Object> map = new HashMap<String, Object>();
        int result = baseMapper.insertForID(sql,map,name,password,number,currentDateString);
        System.out.println("id = " + map.get("id"));
        return new RespValue(0,"插入成功",map.get("id"));
    }

    @PostMapping(value = "insert1")
    public RespValue insert1(String name,Integer number,String password){
        String sql = insertSQL("user","name,password,number,time");
        Map<String, Object> map = new HashMap<String, Object>();
        int result = baseMapper.insertForID(sql,map,name,password,number,TimeUtil.getCurrentDateString());
        return new RespValue(0,"插入成功",map.get("id"));
    }


    @PostMapping(value = "insert2")
    public RespValue insert2(HttpServletRequest request) throws JSQLParserException {
        //String sql = insertSQL("user","name,password,number,time");
        String sql = insertSQL("user");//解析表结构进行自动生成SQL
        //List<Object> args = insertValue(sql,request);//解析SQL语句进行自动填充，缺点是效率略低，不能准确识别类型
        //List<Object> args = fillValueByNameString(request,"name,password,Integer number");//指定参数和类型进行填充，缺点是需要指定参数
        List<Object> args = fillValueByTableForInsert(request,"user");//解析表结构进行自动填充，缺点是需要预加载表结构

        args.set(3,TimeUtil.getCurrentDateString());
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
    public RespValue delete(Integer id){
        int result = baseMapper.delete("delete from user where id=?",id);
        return new RespValue(0,"删除成功",result);
    }

    @PostMapping(value="delete1")
    public RespValue delete1(Integer id){
        int result = baseMapper.delete(deleteSQL("user"),id);
        return new RespValue(0,"删除成功",result);
    }

    @PostMapping(value="delete2")
    public RespValue delete2(HttpServletRequest request) {
        int result = baseMapper.delete(deleteSQL("user"),idValue(request));
        //int result = baseMapper.delete(deleteSQL("user"),fillValueByName(request,"Integer id").toArray());
        return new RespValue(0,"删除成功",result);
    }

    @PostMapping(value="deletes")
    public RespValue deletes(String ids){

        int result = baseMapper.delete("delete from user where id in ("+ids+")");
        return new RespValue(0,"删除成功",result);
    }

    @PostMapping(value = "update")
    public RespValue update(Integer id,String name,Integer number,String password){
        String sqlStr = "update user set name=?,password=?,number=? where id=?";
        int result = baseMapper.update(sqlStr,name,password,number,id);
        return new RespValue(0,"修改成功",result);
    }

    @PostMapping(value = "update1")
    public RespValue update1(Integer id,String name,Integer number,String password){
        String sqlStr = updateSQL("user","name,password,number");
        int result = baseMapper.update(sqlStr,name,password,number,id);
        return new RespValue(0,"修改成功",result);
    }

    @PostMapping(value = "update2")
    public RespValue update2(HttpServletRequest request) throws JSQLParserException {
        String sqlStr = updateSQL("user");//解析表结构进行自动生成SQL
        //List<Object> args = updateValueById(sqlStr,request);//解析SQL语句进行自动填充，缺点是效率略低，不能准确识别类型
        //List<Object> args = fillValueByNameString(request,"name,password,Integer number,Long id");//指定参数和类型进行填充，缺点是需要指定参数
        List<Object> args = fillValueByTableForUpdate(request,"user");//解析表结构进行自动填充，缺点是需要预加载表结构
        int result = baseMapper.update(sqlStr,args.toArray());
        return new RespValue(0,"修改成功",result);
    }

    @PostMapping(value = "findObjectById")
    public RespValue findObjectById(Integer id){
        LinkedHashMap<String, Object> result =  baseMapper.get("SELECT * FROM user where  id=?",id);
        return new RespValue(0,"",result);
    }

    @PostMapping(value = "findObjectById1")
    public RespValue findObjectById1(Integer id){
        LinkedHashMap<String, Object> result =  baseMapper.get(getSQL("user"),id);
        return new RespValue(0,"",result);
    }

    @PostMapping(value = "findObjectById2")
    public RespValue findObjectById2(HttpServletRequest request) {
        LinkedHashMap<String, Object> result =  baseMapper.get(getSQL("user"),idValue(request));
        return new RespValue(0,"",result);
    }

    @PostMapping(value="findListByCondition")
    public RespValue findListByCondition(String name,Integer number,String password,
                                         String orderColumn,String orderDirection,Integer pageNum,Integer pageSize){

        //String sqlStr = "SELECT * FROM user where 1=1 and name like CONCAT('%',IFNULL(?,''),'%') and password=? and number=? ";
        String sqlStr = "SELECT * FROM user where 1=1 and name like ? and password like ? and number=? ";
        sqlStr = SQLBuilderUtil.pageAndOrderBuilder(sqlStr,orderColumn,orderDirection,pageNum,pageSize);
        List<LinkedHashMap<String, Object>> result =  baseMapper.select(sqlStr,"%"+name+"%","%"+password+"%",number);    //"%"+name+"%"
        //System.out.println("result = " + JSON.toJSONString(result,true));
        return new RespValue(0,"",result);
    }

    @PostMapping(value="findListByCondition1")
    public RespValue findListByCondition1(String name,Integer number,String password,
                                         String orderColumn,String orderDirection,Integer pageNum,Integer pageSize){
        //String sqlStr = showSQL("user","(name = '王五' or name = ?) and name like ? and password like ? and number =? and name in ('王五','王五') and abs(id)>0 and id between 1 and 100000 and id is not null");
        String sqlStr = showSQL("user","name like ? and password like ? and number=?");
        sqlStr = pageAndOrderBuilder(sqlStr,orderColumn,orderDirection,pageNum,pageSize);
        List<LinkedHashMap<String, Object>> result =  baseMapper.select(sqlStr,"%"+name+"%","%"+password+"%",number);    //"%"+name+"%"
        //System.out.println("result = " + JSON.toJSONString(result,true));
        return new RespValue(0,"",result);
    }


    @GetMapping("/countByCondition")
    public RespValue countByCondition(String name,Integer number,String password){
        String sqlStr = "SELECT count(*)  FROM user where 1=1 and name like ? and password like ? and number=? ";
        long result =  baseMapper.count(sqlStr,"%"+name+"%","%"+password+"%",number);
        return new RespValue(0,"",result);
    }

    @GetMapping("/countByCondition1")
    public RespValue countByCondition1(String name,Integer number,String password){
        String sqlStr = countSQL("user","name like ? and password like ? and number=?");
        long result =  baseMapper.count(sqlStr,"%"+name+"%","%"+password+"%",number);
        return new RespValue(0,"",result);
    }


}

