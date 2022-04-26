package com.example.demo.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BaseMapper {

    @Select("${sql}")
    List<LinkedHashMap<String, Object>> select(String sql,Object ...args);

    @Select("${sql}")
    long count(String sql,Object ...args);

    @Select("${sql}")
    LinkedHashMap<String, Object> get(String sql,Object ...args);

    @Insert("${sql}")
    int insert(String sql,Object ...args);

    @Options(useGeneratedKeys = true, keyProperty = "map.id")
    @Insert("${sql}")
    int insertForID(String sql,Map map,Object ...args);

    @Update("${sql}")
    int update(String sql,Object ...args);

    @Delete("${sql}")
    int delete(String sql,Object ...args);

    @Update("${sql}")
    int execute(String sql,Object ...args);

    @Update({
    "<script>"
    +"<foreach item='item' index='index' collection='sql'>"
    +"${item};"
    +"</foreach>"
    +"</script>"
    })
    int executeBatch(List<String> sql,Object ...args);

    @Options(statementType = StatementType.CALLABLE, useCache = false)
    @Select("CALL ${sql}")
    List<LinkedHashMap<String, Object>> call(String sql,Map map,Object ...args);
}

