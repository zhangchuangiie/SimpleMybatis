# SimpleMybatis
一个基于Mybatis封装的类JdbcTemplate风格的ORM工具

## 特点：
1. 无需为具体库表建立实体类和Mapper，统一使用BaseMapper即可
2. 采用弱类型返回结果集，这个返回值无需任何转换可以直接在SpringBoot的Controller里面做响应（一般也不需要Service）
3. 将controller、entity、mapper、service、resources简化为只要在controller接口里面直接写逻辑，每一组接口只要写一个java文件
4. 对DDL，DCL也很好的支持，适合大量动态建表的业务
5. BaseMapper将数据库操作抽象为8种，在JDBC语义和ORM语义间做了平衡，支持日常CRUD操作（call后续会加入）
6. 可以直接使用BaseMapper，也可以使用进一步封装的BaseDAO，这个在BaseMapper的基础上做了参数加工和结果集加工（支持?占位符、时间格式化）
7. 在CRUDTask文件中可以查看调用示例

## 联系人：
有问题可以联系:zhangchuang@iie.ac.cn
