package com.example.demo.mapper;

import com.example.demo.util.FormatTimeUtil;
import com.example.demo.util.ParamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class BaseDAO{
    //加载顺序：Constructor >> @Autowired >> @PostConstruct
    @Autowired
    private BaseMapper baseMapper;

    private static BaseDAO baseDAO;

    @PostConstruct
    public void init() {
        baseDAO = this;
        baseDAO.baseMapper = this.baseMapper;
    }


    public static List<LinkedHashMap<String, Object>> select(String sql,Object ...args){
        List<LinkedHashMap<String, Object>> result= baseDAO.baseMapper.select(ParamUtil.paramReplace(sql),args);
        FormatTimeUtil.formatTimeOfListMap(result);
        return result;
    }

    public static LinkedHashMap<String, Object> get(String sql,Object ...args){
        LinkedHashMap<String, Object> result = baseDAO.baseMapper.get(ParamUtil.paramReplace(sql),args);
        FormatTimeUtil.formatTimeOfObjectMap(result);
        return result;
    }

    public static long count(String sql,Object ...args){
        long result = baseDAO.baseMapper.count(ParamUtil.paramReplace(sql),args);
        return result;
    }

    public static int insert(String sql,Object ...args){
        int result = baseDAO.baseMapper.insert(ParamUtil.paramReplace(sql),args);
        return result;
    }

    public static int insertForID(Map map, Object ...args){

        String sql = (String) map.get("sql");
        map.put("sql",ParamUtil.paramReplace(sql));
        int result = baseDAO.baseMapper.insertForID(map,args);
        return result;
    }

    public static int executeBatch(List<String> sql,Object ...args){
        int result = baseDAO.baseMapper.executeBatch(ParamUtil.paramReplace(sql),args);
        return result;
    }

    public static int update(String sql,Object ...args){
        return insert(sql,args);
    }

    public static int delete(String sql,Object ...args){
        return insert(sql,args);
    }

    public static int execute(String sql,Object ...args){
        int result = baseDAO.baseMapper.execute(ParamUtil.paramReplace(sql),args);
        return result;
    }





}
