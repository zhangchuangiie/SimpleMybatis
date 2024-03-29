# SimpleMybatis（Mybatis通用Mapper）
一个基于Mybatis封装的类JdbcTemplate风格的ORM工具，数据库开发效率神器

现有的ORM框架使用起来都依赖配套的实体类和配置文件，对多表关联支持很不友好，对DDL和DCL语句支持极差，SimpleMybatis实现了一种极其简单高效的SQL<——>JSON映射模式


![示意图](https://user-images.githubusercontent.com/40593174/227458541-7904246e-a51e-45c7-8ff3-c51c0c47594b.png)

![e7b36012f25e6bafcb6b7bf702aa95c](https://github.com/zhangchuangiie/SimpleMybatis/assets/40593174/9879c5b3-2bd0-4c0f-bc69-1a7aee4fa2ab)


## 典型示例：
```Java List<LinkedHashMap<String, Object>> resultList =  baseMapper.select("SELECT * FROM user where 1=1 and name=? and password=? and number=?  ORDER BY ? asc LIMIT 1. 2,2",name,password,number,"time");

long resultCount =  baseMapper.count("SELECT count(*) FROM user where 1=1 and name=? and password=? and number=?",name,password,number);

LinkedHashMap<String, Object> resultObject =  baseMapper.get("SELECT  * FROM user where  id=?",id1);

int result = baseMapper.insert("INSERT INTO user(name,password,number,time) VALUES(?,?,?,?)",name,password,number,currentDateString);

int result = baseMapper.update("update user set name=?,password=?,number=? where id=?",name,password,number,id1);

int result = baseMapper.delete("delete from user where id=?",id);

int result=  baseMapper.execute("Truncate Table log");
```

## 特点：
1. 无需为具体库表建立实体类和Mapper，统一使用BaseMapper即可
2. 采用弱类型返回结果集，这个返回值无需任何转换可以直接在SpringBoot的Controller里面做响应（一般也不需要Service）
3. 将controller、entity、mapper、service、resources简化为只要在controller接口里面直接写逻辑，每一组接口只要写一个java文件
4. 对DDL，DCL也很好的支持，适合大量动态建表的业务
5. 对复杂的联表查询，聚合查询支持很好，无需任何配置和定义，直接可以执行复杂查询SQL语句
6. 对存储过程支持友好，支持结果回传
7. BaseMapper将数据库操作抽象为10种，在JDBC语义和ORM语义间做了平衡，支持日常CRUD操作
8. 正常直接使用BaseMapper（带BaseMapperAspect装饰器）版本，另有一个单文件集成的原生JDBCUtil版本，两个版本接口的基本形式都是一样的
9. 支持JDBC中?占位符，跟原生JDBC的SQL占位符写法习惯一致，实际的值通过后面的可变参数传递
10. 在CRUDTask文件中可以查看调用示例
11. APITemplate文件是典型的CRUD接口逻辑代码，结合SQLBuilderUtil可以实现简单SQL语句的自动生成，结合AutoValueFromSqlUtil可以实现接口参数的自动填充

## 使用方式：
1. **试用方式:** 只需要集成1个BaseMapper文件即可，集成和使用方式跟正常的Mapper相同（在不使用?占位符和不需要时间格式化的情况下，跟正常模式接口完全一样）
2. **正常方式:** 集成2个BaseMapper\*文件即可，集成和使用方式跟正常的Mapper相同，BaseMapperAspect是装饰器（参数占位符加工和结果集时间格式化）
3. **备用方式:** 为在没有Mybatis的环境下使用该风格接口，直接用JDBC实现了原生版本，接口跟BaseMapper是一样的，这种方式只需要集成一个JDBCUtil文件即可，这个是单连接版本，对应的连接池版本可以使用JDBCUtilHikari.java

## 接口介绍：
1. **select：** 所有的结果集查询接口，直接拼SQL查询语句一行代码调用函数即可，返回值就是直接SpringBoot可以响应的格式（当然也可以加工后返回），无需bean，无需新建mapper，支持分组查询，连接查询，子查询，组合查询（UNION），视图查询，各种统计类的查询也直接用这个接口即可，别名as什么SpringBoot响应的json字段就是什么（也就是LinkedHashMap的key）
2. **count:** 所有的单值计数查询的快捷接口，也可以用于分页接口的总数接口，直接返回就是一个long型的数字，无需任何解析和加工
3. **get：** 所有的单对象点查询的快捷接口，返回的直接是一个对象（函数返回LinkedHashMap，SpringBoot响应json对象）
4. **insert：** 所有的插入语句接口，返回值是成功行数
5. **insertForID：** 所有的插入语句，支持获得被插入数据的主键ID值的版本，返回值是成功行数，主键ID值在map.id
6. **update：** 所有的更新语句接口，返回值是成功行数
7. **delete：** 所有的删除语句接口，返回值是成功行数
8. **execute：** 所有的执行语句接口，返回值是成功行数，支持所有的DDL，DCL语句，常用于建库建表，建用户，赋权等操作
9. **executeBatch：** 支持批处理的接口，可以一次执行多条语句
10. **call：** 支持调用存储过程的接口，支持带结果集存储过程，也支持带OUT参数

## 接口列表：
1. **select：** List<LinkedHashMap<String, Object>> select(String sql,Object ...args)
2. **count:** long count(String sql,Object ...args)
3. **get：** LinkedHashMap<String, Object> get(String sql,Object ...args)
4. **insert：** int insert(String sql,Object ...args)
5. **insertForID：** int insertForID(String sql,Map map,Object ...args)
6. **update：** int update(String sql,Object ...args)
7. **delete：** int delete(String sql,Object ...args)
8. **execute：** int execute(String sql,Object ...args)
9. **executeBatch：** int executeBatch(List<String> sql,Object ...args)
10. **call：** List<LinkedHashMap<String, Object>> call(String sql,Map map,Object ...args)

## 联系人：
有问题可以联系:zhangchuang@iie.ac.cn
