# SpringBoot 整合MybatisPlus 实现字段自动填充

最近在整合MybatisPlus，发现mybatisPlus有很多有趣并且有用的功能，今天给大家分享一个：如何使用MybatisPlus实现数据库字段自动填充的功能。
## 前言
在我们日常开发中，我们经常会为每个表设置一个创建时间、更新时间或者创建人，更新人的字段，相信大家在这之前肯定都是手动塞值的，为每个表写同样一份代码。
今天我将给大家介绍下，如何使用MybatisPlus实现数据库字段自动填充的功能。
## 开整
### 添加注解
为需要填充的字段加上MybatisPlus注解，注意这个注解有两种常用场景

- insert:当执行insert语句才进行填充这个字段
- update：当执行update语句才进行填充这个字段

在这里我们给Member的创建时间和更新时间分别加上注解
```
package com.aims.mybatisplus.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description member
 * @author 示例作者
 * @date 2021-11-07
 */
@Data
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
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
    * 更新人
    */
    private String updateBy;

    /**
    * 更新时间
    */
    @TableField(fill = FieldFill.UPDATE)
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
    /**
    * 会员等级
    */
    private String tenantId;


    public Member() {}
}
```
### 添加配置
大概解释下这段代码：
1.MybatisPlus在执行插入或更新字段时，会扫描当前实体是否包含对应的注解，如果包含则会回调对应的insertFill或updateFill方法，在这个方法中，我们可以进行统一的赋值操作，如：设置时间，设置当前用户等信息
```
package com.aims.mybatisplus.conf;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)

}}
```
### 编写测试接口
```
package com.aims.mybatisplus.controller;

import com.aims.mybatisplus.dao.MemberMapper;
import com.aims.mybatisplus.model.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testFieldFill")
public class TestFieldFIllController {
    @Autowired
    private MemberMapper memberMapper;
    @RequestMapping("insertFill")
    public String insertFill(){
        Member member = new Member();
        member.setMemberName("测试字段填充插入");
        memberMapper.insert(member);
        return "success";
    }
    @RequestMapping("updateFill")
    public String updateFill(@RequestParam long id){
        Member member = new Member();
        member.setMemberName("更新填充");
        member.setId(id);
        memberMapper.updateById(member);
        return "success";
    }
}

```
大家可以自行验证结果哈。
