package com.englishhelper.controllers;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.englishhelper.pojo.Document;
import com.englishhelper.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.cn;

@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {

    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator +"files";

    //单文件上传
//    @PostMapping("/upload")
//    public Result upload(MultipartFile file) throws IOException, TikaException, SAXException {
//        String originalFilename = file.getOriginalFilename();
//        String mainName = FileUtil.mainName(originalFilename);
//        String extName = FileUtil.extName("文件的后缀");
//
//        if (!FileUtil.exist(ROOT_PATH)){
//            FileUtil.mkdir(ROOT_PATH);   //如果当前文件的父级目录不存在，就创建
//        }
//        if (FileUtil.exist(ROOT_PATH + File.separator + originalFilename)){  //如果当前上传文件已经存在了，那么这个时候我就要重命名一个文件名称
//            originalFilename = System.currentTimeMillis() +"_" + mainName +"." + extName;
//        }
//        File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
//        file.transferTo(saveFile);  //存储文件到本地磁盘
//        String pdfContent = parsePDF(ROOT_PATH + File.separator + originalFilename);
//        System.out.println(pdfContent);
//
//        String url = "http://localhost:8080/file/download/" + originalFilename;
//
//        return  Result.success(url);  //需要返回链接
//    }

    @GetMapping("/download/{fileName}")
    public String download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        String filePath = ROOT_PATH + File.separator + fileName;
        if(!FileUtil.exist(filePath)){
            return "there is no such file.";
        }
        byte[] bytes = FileUtil.readBytes(filePath);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();

        return "success";
    }

    @PostMapping("/multiUpload/{NeoCollectionName}")   //批量上传到对应的范文库
    public Result multiUpload(@RequestParam(name = "file")List<MultipartFile> files, @PathVariable String NeoCollectionName) throws IOException, TikaException, SAXException {
        if (files == null || files.isEmpty()){
            return Result.success("上传文件不能为空");
        }
        //List<String> orgFilenameList = new ArrayList<>(files.size());
        List<String> urlList = new ArrayList<>();  //下载链接

        for (MultipartFile multipartFile : files){
            String orgFilename = multipartFile.getOriginalFilename();
            String mainName = FileUtil.mainName(orgFilename);
            String extName = FileUtil.extName("文件的后缀");

            if (!FileUtil.exist(ROOT_PATH)){
                FileUtil.mkdir(ROOT_PATH);   //如果当前文件的父级目录不存在，就创建
            }
            if (FileUtil.exist(ROOT_PATH + File.separator + orgFilename)){  //如果当前上传文件已经存在了，那么这个时候我就要重命名一个文件名称
                orgFilename = System.currentTimeMillis() +"_" + orgFilename;
            }

            File saveFile = new File(ROOT_PATH + File.separator + orgFilename);
            multipartFile.transferTo(saveFile);
            String pdfContent = parsePDF(ROOT_PATH + File.separator + orgFilename);
            addDocument(NeoCollectionName, mainName, pdfContent);
            //System.out.println(pdfContent);
            String url = "http://localhost:8080/file/download/" + orgFilename;
            urlList.add(url);
        }

        return Result.success(urlList);
    }

    public  String parsePDF(String FILE_PATH) throws IOException, TikaException, SAXException {

        File file = new File(FILE_PATH);
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream fileInputStream = new FileInputStream(file);
        ParseContext parseContext = new ParseContext();

        PDFParser pdfParser = new PDFParser();
        pdfParser.parse(fileInputStream, handler,metadata,parseContext);
        //System.out.println(handler.toString());

        return handler.toString();
    }

    @Autowired  //按照类型匹配
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    public void addDocument(String indexName, String docName, String pdfContent) throws IOException {  //给范文库内添加文档
        Document document = new Document(docName,pdfContent, 0);
        IndexRequest request = new IndexRequest(indexName);

        //规则 put /kuang_index/_doc/1
        request.timeout(TimeValue.timeValueSeconds(1));

        //将数据放入请求
        IndexRequest source = request.source(JSON.toJSONString(document), XContentType.JSON);

        //发送请求
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
    }

}
