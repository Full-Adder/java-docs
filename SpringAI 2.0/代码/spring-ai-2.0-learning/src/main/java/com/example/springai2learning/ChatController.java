package com.example.springai2learning;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class ChatController {
//    @Bean
//    public ChatMemory chatMemory() {
//        return MessageWindowChatMemory.builder()
//                .maxMessages(20)           // 滑动窗口：保留最近 20 条
//                .build();
//    }


    private final ChatClient chatClient;

    private final OrderTools orderTools;

//    @Resource
//    private ChatMemory chatMemory;




//    public ChatController(ChatClient.Builder builder, OrderTools orderTools, ChatMemory chatMemory) {
//        this.orderTools = orderTools;
//        this.chatClient = builder
//                .defaultTools(orderTools)
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())  // 👈 新增
//                .build();
//    }

    public ChatController(ChatClient.Builder builder, OrderTools orderTools, ChatMemory chatMemory, ToolCallbackProvider mcpTools) {
        this.orderTools = orderTools;
        this.chatClient = builder
                .defaultTools(mcpTools)
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())  // 👈 新增
                .build();
    }
//    public ChatController(ChatClient.Builder builder, OrderTools orderTools, ChatMemory chatMemory, VectorStore vectorStore) {
//        this.orderTools = orderTools;
//        this.chatClient = builder
//                .defaultTools(orderTools)
//                .defaultAdvisors(
////                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
//                        QuestionAnswerAdvisor.builder(vectorStore).build())   // 👈 RAG
//                .build();
//    }

    @GetMapping("/memory")
    public String memory(@RequestParam String sessionId, @RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))  // 👈 关键
                .call()
                .content();
    }

    @GetMapping("/agent")
    public String agent(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }


    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()   // 创建一次对话
                .user(message)       // 用户输入
                .call()              // 同步调用
                .content();           // 取文本结果
    }

    // ChatController.java 新增
    @GetMapping(value = "/stream", produces = "text/html;charset=UTF-8")
    public Flux<String> stream(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()               // 👈 流式调用
                .content();
    }
    @GetMapping("/poet")
    public String poet(@RequestParam String topic) {
        return chatClient.prompt()
                .system("你是一位资深Java技术作家，文风幽默")   // 系统人设
                .user(u -> u.text("写一首关于{topic}的五言打油诗，结尾埋一个Java梗")
                        .param("topic", topic))          // 模板参数
                .call()
                .content();
    }

    // 先定义记录类
    public record BookInfo(String title, String author, double price, List<String> tags) {}

    @GetMapping("/book")
    public BookInfo book(@RequestParam String keyword) {
        return chatClient.prompt()
                .user("推荐一本关于" + keyword + "的经典书，返回书名、作者、大致价格和3个标签")
                .call()
                .entity(BookInfo.class);   // 👈 直接映射为 Java 对象
    }
}