package com.PDFScrapApplication.controller;

import com.PDFScrapApplication.model.TransactionEntity;
import com.PDFScrapApplication.service.PDFScrapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PDFScrapController {

    String path = "C:\\Users\\manjurul.sohag\\Desktop\\Manjurul Islam\\Manjurul Islam\\PDF Scrapping\\PDFScrapApplication\\src\\main\\resources\\pdf_dir";


    @Autowired
    private PDFScrapping pdfScrapping;

    @PostMapping("/scrap")
    public String scrapPdf() throws IOException {
        pdfScrapping.scrapPDF(path);
        return "Done";
    }

    @GetMapping("/test")
    public String test(){
        return "Test API Successfully Run";
    }
}
