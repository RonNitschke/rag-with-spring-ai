package com.schweizerelectronic.service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.schweizerelectronic.model.Paragraph;

@Service
public class CsvParserService {
    public List<Document> getContentFromCsv(Resource resource){
        try (Reader reader = Files.newBufferedReader(Paths.get(resource.getURI()))
             ; CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .builder()
                .setHeader().setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setIgnoreHeaderCase(true)
                .build())) {
            List<Document> documentsToAdd = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                Paragraph paragraph = Paragraph.builder()
                        .id(Long.parseLong(csvRecord.get("ID")))
                        .page(Integer.parseInt(csvRecord.get("page")))
                        .title(Arrays.stream(csvRecord.get("Accommodation Type").split(" ")).filter(s -> s.equals(s.toUpperCase())).collect(Collectors.joining(" ")))
                        .content(csvRecord.get("Resort Explanation"))
                        .build();
                documentsToAdd.add(paragraph.toDocument(paragraph));
            }
            return documentsToAdd;
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }
}
