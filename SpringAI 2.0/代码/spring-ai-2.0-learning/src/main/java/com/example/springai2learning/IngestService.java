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
