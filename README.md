# Java 技术文档合集

一份面向 Java 后端工程师的学习笔记与面试题资料合集。共 313 篇 Markdown 文档 + 49 份 PDF 面试资料，按知识点由浅入深排列。

## 在线阅读

打开 GitHub Pages 站点：<https://full-adder.github.io/java-docs/>

（仓库地址：<https://github.com/Full-Adder/java-docs>）

## 内容目录

文档按学习顺序组织，左侧导航栏分组如下：

1. Java 并发编程
2. JVM 原理
3. 设计模式
4. 正则表达式
5. 开发与面试经验
6. Spring Boot 实战 / 整合
7. MyBatis-Plus 实战
8. Spring Security
9. 微服务实战 / Spring Cloud Alibaba
10. ShardingSphere 实战
11. MySQL 原理
12. Redis 从入门到精通 / Redis 原理
13. Elasticsearch / Kafka / Netty / gRPC
14. Docker / Kubernetes
15. LangChain4j / Spring AI
16. 面试题 PDF 资料（下载）

## 本地查看

本站是纯静态页面，直接双击 `index.html` 即可浏览；或在本目录启动任意静态服务器：

```bash
python3 -m http.server 8000
```

然后访问 <http://localhost:8000>。

## 重新生成网页

内容变更后，运行构建脚本可重新生成所有 HTML 页面（需要 `mistune`）：

```bash
pip install mistune
python3 _build/build_site.py
```

脚本会：按知识点顺序整理目录、渲染 Markdown、生成侧边栏与搜索、输出下载索引。生成结果直接写入仓库根目录，由 GitHub Pages 发布。

## 说明

- 图片/附件保留在原始 `media/` 目录，网页直接引用。
- `.gitignore` 已排除 `.DS_Store`、`.idea/`、`target/`、`*.class` 等无关文件。
