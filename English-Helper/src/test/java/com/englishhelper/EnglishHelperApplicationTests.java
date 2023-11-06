package com.englishhelper;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
class EnglishHelperApplicationTests {

    @Test
    void  paserPDF() throws IOException, TikaException, SAXException {

        File file = new File("E:\\elasticSearch\\test\\test.pdf");
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream fileInputStream = new FileInputStream(file);
        ParseContext parseContext = new ParseContext();

        PDFParser pdfParser = new PDFParser();
        pdfParser.parse(fileInputStream, handler, metadata, parseContext);
        System.out.println(handler.toString());
    }
}
