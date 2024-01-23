package com.a708.drwa.elasticsearch;


import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.SearchHit;
import org.apache.http.HttpHost;
import java.io.IOException;
//import org.elasticsearch.xcontent.XContentType;
// 다른 필요한 임포트들

public class ElasticsearchSearchExample {

    public static void main(String[] args) {
        // Elasticsearch 클라이언트 초기화
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")))) {

            // 검색 요청 생성
            SearchRequest searchRequest = new SearchRequest("인덱스명");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 형태소 단위 검색 쿼리 설정
            searchSourceBuilder.query(QueryBuilders.matchQuery("text", "검색어"));

            // 검색 요청에 검색 소스 추가
            searchRequest.source(searchSourceBuilder);

            // 검색 실행
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // 검색 결과 출력
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                System.out.println(hit.getSourceAsString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
