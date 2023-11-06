package com.englishhelper.controllers;

import com.englishhelper.pojo.Document;
import com.englishhelper.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/file")
public class SearchController {

    @Autowired  //按照类型匹配
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @GetMapping ("/search/{indexName}")
    public Result searchEx(@PathVariable String indexName, @RequestBody String words) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询一个字段
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("content", words);
        request.source().query(matchQueryBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        List<String> contents = new ArrayList<>();
        for (SearchHit documentFields : response.getHits().getHits()){
            contents.add(documentFields.getSourceAsMap().get("content").toString())  ;  //仅获取内容, 且只有一条
        }

        return Result.success(contents);
    }

}
