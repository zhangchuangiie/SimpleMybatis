package com.example.demo.mapper;

import com.example.demo.trigger.aspect.BaseMapperDecorator;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BaseMapper {
    @BaseMapperDecorator("")
    List<LinkedHashMap<String, Object>> select(String sql,Object ...args);
    @BaseMapperDecorator("")
    long count(String sql,Object ...args);
    @BaseMapperDecorator("")
    LinkedHashMap<String, Object> get(String sql,Object ...args);
    @BaseMapperDecorator("")
    int insert(String sql,Object ...args);
    @BaseMapperDecorator("")
    int insertForID(String sql,Map map,Object ...args);
    @BaseMapperDecorator("")
    int update(String sql,Object ...args);
    @BaseMapperDecorator("")
    int delete(String sql,Object ...args);
    @BaseMapperDecorator("")
    int execute(String sql,Object ...args);
    @BaseMapperDecorator("")
    int executeBatch(List<String> sql,Object ...args);
    @BaseMapperDecorator("")
    List<LinkedHashMap<String, Object>> call(String sql,Map map,Object ...args);
}

