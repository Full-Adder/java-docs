# SpringBoot配置文件数据加密，拒绝暴露明文密码

## 加密的密码
```
mysql.username= ENC(+ldb11Yv/wR0IgGJIofVTzbicpBJ8nkFF6lV6mWE7oOMDCuW1ubgLL/wELK39pbp)
mysql.password= ENC(+cJ5Lzd4JCIum16d/ZEwrcGcst72Q2ogEMq+b2L508BcNb+U/krhMDUc0bQjyPwG)
```
## 引入坐标
```
        <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>3.0.3</version>
        </dependency>
```
## 添加虚拟机参数
```
-Djasypt.encryptor.password=123456
```
## 生成加密密码
```

    @Autowired
    private StringEncryptor stringEncryptor;
    System.out.println(stringEncryptor.encrypt("root"));
    System.out.println(stringEncryptor.encrypt("root"));
```
## 修改配置文件，使用加密的密码
```
mysql.username= ENC(+ldb11Yv/wR0IgGJIofVTzbicpBJ8nkFF6lV6mWE7oOMDCuW1ubgLL/wELK39pbp)
mysql.password= ENC(+cJ5Lzd4JCIum16d/ZEwrcGcst72Q2ogEMq+b2L508BcNb+U/krhMDUc0bQjyPwG)
```
## 读取密码，验证是否是明文
```
    @Value("${mysql.username}")
    private String username;
    @Value("${mysql.password}")
    private String password;
    
    System.out.println("username:"+username);
    System.out.println("password:"+password);
```
![image.png](media/16838989033428/16984588392348.png)
