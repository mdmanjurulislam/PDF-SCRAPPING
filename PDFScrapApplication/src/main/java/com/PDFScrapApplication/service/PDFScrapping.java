package com.PDFScrapApplication.service;

import com.PDFScrapApplication.repository.ScrappingRepo;
import com.PDFScrapApplication.model.TransactionEntity;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.utilities.PdfTable;
import com.spire.pdf.utilities.PdfTableExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PDFScrapping {

    @Autowired
    private ScrappingRepo scrappingRepo;

//Scrapping PDF file method
    public void scrapPDF(String path) throws IOException {
//        Calling splitPdf method make separate pdf file for each page of pdf
        SplitPdf(path);
        File dir = new File(path);

//        after split pdf file all the pdf file will store in the files array
        File[] files = dir.listFiles();
        for (File file : files) {
            PdfDocument pdf = new PdfDocument(String.valueOf(file));
            System.out.println("File Name : "+file.getName());

//Grabbing only table from the pdf file
            PdfTableExtractor extractor = new PdfTableExtractor(pdf);
//Every Single data will be store in this ArrayList after scrapping, after that will store all the data to the database table from this array list
            ArrayList<String> builder = new ArrayList();

            for (int pageIndex = 0; pageIndex < pdf.getPages().getCount(); pageIndex++) { // PER PAGE LOOP

                PdfTable[] pdfTables1 = extractor.extractTable(pageIndex);
                System.out.println("=============================");
                System.out.println("File Name : " + (file.getName()));
                System.out.println("=============================");

                if (pdfTables1 != null && pdfTables1.length > 1) {
                    PdfTable table = pdfTables1[1]; // get 2nd table

                    for (int i = 0; i < table.getRowCount(); i++) { // PER ROW LOOP

                        for (int j = 0; j < table.getColumnCount(); j++) {
                            String text = table.getText(i, j);

                            if (j == 0) {// 1st column split
                                // Pattern for strings with three parts
                                String patternThreeParts = "(\\d{2}/\\d{2}/\\d{2})\\s(\\d+)\\s(\\d{3})\\s(.+)";
                                // Pattern for strings with two parts
                                String patternTwoParts = "(\\d{2}/\\d{2}/\\d{2})\\s(\\d+)\\s(.+)";
                                // Pattern for strings with one part
                                String patternOnePart = "\\s*(.+)";

                                Pattern compiledPatternThreeParts = Pattern.compile(patternThreeParts, Pattern.DOTALL);
                                Matcher matcherThreeParts = compiledPatternThreeParts.matcher(text);

                                Pattern compiledPatternTwoParts = Pattern.compile(patternTwoParts, Pattern.DOTALL);
                                Matcher matcherTwoParts = compiledPatternTwoParts.matcher(text);

                                Pattern compiledPatternOnePart = Pattern.compile(patternOnePart, Pattern.DOTALL);
                                Matcher matcherOnePart = compiledPatternOnePart.matcher(text);

                                if (matcherThreeParts.find()) {
                                    String date = matcherThreeParts.group(1);
                                    String number = matcherThreeParts.group(2);
                                    String extraNumber = matcherThreeParts.group(3);
                                    String rest = matcherThreeParts.group(4);

                                    builder.add(date);
                                    builder.add(number);
                                    builder.add(extraNumber);
                                    builder.add(rest);
                                } else if (matcherTwoParts.find()) {
                                    String date = matcherTwoParts.group(1);
                                    String number = matcherTwoParts.group(2);
                                    String rest = matcherTwoParts.group(3);

                                    builder.add(date);
                                    builder.add(number);
                                    builder.add("null");
                                    builder.add(rest);
                                } else if (matcherOnePart.find() && !matcherOnePart.group(1).isEmpty()) {
                                    String rest = matcherOnePart.group(1);

                                    builder.add("null");
                                    builder.add("null");
                                    builder.add("null");
                                    builder.add(rest);
                                } else {
                                    builder.add("null");
                                    builder.add("null");
                                    builder.add("null");
                                    builder.add("null");
                                }

                            } else if (j == 4) {
                                String pattern = "(-?\\d{1,3}(,\\d{3})*\\.\\d+|\\d+\\.\\d+)";

                                Pattern compiledPattern = Pattern.compile(pattern);
                                Matcher matcher = compiledPattern.matcher(text);

                                // List to store the matched numbers
                                List<String> numbers = new ArrayList<>();
                                while (matcher.find()) {
                                    numbers.add(matcher.group().replaceAll(",", ""));
                                }

                                // Adding the numbers to the result or setting as "null" if missing
                                if (numbers.size() > 0) {
                                    builder.add(numbers.size() > 0 ? numbers.get(0) : "null");
                                    builder.add(numbers.size() > 1 ? numbers.get(1) : "null");
                                    builder.add(numbers.size() > 2 ? numbers.get(2) : "null");
                                } else {
                                    builder.add("null");
                                    builder.add("null");
                                    builder.add("null");
                                }
                            } else if (j == 5) {
                                String pattern = "(-?\\d{1,3}(,\\d{3})*\\.\\d+|\\d+\\.\\d+)";

                                Pattern compiledPattern = Pattern.compile(pattern);
                                Matcher matcher = compiledPattern.matcher(text);

                                // List to store the matched numbers
                                List<String> numbers = new ArrayList<>();
                                while (matcher.find()) {
                                    numbers.add(matcher.group().replaceAll(",", ""));
                                }

                                // Adding the numbers to the result or setting as "null" if missing
                                if (numbers.size() > 0) {
                                    builder.add(numbers.size() > 0 ? numbers.get(0) : "null");
                                    builder.add(numbers.size() > 1 ? numbers.get(1) : "null");
                                    builder.add(numbers.size() > 2 ? numbers.get(2) : "null");
                                } else {
                                    builder.add("null");
                                    builder.add("null");
                                    builder.add("null");
                                }
                            } else {
                                if(text.trim().isEmpty()) {
                                    builder.add("null");
                                }else {
                                    builder.add(text);
                                }
                            }
                        }
                        saveData(builder,file.getName());
                        System.out.println("==========================");
                    } // END ROW LOOP
                }
            }

//            Destination Directory Path.After reading or scrapping file,file will move to the destination directory
            String dest_dir ="C:\\Users\\manjurul.sohag\\Desktop\\Manjurul Islam\\Manjurul Islam\\PDF Scrapping\\PDFScrapApplication\\src\\main\\resources\\readed_file";
//            Calling the move file method
            moveFile(file,dest_dir);
        }
    }

// Saving all the data to the database table
    public void saveData(ArrayList<String> builder,String fileName){
        System.out.println(builder.toString());
        TransactionEntity transactionEntity = new TransactionEntity();

        transactionEntity.setTrans_date(builder.get(0));
        transactionEntity.setTrans_no(builder.get(1));
        transactionEntity.setContra_br(builder.get(2));
        transactionEntity.setParticulars(builder.get(3));

        transactionEntity.setDebit(builder.get(4));
        transactionEntity.setCredit(builder.get(5));
        transactionEntity.setEx_rating(builder.get(6));

        transactionEntity.setDebit_amt_fc(builder.get(7));
        transactionEntity.setCredit_amt_fc(builder.get(8));
        transactionEntity.setBalance_fc(builder.get(9));

        transactionEntity.setDebit_amt_lc(builder.get(10));
        transactionEntity.setCredit_amt_lc(builder.get(11));
        transactionEntity.setBalance_lc(builder.get(12));

        transactionEntity.setSource_file(fileName);
        scrappingRepo.save(transactionEntity);
        builder.clear();

    }


//    If file read successfully done then fill directory will change to another directory
    public static boolean moveFile(File sourceFile, String desPath) {
        boolean hasMove = false;
        desPath = desPath + File.separator + sourceFile.getName();

        try {
            Path p = Files.move(Paths.get(sourceFile.getAbsolutePath()), Paths.get(desPath), StandardCopyOption.REPLACE_EXISTING);
            if (p.toString().equals(desPath)) {
                hasMove = true;
            }
        } catch (IOException var5) {
            log.error("error {}, \nMessage *** : {}", var5, var5.getLocalizedMessage());
        }
        return hasMove;
    }

//Splitting PDF file pages to make a single page pdf file.This way we will cut every single page of a single pdf then make it individual pdf file for each pdf pages.
    public static void SplitPdf(String filePath) throws IOException {
        File dir = new File(filePath);
        File[] files = dir.listFiles();
        if (files.length > 0) {
            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                PDDocument document = PDDocument.load(file);
                Splitter splitting = new Splitter();
                List<PDDocument> Page = splitting.split(document);
                Iterator<PDDocument> iteration = Page.listIterator();
                int var11 = 1;

                while(iteration.hasNext()) {
                    PDDocument pd = (PDDocument)iteration.next();
                    pd.save(filePath + "/" + var11++ + "-" + file.getName());
                }

                System.out.println("Splitted Pdf Successfully.");
                document.close();
//                File will be deleted from the directory after completed scrapping
                file.delete();
            }
        }
    }
}
