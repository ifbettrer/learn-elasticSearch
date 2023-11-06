package com.englishhelper.controllers;


import com.englishhelper.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/file")
public class GetContentController {  //查看文本信息,使用term搜索，找到对应的

    @Autowired  //按照类型匹配
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

//    @GetMapping("/{indexName}/{fileName}/content")
//    public Result getDoc(@PathVariable String indexName, @PathVariable String fileName) throws IOException {
//        SearchRequest searchRequest = new SearchRequest(indexName);
//        //构建搜索文件
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name",fileName);
//        searchSourceBuilder.query(termQueryBuilder);
//
//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//        //所有结果都在searchhit
//        System.out.println(searchResponse.getHits());
//
//
//        //仅显示content操作
//        System.out.println("=======================");
//
//        for (SearchHit documentFields : searchResponse.getHits().getHits()){
//            String content = documentFields.getSourceAsMap().get("content").toString();  //仅获取内容
//            System.out.println(content);
//        }
//
//        return Result.success();
//    }
//}

    @GetMapping("/{indexName}/{fileName}/content")  //只是查看对应文件内容，所以只有一条值
    public Result getDoc(@PathVariable String indexName, @PathVariable String fileName) throws IOException{
        //准备request
        SearchRequest request = new SearchRequest(indexName);
        //准备DSL
        //准备BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //添加term
        boolQuery.must(QueryBuilders.termQuery("name", fileName));
        boolQuery.must(QueryBuilders.termQuery("changed", 0));  //只查看还未被修改的
        request.source().query(boolQuery);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        String content = "";
        for (SearchHit documentFields : response.getHits().getHits()){
            content = documentFields.getSourceAsMap().get("content").toString();  //仅获取内容, 且只有一条
        }

        return Result.success(content);
    }

    @GetMapping("/{indexName}/{fileName}/past")  //查看修改记录
    public Result getPast(@PathVariable String indexName, @PathVariable String fileName) throws IOException{
        //准备request
        SearchRequest request = new SearchRequest(indexName);
        //准备DSL
        //准备BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //添加term
        boolQuery.must(QueryBuilders.termQuery("name", fileName));
        boolQuery.must(QueryBuilders.termQuery("changed", 1));  //是否修改label为1的
        request.source().query(boolQuery);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        List<String> content = new ArrayList<>();  //或许这边只需要
        for (SearchHit documentFields : response.getHits().getHits()){
            content.add(documentFields.getSourceAsMap().get("content").toString());
        }

        return Result.success(content);
    }
}

/*
GET kuangshen/_doc/_search
{
    "query":{
        "match":{
        "name":"狂神"
        }
    }
}
*/
