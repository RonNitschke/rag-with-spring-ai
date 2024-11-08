package com.schweizerelectronic.controller;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schweizerelectronic.service.ChatGeneratorService;
import com.schweizerelectronic.service.RagService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping(path = "/assistant")
public class AssistantController {
    private final ChatGeneratorService chatGeneratorService;
    private final RagService ragService;

    public AssistantController(ChatGeneratorService chatGeneratorService, RagService ragService) {
		this.chatGeneratorService = chatGeneratorService;
		this.ragService = ragService;
	}
	@PostMapping(value = "/chat", produces = "text/event-stream")
    public Flux<String> prompt(@RequestBody String clientPrompt) {
        Prompt prompt = ragService.generatePromptFromClientPrompt(clientPrompt);
        Flux<String> promptString = chatGeneratorService.generateStream(prompt)
                .map(this::extractContentFromChatResponse);
        return promptString;
    }
    private String extractContentFromChatResponse(ChatResponse chatResponse) {
    	String content = chatResponse.getResult().getOutput().getContent();
        return content;
    }

}
