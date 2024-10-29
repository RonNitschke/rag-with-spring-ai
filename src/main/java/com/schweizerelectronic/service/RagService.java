package com.schweizerelectronic.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.schweizerelectronic.repository.DocumentRepository;

@Service
public class RagService {
	
	private static final Logger LOG = LoggerFactory.getLogger(RagService.class);
	
    @Value("classpath:/prompts/system-qa.st")
    private Resource systemNdaPrompt;
    
    @Value("${queries.top-k:2}")
    private int topK;

    private final DocumentRepository documentRepository;
    
    
    
    public RagService( DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}
	public Prompt generatePromptFromClientPrompt(String clientPrompt) {
        List<Document> docs = documentRepository.similaritySearchWithTopK(clientPrompt, topK);
        Message systemMessage = getSystemMessage(docs);
        LOG.info("System message: {}", systemMessage.getContent());
        UserMessage userMessage = new UserMessage(clientPrompt);
        return new Prompt(List.of(systemMessage, userMessage));
    }
    private Message getSystemMessage(List<Document> similarDocuments) {
        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemNdaPrompt);
        return systemPromptTemplate.createMessage(Map.of("documents", documents));
    }
}
