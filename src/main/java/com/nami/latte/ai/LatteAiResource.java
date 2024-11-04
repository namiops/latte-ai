package com.nami.latte.ai;

import dev.langchain4j.model.ollama.OllamaChatModel;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("ai")
public class LatteAiResource {

    @Inject
    MyAiService service;

    @GET
    @Path("hello")
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @POST
    @Path("chat")
    @Produces(MediaType.TEXT_PLAIN)
    public String chat(String askString) {
        Log.infof("Ask: %s", askString);
        /* 
        OllamaChatModel ollamaChatModel = OllamaChatModel.builder()
                .baseUrl("http://192.168.1.11:11434")
                .modelName("llama3.2")
                .temperature(0.8)
                .timeout(Duration.ofSeconds(60))
                .build();

        String text = ollamaChatModel.generate(askString);
        */
        String text = service.chat(askString);

        Log.infof("Bot: %s", text);
        return text;
    }
}
