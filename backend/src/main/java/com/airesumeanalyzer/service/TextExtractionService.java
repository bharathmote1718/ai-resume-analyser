package com.airesumeanalyzer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TextExtractionService {

    public String extractText(MultipartFile file) throws IOException {
        String fileName = Objects.requireNonNull(file.getOriginalFilename(), "File name is required");
        String lower = fileName.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return extractPdfText(file.getInputStream());
        }
        if (lower.endsWith(".docx")) {
            return extractDocxText(file.getInputStream());
        }
        if (lower.endsWith(".txt")) {
            return new String(file.getBytes());
        }

        throw new IllegalArgumentException("Unsupported file type. Please upload PDF, DOCX, or TXT.");
    }

    private String extractPdfText(InputStream inputStream) throws IOException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractDocxText(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }
}
