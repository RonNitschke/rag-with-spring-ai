package com.schweizerelectronic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class ChatGeneratorService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChatGeneratorService.class);
	
    private final StreamingChatClient streamingChatClient;
    private final ChatClient chatClient;
    
    
    
    public ChatGeneratorService(StreamingChatClient streamingChatClient, ChatClient chatClient) {
		this.streamingChatClient = streamingChatClient;
		this.chatClient = chatClient;
	}
    
    
	public StreamingChatClient getStreamingChatClient() {
		return streamingChatClient;
	}


	public ChatClient getChatClient() {
		return chatClient;
	}


	public String chat(String message) {
        return getChatClient().call(message);
    }
	
    public Flux<String> chatStream(String message) {
        return getStreamingChatClient().stream(message);
    }
    
    
    public Flux<ChatResponse> chatStream(Prompt prompt) {
    	Flux<ChatResponse> chatResponse = getStreamingChatClient().stream(prompt);
    	chatResponse
        .doOnNext(response -> LOG.debug("Received ChatResponse: {}", response))
        .subscribe();
        return chatResponse;
    }
    
    
    public ChatResponse chat(Prompt prompt) {
    	return chatClient.call(prompt);
    }

}
