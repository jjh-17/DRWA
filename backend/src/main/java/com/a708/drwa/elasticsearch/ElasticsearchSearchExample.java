//package com.a708.drwa.elasticsearch;
//
//
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.SearchHit;
//import org.apache.http.HttpHost;
//import java.io.IOException;
////import org.elasticsearch.xcontent.XContentType;
//// 다른 필요한 임포트들
//
//public class ElasticsearchSearchExample {
//
//    public static void main(String[] args) {
//        // Elasticsearch 클라이언트 초기화
//        try (RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http")))) {
//
//            // 검색 요청 생성
//            SearchRequest searchRequest = new SearchRequest("인덱스명");
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//            // 형태소 단위 검색 쿼리 설정
//            searchSourceBuilder.query(QueryBuilders.matchQuery("text", "검색어"));
//
//            // 검색 요청에 검색 소스 추가
//            searchRequest.source(searchSourceBuilder);
//
//            // 검색 실행
//            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//            // 검색 결과 출력
//            for (SearchHit hit : searchResponse.getHits().getHits()) {
//                System.out.println(hit.getSourceAsString());
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

package com.a708.drwa.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import java.io.IOException;

public class ElasticsearchSearchExample {

    public static void main(String[] args) {
        try (RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build()) {
            RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            ElasticsearchClient client = new ElasticsearchClient(transport);

            // 검색 요청 생성 및 실행
            SearchResponse<YourDocumentClass> searchResponse = client.search(s -> s
                            .index("인덱스명")
                            .query(q -> q
                                    .match(m -> m
                                            .field("text")
                                            .query("검색어")
                                    )
                            ),
                    YourDocumentClass.class
            );

            // 검색 결과 출력
            for (Hit<YourDocumentClass> hit : searchResponse.hits().hits()) {
                System.out.println(hit.source().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // YourDocumentClass는 엘라스틱서치 인덱스에 저장된 문서의 구조를 나타내는 클래스입니다.
    // 이 클래스는 인덱스의 필드 구조에 맞게 정의해야 합니다.
    public static class YourDocumentClass {
        // 인덱스에 있는 필드들을 여기에 정의하세요.
    }
}
