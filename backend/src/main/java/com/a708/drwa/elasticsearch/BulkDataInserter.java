//package com.a708.drwa.elasticsearch;
//
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.xcontent.XContentType;
//import org.elasticsearch.action.index.IndexRequest;
//import org.apache.http.HttpHost;
//
//import java.io.IOException;
//
//public class BulkDataInserter {
//
//    public static void main(String[] args) {
//        try (RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http")))) {
//
//            // Bulk 요청 생성
//            BulkRequest request = new BulkRequest();
//
//            // 데이터 세트 추가
//            String[] dataSet = {
//                    "{\"text\":\"서울은 대한민국의 수도입니다.\"}",
//                    "{\"text\":\"비빔밥은 한국의 전통 음식 중 하나입니다.\"}",
//                    // 더 추가
//            };
//
//            // 각 데이터를 Bulk 요청에 추가
//            for (int i = 0; i < dataSet.length; i++) {
//                request.add(new IndexRequest("인덱스명")
//                        .source(dataSet[i], XContentType.JSON));
//            }
//
//            // Bulk 요청 실행
//            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
//
//            // 응답 결과 출력
//            if (!bulkResponse.hasFailures()) {
//                System.out.println("모든 문서 삽입 성공");
//            } else {
//                System.out.println("일부 문서 삽입 실패");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

package com.a708.drwa.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import java.io.IOException;
import java.util.Arrays;


public class BulkDataInserter {

    public static void main(String[] args) {
        try (RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build()) {
            RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            ElasticsearchClient client = new ElasticsearchClient(transport);

            // Bulk 요청 생성 및 실행
            BulkResponse response = client.bulk(b -> b
                    .index("인덱스명")
                    .operations(Arrays.asList(
                            // 데이터 세트 추가
                            BulkOperation.of(bo -> bo
                                    .index(i -> i
                                            .document(new YourDocumentClass("서울은 대한민국의 수도입니다."))
                                    )
                            ),
                            BulkOperation.of(bo -> bo
                                    .index(i -> i
                                            .document(new YourDocumentClass("비빔밥은 한국의 전통 음식 중 하나입니다."))
                                    )
                            )
                            // 더 추가
                    ))
                    .refresh(Refresh.True)
            );

            // 응답 확인
            if (!response.errors()) {
                System.out.println("모든 문서 삽입 성공");
            } else {
                System.out.println("일부 문서 삽입 실패");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // YourDocumentClass는 엘라스틱서치 인덱스에 저장된 문서의 구조를 나타내는 클래스입니다.
    public static class YourDocumentClass {
        // 인덱스에 있는 필드들을 여기에 정의하세요.
        private String text;

        public YourDocumentClass(String text) {
            this.text = text;
        }

        // getter 및 setter 추가
    }
}


