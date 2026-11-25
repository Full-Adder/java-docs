# SpringBoot配置文件yaml格式和properties格式的常见写法

## 普通写法
- yaml
```
student:
  name: 编程学习
```

- properties
```
student.name= 编程学习
```
SpringBoot读取：
```
 @Value("${student.name}")
    private String name;
```
## 数组

- yaml
```
student:
  name: 编程学习
  province:
    - 安庆
    - 黄山
    - 六安
```
```
student:
  name: 编程学习
  province: [安徽,北京,上海]
```

- properties
```
student.name= 编程学习22
student.province[0]= 安徽1
student.province[1]= 北京1
student.province[2]= 上海1
```
```
student.name= 编程学习22
student.province= 安徽1,北京1,上海1
```
SpringBoot读取方式
```
@ConfigurationProperties(prefix = "student")
@Configuration
public class StudentConfig {
    private String name;
    private List<String> province;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProvince() {
        return province;
    }

    public void setProvince(List<String> province) {
        this.province = province;
    }
}
```
## 对象
SpringBoot读取
```
@ConfigurationProperties(prefix = "student")
@Configuration
public class StudentConfig {
    private String name;
    private List<String> province;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProvince() {
        return province;
    }

    public void setProvince(List<String> province) {
        this.province = province;
    }
}
```

- yaml
```
student:
  name: 编程学习
  city: [安徽,北京,上海]
```

- properties
```
student.ame= 编程学习22
student.province= 安徽1,北京1,上海1
```
