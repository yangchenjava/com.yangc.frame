com.yangc.frame
===============

###一个初级的权限框架
使用jdk6，maven项目<br />
后台 hibernate3.6.10 + spring3.2.8 + jersey1.18.1 + shiro1.2.3<br />
前端 extjs4.2 + jquery1.8<br />

###shiro
使用shiro实现登录认证和权限控制，将shiro默认的ehcache缓存改为使用redis缓存，通过配置文件，配置redis的分布式策略。

###jersey
通过jersey实现标准的rest请求格式。（所谓rest，无非是一种思维方式。rest面向的重点是服务、资源，访问者可以是浏览器或者手机app，重点在于给消费者提供怎样的服务。传统的请求方式，消费者是浏览器，受用者是web页面）

###hibernate
hibernate中主要使用的是HibernateTemplate和JdbcTemplate，封装了两个dao（BaseDao和JdbcDao）提供使用，BaseDao主要用来增、删、改、批量、和单表查询，JdbcDao主要负责多表联合的复杂查询，主要为了保证性能。并且sql语句使用xml作为载体写在代码外面，根据不同数据库写不同的sql语句，如：项目模块名-oracle-sql.xml、项目模块名-mysql-sql.xml

###Tips：
    1.通过过滤器获取页面分页的pageNow、pageSize放到ThreadLocal，无需每回在action（controller、resource..
      汗，叫的名称真是多种多样啊）的方法中写入这两个参数，直接通过ThreadLocal获取。
    2.通过hibernate的拦截器去保存公共信息，如：createTime、updateTime。
    3.重新授予权限后记得清空过期的权限缓存。（毕竟菜单、权限等不经常修改的信息适合放入缓存）
    4.想到了再写...
    
