# SpringBoot解决跨域问题

## 什么是跨域
当ajax请求的url中的传输协议、域名、端口号，有任意一个不同时，就会出现跨域请求
## 解决方案
### 一、注解解决
@CrossOrigin
### 二、配置解决
```
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
 
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(1800)
                .allowedOrigins("*");
    }
}
```
### 三、 nginx解决
```
location / {
        proxy_pass http://localhost:8080;
        }
        location /example {
        proxy_pass http://localhost:8848;
        }
```
