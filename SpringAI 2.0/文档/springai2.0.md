# 拒绝冗长！Spring AI 2.0 超浓缩实战



一份面向 Java 工程师的 Spring AI 2.0 实战教学：90~120 分钟带你从零跑通 Java AI 应用的五大核心能力——对话、结构化输出、工具调用、对话记忆、RAG，外加 MCP 协议入门与 1.x 升级避坑。全程理论配代码，每一步都可运行、可验证。

## 你将学到什么（Learning Objectives）

- [ ] 理解 Spring AI 2.0 相比 1.x 的架构变化，以及为什么它值得 Java 工程师投入
- [ ] 用 ChatClient 跑通第一个 AI 对话应用（支持 OpenAI / DeepSeek 等兼容接口）
- [ ] 掌握 Prompt 模板与结构化输出，让 AI 直接返回 Java 对象
- [ ] 用 @Tool 实现工具调用，让 AI 操作你的业务代码
- [ ] 用 ChatMemory + Advisor 让 AI 记住上下文
- [ ] 用 VectorStore + ETL + RAG 搭一个企业知识库问答
- [ ] 了解 MCP 协议：把 Java 服务暴露为 AI 可调用的工具
- [ ] 拿到一份 1.x → 2.0 的升级避坑清单

## 前置要求

- 了解 Spring Boot 基础（依赖注入、自动配置、application.yml）
- JDK 17+（建议 21），Maven 3.9+ 或 Gradle
- 一个大模型 API Key（OpenAI / DeepSeek / Anthropic 任一，本文以 DeepSeek 为例，注册即用、价格友好）

> ⚠️ 注意：Spring AI 2.0 基线是 Spring Boot 4.0/4.1 + Spring Framework 7.0，最低 JDK 17。如果你的老项目还在 Boot 2.x/3.x，建议新开工程练习，不要原地升级。

---

## 第一章 认知篇：Spring AI 2.0 到底变了什么（8 分钟）

### 1.1 Spring AI 是什么

一句话：**Spring AI 是 Spring 官方的 AI 应用开发框架，你可以把它理解为「AI 世界的 JDBC」**——就像当年 JDBC 统一了数据库访问，Spring AI 用统一的 `ChatClient` API 屏蔽了 OpenAI、Anthropic、DeepSeek、Ollama 等几十家模型的差异，换模型只改配置，不改代码。

对 Java 工程师的意义：**做 AI 应用 ≠ 转 Python**。AI 应用开发 ≈ 调 API + 编排 + 工程化——这恰好是 Java 工程师做了十几年的事。

### 1.2 2.0 的四个关键变化（2026-06-12 GA）

| 变化 | 1.x 时代 | 2.0 时代 | 对你的影响 |
|------|----------|----------|-----------|
| 基线 | Spring Boot 3.x、Jackson 2 | Spring Boot 4.0/4.1 + Framework 7 + Jackson 3 + JSpecify 空安全 | 老项目升级要动基线，新项目直接上 |
| 工具调用 | 工具循环埋在每个 ChatModel 内部 | `ToolCallingAdvisor` 成为 Advisor 链上的一等公民 | 工具调用全模型统一行为，可拦截、可组合 |
| MCP | 外挂模块 | 官方 MCP Java SDK 2.0 内置，`@McpTool` 注解驱动 | Java 服务一键变成 AI 的工具 |
| API 定位 | ChatClient/ChatModel 并重 | **ChatClient 是主入口**，ChatModel 降为底层积木 | 日常只学 ChatClient 就够了 |

> 💡 小知识：MCP（Model Context Protocol）是 Anthropic 发起的开放协议，定义了「AI 应用 ↔ 工具服务」之间的通信标准，2026 年已成为行业事实标准，Spring 团队官方维护其 Java 实现。

### 1.3 一张图理解 Spring AI 应用架构

```
用户请求
   │
   ▼
ChatClient（面向开发者的主入口）
   │
   ▼
Advisor 链（请求/响应拦截器：记忆、RAG、工具调用、审计……）
   │  ├── MessageChatMemoryAdvisor   ← 对话记忆
   │  ├── QuestionAnswerAdvisor     ← RAG 检索增强
   │  └── ToolCallingAdvisor        ← 工具调用循环（2.0 新一等公民）
   ▼
ChatModel（底层积木，对接 OpenAI / DeepSeek / Anthropic / Ollama …）
   │
   ▼
大模型 API
```

记住这个分层：**你写的代码主要在和 ChatClient 与 Advisor 打交道**。

---

## 第二章 五分钟跑通第一个 AI 应用（10 分钟）

### Step 1：创建工程

用 [start.spring.io](https://start.spring.io) 或 [start.aliyun.io](https://start.aliyun.io) 新建工程：

- Project：Maven / Java 21 / Spring Boot 4.0.x
- Dependencies：Spring Web、Spring AI

### Step 2：添加依赖

```xml
<!-- pom.xml -->
<dependencyManagement>
    <dependencies>
        <!-- Spring AI 版本管理（BOM） -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>2.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- OpenAI 兼容模型 starter（DeepSeek/通义/Kimi 等兼容接口都用它） -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

### Step 3：配置模型（以 DeepSeek 为例）

```yaml
# src/main/resources/application.yml
spring:
  ai:
    openai:
      api-key: sk-b1b3f5a19ff944b4a0a10910c75dc3c1      # 建议用环境变量，别把 Key 提交到 Git
      base-url: https://api.deepseek.com
      chat:
        model: deepseek-v4-flash              # 2.0 移除了 .options 段，配置键更短
```

> ⚠️ 注意：1.x 的 `spring.ai.openai.chat.options.model` 在 2.0 简化为 `spring.ai.openai.chat.model`。如果你照着老教程抄配置报错，先检查这里。
>
> 💡 换模型零成本：改 `base-url` 和 `model` 即可切换到 OpenAI（`https://api.openai.com`）、通义千问（`https://dashscope.aliyuncs.com/compatible-mode/v1`）等兼容接口。

### Step 4：写第一个接口

```java
// src/main/java/com/example/demo/ChatController.java
@RestController
public class ChatController {

    private final ChatClient chatClient;

    // ChatClient.Builder 由 Spring AI 自动配置注入
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()   // 创建一次对话
                .user(message)       // 用户输入
                .call()              // 同步调用
                .content();           // 取文本结果
    }
}
```

### 预期输出

启动后访问 `http://localhost:8080/chat?message=用一句话介绍Spring AI`：

```
Spring AI 是 Spring 官方推出的 AI 应用开发框架，
它用统一的 API 屏蔽不同大模型的差异，让 Java 工程师用自己的技术栈就能构建 AI 应用。
```

### 发生了什么？

`ChatClient.Builder` 被自动配置好后，`.prompt().user().call().content()` 五个链式调用就完成了一次模型请求——这就是 Java 生态里的「Hello AI World」。

---

## 第三章 ChatClient 与提示工程：让 AI 返回 Java 对象（15 分钟）

### 3.1 流式输出：像打字机一样逐字返回

```java
// ChatController.java 新增
@GetMapping(value = "/stream", produces = "text/html;charset=UTF-8")
public Flux<String> stream(@RequestParam String message) {
    return chatClient.prompt()
            .user(message)
            .stream()               // 👈 流式调用
            .content();
}
```

用浏览器访问 `/stream`，能看到文字一段段「打」出来——前端体验直接拉满。

### 3.2 Prompt 模板：参数化你的提示词

```java
@GetMapping("/poet")
public String poet(@RequestParam String topic) {
    return chatClient.prompt()
            .system("你是一位资深Java技术作家，文风幽默")   // 系统人设
            .user(u -> u.text("写一首关于{topic}的五言打油诗，结尾埋一个Java梗")
                          .param("topic", topic))          // 模板参数
            .call()
            .content();
}
```

### 3.3 结构化输出：AI 直接返回 Java 对象（重点）

面试常问、业务最常用——让 AI 输出对象而不是自然语言：

```java
// 先定义记录类
public record BookInfo(String title, String author, double price, List<String> tags) {}

@GetMapping("/book")
public BookInfo book(@RequestParam String keyword) {
    return chatClient.prompt()
            .user("推荐一本关于" + keyword + "的经典书，返回书名、作者、大致价格和3个标签")
            .call()
            .entity(BookInfo.class);   // 👈 直接映射为 Java 对象
}
```

### 预期输出

```json
{
  "title": "Effective Java（第3版）",
  "author": "Joshua Bloch",
  "price": 89.0,
  "tags": ["Java", "最佳实践", "进阶"]
}
```

### 原理解释

`entity()` 底层会自动在提示词中追加格式约束，把模型输出解析为 JSON 再反序列化成目标类（2.0 用 Jackson 3 完成）。如果校验失败，2.0 新增的 `StructuredOutputValidationAdvisor` 会自动把错误信息喂回模型自我纠正，结构化输出的可靠性大幅提升。

---

## 第四章 工具调用 @Tool：让 AI 操作你的 Java 代码（20 分钟）

这是 2.0 架构改动最大的一章，也是 Agent 的地基。

### 4.1 什么是工具调用

AI 本身只会「说话」，不能查数据库、调接口。**工具调用（Tool Calling）就是你把 Java 方法登记给 AI，AI 判断需要时主动发起调用，拿到结果再组织回答。**

```
用户：北京今天多少度？
  → AI：我需要调天气工具（自主决策）
  → 执行你的 Java 方法 getWeather("北京")
  → AI 拿到 28℃，回复："北京今天 28℃，适合出门"
```

### 4.2 实战：一个订单查询工具

```java
// src/main/java/com/example/demo/OrderTools.java
@Component
public class OrderTools {

    // 模拟数据库
    private static final Map<String, String> ORDERS = Map.of(
            "A1001", "已发货，顺丰 SF1234567890",
            "A1002", "待支付，30 分钟后自动取消"
    );

    @Tool(description = "根据订单号查询订单状态和物流信息")   // 👈 核心
    public String getOrderStatus(@ToolParam(description = "订单号，格式如A1001") String orderId) {
        return ORDERS.getOrDefault(orderId, "订单号不存在");
    }
}
```

```java
// ChatController.java 新增
private final OrderTools orderTools;

public ChatController(ChatClient.Builder builder, OrderTools orderTools) {
    this.orderTools = orderTools;
    this.chatClient = builder
            .defaultTools(orderTools)    // 👈 注册工具
            .build();
}

@GetMapping("/agent")
public String agent(@RequestParam String message) {
    return chatClient.prompt()
            .user(message)
            .call()
            .content();
}
```

### 预期输出

访问 `/agent?message=我的订单A1001到哪了`：

```
您的订单 A1001 当前状态：已发货，由顺丰承运，运单号 SF1234567890，
可在顺丰小程序实时查看物流轨迹。
```

### 4.3 原理解释：ToolCallingAdvisor（2.0 的灵魂）

1.x 时代，工具循环埋在每个 ChatModel 内部，各家实现不一致、无法拦截。2.0 把它提升为 Advisor 链上的 `ToolCallingAdvisor`：

```
循环开始
  ├─ 发送对话历史给模型（含工具定义）
  ├─ 模型要求调用工具？
  │     ├─ 是 → ToolCallingManager 执行对应 Java 方法 → 结果追加进对话 → 回到循环开始
  │     └─ 否 → 返回最终回答，循环结束
```

**Advisor 链上的位置决定了行为**：把记忆 Advisor 放在工具循环之前，历史记录更紧凑；放在之后，则能留存完整的工具调用审计记录——这个设计空间在 1.x 是不存在的。

### 4.4 进阶：函数式工具（了解即可）

除 @Tool 注解外，也可以显式声明 `ToolCallback` Bean：

```java
@Bean
public ToolCallback currentTime() {
    return FunctionToolCallback.builder("currentTime", (String tz) ->
                    "当前时间：" + LocalDateTime.now(ZoneId.of(tz)))
            .description("查询指定时区的当前时间")
            .inputType(String.class)
            .build();
}
```

> ⚠️ 避坑：1.x 的 `toolNames()`、裸 `Function` Bean 自动发现（SpringBeanToolCallbackResolver）在 2.0 已移除，老代码必须显式注册。
>
> 💡 工具一多怎么办？2.0 的 `ToolSearchToolCallingAdvisor` 支持「渐进式工具发现」：不把全部工具定义塞给模型，而是给模型一个搜索工具按需检索，官方基准测试省 34%~64% token。开启：`spring.ai.chat.client.tool-search-advisor.enabled=true`。

---

## 第五章 对话记忆：让 AI 记住你是谁（10 分钟）

### 5.1 问题：ChatClient 默认失忆

上一章的对话每次都是「一锤子买卖」。真实应用需要多轮上下文。

### Step 1：注入 ChatMemory

```java
@Bean
public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
            .maxMessages(20)           // 滑动窗口：保留最近 20 条
            .build();
}
```

### Step 2：挂上记忆 Advisor

```java
 @Resource
    private ChatMemory chatMemory;

    public ChatController(ChatClient.Builder builder, OrderTools orderTools) {
        this.orderTools = orderTools;
        this.chatClient = builder
                .defaultTools(orderTools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())  // 👈 新增
                .build();
    }
```

### Step 3：带会话 ID 调用

```java
@GetMapping("/memory")
public String memory(@RequestParam String sessionId, @RequestParam String message) {
    return chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))  // 👈 关键
            .call()
            .content();
}
```

### 验证多轮记忆

```
GET /memory?sessionId=u001&message=我叫示例用户，我在学Spring AI
→ 你好示例用户！Spring AI 值得深入……

GET /memory?sessionId=u001&message=我叫什么名字？
→ 你叫示例用户，你正在学习 Spring AI。    ← 它记住了！
```

### 原理解释

`MessageChatMemoryAdvisor` 在每次请求前从 ChatMemory 拉取历史消息拼进 Prompt，响应后再把新消息写回。默认实现是内存存储，生产环境可替换为 JDBC/Redis 等持久化实现；社区扩展 `spring-ai-session` 还提供了事件溯源式记忆与 LLM 摘要压缩。

---

## 第六章 RAG 实战：让 AI 基于你的文档回答（20 分钟）

RAG（Retrieval-Augmented Generation，检索增强生成）= 先从你的知识库里检索相关内容，再让 AI「开卷考试」。解决大模型不知道你公司内部知识、且爱编造的问题。

### 6.1 RAG 工作原理

```
用户提问
   │
   ▼
① 向量化：把问题转成 Embedding（一串浮点数，语义相近则数值接近）
   │
   ▼
② 相似检索：在 VectorStore 中找语义最近的 Top-K 文档片段
   │
   ▼
③ 增强提示：把片段 + 问题一起发给模型
   │
   ▼
④ 模型基于给定材料回答（可溯源、不瞎编）
```

### 6.2 Step 1：导入知识入库（ETL 流水线）

```java
package com.example.springai2learning;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.annotation.PostConstruct;
import java.nio.file.Path;

@Service
public class IngestService {

    private static final Path COURSE_DOC =
            Path.of("/Users/ly/Desktop/课程/SpringAI 2.0/文档/编程学习.txt");

    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public IngestService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.splitter = new TokenTextSplitter();
    }

    @PostConstruct
    public void init() throws IOException {
        ingest(COURSE_DOC);
    }

    public void ingest(Path file) throws IOException {
        // ETL：读取 → 切块 → 向量化 → 入库
        var documents = new TikaDocumentReader(
                new FileSystemResource(file.toFile()))
                .read();
        vectorStore.add(splitter.apply(documents));
    }
}

```

### 6.3 Step 2：配置向量库（演示用内存版）

```java
@Bean
public VectorStore vectorStore(EmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
}
```

> 💡 生产环境把 `SimpleVectorStore` 换成 `RedisVectorStore`、`PgVectorStore`、`ElasticsearchVectorStore` 等，接口一致，只改 Bean 定义。`EmbeddingModel` 与主模型可分开配（如文本转向量用性价比高的模型）。

### 6.4 Step 3：挂上 RAG Advisor

```java
this.chatClient = builder
        .defaultTools(orderTools)
        .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore).build())   // 👈 RAG
        .build();
```

相关依赖

```
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-tika-document-reader</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-vector-store</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-vector-store-advisor</artifactId>   <!-- 改名后的 2.0 坐标 -->
        </dependency>
```

配置文件

```
      embedding: # 只覆盖 embedding 模态
        base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
        api-key: sk-5608ecae5def4d0da0b94bff710ddcd2
        model: text-embedding-v3
```



### 预期输出：

```
问：编程学习是谁？
```

### 6.5 常见问题：为什么检索不准？

| 现象 | 排查方向 |
|------|---------|
| 检索不到相关内容 | 切块太大/太小（TokenTextSplitter 默认 1000 token 左右较稳） |
| 中英文混检效果差 | 换更擅长中文的 Embedding 模型 |
| 回答引用了错误片段 | Top-K 调大一点试试，或加相似度阈值过滤 |

---

## 第七章 MCP 入门：把你的 Java 服务变成 AI 的工具（15 分钟）

### 7.1 从工具调用到 MCP

第四章的 @Tool 只在「单应用内」有效。**MCP 解决的是跨进程**：你的订单服务、天气服务、ERP 都独立部署，AI 应用通过 MCP 协议发现并调用它们——就像 USB 接口，任何设备插上就能用。

### 7.2 实战：写一个 MCP Server

新建一个 Boot 工程，加依赖：

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server-webmvc</artifactId>
</dependency>
```

```yaml
  ai:
    mcp:
      server:
        name: order-mcp-server
        version: 1.0.0
        protocol: STREAMABLE
```

把第四章的工具类挪过来，注解换成 MCP 的：

```java
package com.example.mcpserver;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class McpOrderTools {
    // 模拟数据库
    private static final Map<String, String> ORDERS = Map.of(
            "A1001", "已发货，顺丰 SF1234567890",
            "A1002", "待支付，30 分钟后自动取消"
    );

    @McpTool(description = "根据订单号查询订单状态和物流信息")   // 👈 MCP 注解
    public String getOrderStatus(@ToolParam(description = "订单号") String orderId) {
        return ORDERS.getOrDefault(orderId, "订单号不存在");
    }
}
```

### 预期输出

启动后，服务默认以 Streamable HTTP 方式暴露在 `/mcp` 端点（2.0 默认传输协议，替代了旧的 STDIO/SSE）。任何 MCP 客户端（包括 ChatGPT、Claude Desktop、你自己的 Spring AI 应用）都能接入。

### 7.3 AI 应用侧：接入 MCP Client

```xml
     <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-mcp-client</artifactId>
        </dependency>
```

```yaml
  ai:
    mcp:
      client:
        streamable-http:
          connections:
            order-server:
              url: http://localhost:9090
```

```
    public ChatController(ChatClient.Builder builder, OrderTools orderTools, ToolCallbackProvider mcpTools) {
        this.orderTools = orderTools;
        this.chatClient = builder
                .defaultTools(mcpTools)    // 👈 注册工具
                .build();
    }
```



配置好后，MCP 服务器的工具会自动注册进 `ToolCallingAdvisor`，用法和本地 @Tool 完全一致。

### 原理解释

- `@McpTool` 声明服务端暴露的工具；客户端通过 `SyncMcpToolCallbackProvider` 连接后发现工具，生成 ToolCallback 代理
- 2.0 内置 OAuth 2.0 / API-Key 安全支持与 OpenTelemetry 可观测性，企业级开箱即用

> 💡 思考题：MCP Server 要不要认证？生产环境一定要——用 OAuth 2.0 或 API-Key，别裸奔。社区扩展 spring-ai-community/mcp-security 提供了现成方案。

---

## 第八章 1.x → 2.0 升级避坑清单（7 分钟）

老项目迁移前对照自查：

| 坑 | 说明 | 迁移动作 |
|----|------|---------|
| Spring Boot 基线 | 必须升到 Boot 4.0/4.1 + Framework 7 | 先升 Boot 再升 Spring AI |
| `.options` 配置段 | 属性键移除 `.options` | `spring.ai.openai.chat.options.model` → `spring.ai.openai.chat.model` |
| `toolNames()` 移除 | 裸 Function Bean 不再被自动发现 | 改用 @Tool 或 `FunctionToolCallback.builder()` 显式注册 |
| `internalToolExecutionEnabled` 移除 | 内部工具执行不存在了 | 默认走 ToolCallingAdvisor；用户自控循环用 `AdvisorParams.toolCallingAdvisorAutoRegister(false)` |
| `ToolCallAdvisor` 改名 | 类名统一 | 全局替换为 `ToolCallingAdvisor` |
| Options API | `copy()/fromOptions()` 移除 | 改用 `.mutate()` 生成修改副本 |
| Jackson 2 → 3 | 序列化行为变化 | 自定义 ObjectMapper 的地方回归测试一遍 |

**一句话总结**：2.0 的 API 更少了、更一致了——`ChatClient` 一个入口 + Advisor 一条链，就是全部主线。

---

## 常见问题 FAQ

**Q1：不用科学上网能用吗？**
可以。DeepSeek、通义千问、Kimi 都提供 OpenAI 兼容接口，国内直连，改 `base-url` 即可。

**Q2：本地跑大模型行吗？**
行。配一个 Ollama（`ollama run qwen3`），starter 换成 Ollama 的即可，零 API 费用，适合调试。

**Q3：Spring AI 和 LangChain4j 怎么选？**
Spring 技术栈 → 无脑 Spring AI（自动配置、生态集成无缝）；需要框架中立、极致灵活 → LangChain4j。两者都成熟，别陷入选型内耗。

**Q4：AI 接口超时/报错怎么排查？**
先 curl 直连模型 API 确认 Key 和网络；再开 Spring AI 的调试日志 `logging.level.org.springframework.ai=DEBUG` 看完整请求响应。

**Q5：什么时候需要 Agent，什么时候 ChatClient 就够？**
单轮问答 + 简单工具 → ChatClient 足够；需要多步规划、循环决策 → 在工具调用基础上加 Agent 模式（ReAct），Spring AI 的 ToolCallingAdvisor 架构就是为此准备的。

---

## 下一步学习路径

2. **替换生产组件**：SimpleVectorStore → Redis/PgVector；内存 ChatMemory → JDBC/Redis 持久化
3. **深入 Agent**：研究 ToolCallingAdvisor 四个扩展点（doInitializeLoop / doBeforeCall / doAfterCall / doFinalizeLoop），实现审批门控等企业需求
4. **可观测性**：接入 Micrometer + OpenTelemetry，看每次调用的 token 消耗与延迟
5. **关注社区**：spring-ai-session（记忆增强）、spring-ai-agent-utils（Agent 工具箱）

---

## 元信息

- 目标读者：有 Spring Boot 基础的 Java 后端开发者（初级→中级）
- 前置知识：Spring Boot、Maven、REST 接口开发
- 技术栈版本：Spring AI 2.0.0 GA（2026-06-12）、Spring Boot 4.0.x、Spring Framework 7.0、JDK 17+（建议 21）、DeepSeek API（OpenAI 兼容）
- 预计阅读时间：35 分钟（跟练约 2 小时）
