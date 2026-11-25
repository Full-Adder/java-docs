# SpringBoot 整合ES，解放你的mysql
## 引言
最近在准备一期SpringBoot整合大全系列文章，同时也会有视频放出(视频还在酝酿中)，如果大家觉得有帮助，记得点赞加收藏哦。
话不多说，咱们直接进入正题。

代码已经上传到码云：[https://gitee.com/exampleclub/springboot-hyper-integration.git](https://gitee.com/exampleclub/springboot-hyper-integration.git),欢迎白嫖
## 开整
### 引入需要的依赖
前面创建springboot项目就不说了
```
   <dependencies>
        <!--        springboot相关包-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!--        springboot相关包-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--        数据源链接池 不是此部分必备包-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
        <!--        springboot相关包-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--        json序列化相关包 不是本部分关键包-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.70</version>
        </dependency>
        <!--        mybatis相关包 必备包-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.0.5</version>
        </dependency>
        <!--        mysql数据库驱动 必备包-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.23</version>
        </dependency>
        <!--        lombok 不用写写get和set，不是本部分必备包-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.18</version>
        </dependency>
    </dependencies>
```
### 扫描mapper包路径
在springboot启动类上添加注解
```
@SpringBootApplication
// 扫描指定包路径
@MapperScan("com.aims.mybatisplus.dao")
public class SpringbootMybatisPlusApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootMybatisPlusApplication.class, args);
    }
}
```
### 创建表语句
```
CREATE TABLE `member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_by` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `member_name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '会员名称',
  `member_type` tinyint(3) DEFAULT NULL COMMENT '会员类型',
  `member_phone` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '手机号',
  `member_level` tinyint(3) DEFAULT NULL COMMENT '会员等级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='会员表';
```
### 创建实体
```
package com.aims.mybatisplus.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description member
 * @author 示例作者
 * @date 2021-11-07
 */
@Data
// 如果表名和实体名称一样 则不需要使用这个注解
@TableName("member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 创建人
    */
    private String createBy;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 更新人
    */
    private String updateBy;

    /**
    * 更新时间
    */
    private Date updateTime;

    /**
    * 会员名称
    */
    private String memberName;

    /**
    * 会员类型
    */
    private int memberType;

    /**
    * 手机号
    */
    private String memberPhone;

    /**
    * 会员等级
    */
    private int memberLevel;

    public Member() {}
}
```
### 创建操作数据库的mapper层
你会发现mapper里面啥都不用写，开心的起飞，只要继承BaseMapper就好了
```
package com.aims.mybatisplus.dao;

import com.aims.mybatisplus.model.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 示例作者
 * @description memberMapper
 * @date 2021-11-07
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
// 用上

}
```
### 配置数据库链接
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot-integration?useUnicode=true&characterEncoding=utf-8
    username: aims
    password: aims
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
// 下面这个是开启sql打印的，不是必须要配置的
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```
到此 集成mybatisPlus已经完成了，我们现在写几个简单的接口来演示下mapper相关的api

## 添加相关接口
### 查询接口
通过lambada表达式轻松可以实现相关sql操作
```
   @GetMapping
    public String get(@RequestParam("name") String name) {
        
        LambdaQueryWrapper<Member> lambda = new QueryWrapper<Member>().lambda();
        lambda.eq(Member::getMemberName, name);
        Member member = memberMapper.selectOne(lambda);
        return Objects.isNull(member) ? "没找到信息" : JSON.toJSONString(member);
    }
```
### 新增接口
添加操作更是简单到无语，后续会出一篇文章讲解这方面的常规操作语法，敬请期待!
```
    @PostMapping
    public int post(@RequestBody Member member) {
        int effectNum = memberMapper.insert(member);
        return effectNum;
    }
```
