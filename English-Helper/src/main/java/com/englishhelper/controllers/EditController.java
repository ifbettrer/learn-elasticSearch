package com.englishhelper.controllers;

import com.alibaba.fastjson.JSON;
import com.englishhelper.pojo.Document;
import com.englishhelper.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/file")
public class EditController {

    @Autowired  //按照类型匹配
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @PostMapping("/edit/{docname}")
    public Result editDoc(@PathVariable String docname, @RequestBody String content) throws IOException {  //修改后将其添加入待修改库中
        //先将历史版本的changed改为1

        //再添加对应的文档
        addDocument(docname, content);

        return Result.success();
    }

    //或许可以单独拿出来后面引用
    public void addDocument( String docName, String content) throws IOException {  //添加文档
        Document document = new Document(docName,content, 0);
        IndexRequest request = new IndexRequest("text-to-improve");

        request.timeout(TimeValue.timeValueSeconds(1));
        IndexRequest source = request.source(JSON.toJSONString(document), XContentType.JSON);

        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
    }

}
