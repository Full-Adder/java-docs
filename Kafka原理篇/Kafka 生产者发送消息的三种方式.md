## Kafka 生产者发送消息的三种方式
## 创建配置文件
```
server:
  port: 8080
spring:
  kafka:
    bootstrap-servers: 192.168.64.3:9092
    listener:
      ack-mode: manual_immediate
    producer:
      acks: 1
      batch-size: 16384
      buffer-memory: 33554432
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      properties:
        linger:
          ms: 0
      retries: 0
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```
## 引入依赖
```
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```
## 配置消费者
```
@Component
@Slf4j
public class KafkaConsumer {
    private final String topic = "topic_test";
    @KafkaListener(topics = topic, groupId = "group.springboot")
    public void topicListener(ConsumerRecord<String, String> record, Acknowledgment item) {
        log.info("消费消息：{}==,{}==,{};",record.topic(),record.partition(),record.value());
        //手动提交
        item.acknowledge();
    }
}
```

## 配置生产者监听器
```
@Component
@Slf4j
public class ProductSendListener implements ProducerListener {
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        log.info("消息发送成功:"+producerRecord.key());
    }

    @Override
    public void onError(ProducerRecord producerRecord, RecordMetadata recordMetadata, Exception exception) {
        log.info("消息发送失败:"+producerRecord.key());
    }
}
```

## 编写测试代码
```
package com.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
@Slf4j
class KafkaApplicationTests {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ProductSendListener productSendListener;
    String topic = "topic_test";

    @Test
    void sendMsg() {
        kafkaTemplate.setProducerListener(productSendListener);
        kafkaTemplate.send(topic, "hello kafka i`m SpringBoot");
    }

}
```

