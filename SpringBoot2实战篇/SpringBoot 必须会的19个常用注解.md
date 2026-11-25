# SpringBoot 必须会的19个常用注解

## @SpringBootApplication
这是开启SpringBoot项目的首要注解
```
@SpringBootApplication
public class SpringbootLearningApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootLearningApplication.class, args);
    }
}
```
## @Autowired
装配容器内对应的bean,自动导入到当前对象
```
@RestController
public class IndexController {
    @Autowired
    private TestService testService;
}
```
## @Service
一般标记业务层组件
```
@Service
public class TestService {
}
```
## @Repository
一般标记持久层组件
```
@Repository
public class TestDao {
}
```
## @Controller
对应SpringMVC层
```
@Controller
public class IndexController {

}
```
## @RestController
该注解是@Controller和@ResponseBody的结合体，将响应数据直接塞到响应体里面
```
@RestController
public class IndexController {
}
```
## @Configuration
对应Spring 的Beans定义
```
@Configuration
public class TestConfiguration {
}
```
## @Bean
对应xml中的bean定义
```
@Configuration
public class TestConfiguration {
    @Bean
    public TestHelper testHelper(){
        return new TestHelper();
    }
}
```
## @RequestMapping
定义uri，接口的访问地址
```
    @RequestMapping("/helloworld")
    public String helloWorld(){
        return "helloWorld";
    }
```
## @GetMapping
只允许get方式请求
```
 @GetMapping("/helloworld")
    public String helloWorld(){
        return "helloWorld";
    }
```
## @PostMapping
只允许post方式请求
```
 @PostMapping("/helloworld")
    public String helloWorld(){
        return "helloWorld";
    }
```
## @DeleteMapping
只允许delete方式请求
```
 @DeleteMapping("/helloworld")
    public String helloWorld(){
        return "helloWorld";
    }
```
## @PatchMapping
只允许patch方式请求
```
 @PatchMapping("/helloworld")
    public String helloWorld(){
        return "helloWorld";
    }
```
## @PutMapping
只允许put方式请求
```
 @PutMapping("/helloworld")
    public String helloWorld(){
        return "helloWorld";
    }
```
## @PathVariable
接收占位符方式传参
```
    @GetMapping("/helloworld/{name}/{age}")
    public String helloWorld(@PathVariable("name") String name,@PathVariable("age") String age){
        return name;
    }
```
## @RequestParam
接受url中的参数
```
   public String helloWorld(@RequestParam String name){
        return name;
    }
```
## @RequestBody
接收请求体里面的参数,一般用来接收json字符串转化为实体对象
```
   public String helloWorld(@RequestBody User user){
        return user.getName();
    }
```
## @value
读取配置文件的变量
```
    @Value("${name}")
    private String name;
```
## @ConfigurationProperties
整体读取配置文件的变量
```
@Configuration
@ConfigurationProperties(prefix = "user")
public class UserConfig {
    private String userName;
    private String age;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
```
```
    @Autowired
    private UserConfig userConfig;
```
