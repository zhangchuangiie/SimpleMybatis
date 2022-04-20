# SimpleMybatis
一个基于Mybatis封装的类JdbcTemplate风格的ORM工具

## 特点：
1. 无需为具体库表建立实体类和Mapper，统一使用BaseMapper即可
2. 采用弱类型返回结果集，这个返回值无需任何转换可以直接在SpringBoot的Controller里面做响应（一般也不需要Service）
3. 将controller、entity、mapper、service、resources简化为只要在controller接口里面直接写逻辑，每一组接口只要写一个java文件
4. 对DDL，DCL也很好的支持，适合大量动态建表的业务
5. BaseMapper将数据库操作抽象为9种，在JDBC语义和ORM语义间做了平衡，支持日常CRUD操作
6. 可以直接使用BaseMapper，也可以使用进一步封装的BaseDAO（相当于BaseMapper的装饰器），这个在BaseMapper的基础上做了参数加工和结果集加工（支持?占位符、时间格式化）
7. 在CRUDTask文件中可以查看调用示例

## 使用方式：
1. **基本方式:** 只要集成BaseMapper的两个文件即可，集成和使用方式跟正常的Mapper相同
2. **扩展方式:** 在基本方式的基础上集成BaseDAO（以及其依赖的两个\*Util文件），集成和使用方式就是等同于一般的静态工具类

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

## 联系人：
有问题可以联系:zhangchuang@iie.ac.cn
