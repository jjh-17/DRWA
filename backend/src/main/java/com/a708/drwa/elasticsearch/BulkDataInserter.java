package com.a708.drwa.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.action.index.IndexRequest;
import org.apache.http.HttpHost;

import java.io.IOException;

public class BulkDataInserter {

    public static void main(String[] args) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")))) {

            // Bulk 요청 생성
            BulkRequest request = new BulkRequest();

            // 데이터 세트 추가
            String[] dataSet = {
                    "{\"text\":\"서울은 대한민국의 수도입니다.\"}",
                    "{\"text\":\"비빔밥은 한국의 전통 음식 중 하나입니다.\"}",
                    // 더 추가
            };

            // 각 데이터를 Bulk 요청에 추가
            for (int i = 0; i < dataSet.length; i++) {
                request.add(new IndexRequest("인덱스명")
                        .source(dataSet[i], XContentType.JSON));
            }

            // Bulk 요청 실행
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);

            // 응답 결과 출력
            if (!bulkResponse.hasFailures()) {
                System.out.println("모든 문서 삽입 성공");
            } else {
                System.out.println("일부 문서 삽입 실패");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
