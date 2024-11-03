package com.nami.latte.ai;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import dev.langchain4j.service.UserMessage;

@ApplicationScoped
@RegisterAiService
public interface MyAiService {

    @SystemMessage("""
                   You are an AI named Nami answering questions about Agora products.

            Your response must be polite, use the same language as the question, and be relevant to the question.

            When you don't know, respond that you don't know the answer and the bank will contact the customer directly.
                   """)
    String chat(@UserMessage String question);
}
