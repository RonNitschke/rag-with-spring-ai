package com.schweizerelectronic.runner;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.schweizerelectronic.repository.DocumentRepository;
import com.schweizerelectronic.service.CsvParserService;

@Component
@ConditionalOnProperty(value = { "database.init" }, havingValue = "true")
public class DatabaseInitRunner implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseInitRunner.class);

	private final DocumentRepository documentRepository;
	private final CsvParserService csvParserService;

	@Value("classpath:data.csv")
	private Resource resource;

	public DatabaseInitRunner(DocumentRepository documentRepository, CsvParserService csvParserService) {
		this.documentRepository = documentRepository;
		this.csvParserService = csvParserService;
	}

	@Override
	public void run(ApplicationArguments args) {
		List<Document> documents = csvParserService.getContentFromCsv(resource);
		LOG.info("Adding documents to vector store");
		documents.forEach(doc -> LOG.debug("Document: {}", doc));
		documentRepository.addDocuments(documents);
		LOG.info("done!");
	}
}
