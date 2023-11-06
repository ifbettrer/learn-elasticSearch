package com.englishhelper.controllers;


import com.englishhelper.pojo.FileName;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/poi")
public class ESController {

    @Autowired  //按照类型匹配
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //创建范文库，前端获取名字
    @PostMapping("/createIndex")
    public String createIndex(@RequestBody FileName fileName) throws IOException {
        log.info(fileName.getName());
        CreateIndexRequest request = new CreateIndexRequest(fileName.getName());
        //执行创建请求,IndicesClient,请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(createIndexResponse);
        return "sunccess";
    }
}
