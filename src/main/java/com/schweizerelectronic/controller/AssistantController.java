package com.schweizerelectronic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(AssistantController.class);
	
    private final ChatGeneratorService chatGeneratorService;
    private final RagService ragService;

    public AssistantController(ChatGeneratorService chatGeneratorService, RagService ragService) {
		this.chatGeneratorService = chatGeneratorService;
		this.ragService = ragService;
	}
	@PostMapping(value = "/chat", produces = "text/event-stream")
    public String prompt(@RequestBody String clientPrompt) {
		LOG.info("Client prompt = ", clientPrompt);
        Prompt prompt = ragService.generatePromptFromClientPrompt(clientPrompt);
        String response = chatGeneratorService.chat(prompt).getResult().getOutput().getContent();
        LOG.info("response = ", response);
        return response;
    }
	
	@PostMapping(value = "/chatStream", produces = "text/event-stream")
    public Flux<String> promptStream(@RequestBody String clientPrompt) {
        Prompt prompt = ragService.generatePromptFromClientPrompt(clientPrompt);
        Flux<String> promptString = chatGeneratorService.chatStream(prompt)
                .map(this::extractContentFromChatResponse);
        return promptString;
    }
	
    private String extractContentFromChatResponse(ChatResponse chatResponse) {
    	String content = chatResponse.getResult().getOutput().getContent();
        return content;
    }
}