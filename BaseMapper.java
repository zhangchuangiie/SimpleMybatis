package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface BaseMapper {

    List<LinkedHashMap<String, Object>> select(String sql,Object ...args);
    long count(String sql,Object ...args);
    LinkedHashMap<String, Object> get(String sql,Object ...args);
    int insert(String sql,Object ...args);
    int insertForID(String sql,Map map,Object ...args);
    int update(String sql,Object ...args);
    int delete(String sql,Object ...args);
    int execute(String sql,Object ...args);
    int executeBatch(List<String> sql,Object ...args);
    List<LinkedHashMap<String, Object>> call(String sql,Map map,Object ...args);
}

