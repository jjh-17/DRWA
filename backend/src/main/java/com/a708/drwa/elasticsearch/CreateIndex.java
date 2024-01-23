package com.a708.drwa.elasticsearch;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;

public class CreateIndex {

    public static void main(String[] args) throws IOException {
        // Elasticsearch 클라이언트 초기화
        try (RestHighLevelClient client = new RestHighLevelClient(RestClient.builder("localhost:9200"))) {

            // 인덱스 생성 요청 생성
            CreateIndexRequest request = new CreateIndexRequest("인덱스명");

            // 인덱스 설정 구성
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 1)         // 샤드의 수를 1로 설정 -> 프라이머리샤드(색인 작업을 처리하고 검색 작업을 분산 처리하는 역할)
                    .put("index.number_of_replicas", 0)       // 레플리카의 수를 0으로 설정 -> 레플리카샤드(프라이머리 샤드의 데이터를 복제하며, 데이터 복제본이 여러 노드에 분산 저장되므로 하나의 노드나 샤드의 장애에 대비)
                    .put("index.analysis.analyzer.nori_analyzer.type", "nori_tokenizer") // 사용할 분석기 설정 : Elasticsearch에서 사용할 인덱스 분석기를 설정합니다. "nori_analyzer"라는 이름의 분석기를 사용하며, 이 분석기의 타입을 "nori_tokenizer"로 설정합니다. 이렇게 설정하면 텍스트 필드에 한국어 형태소 분석을 수행하는 Nori 분석기를 사용하도록 인덱스가 설정됩니다.
            );

            // 매핑 설정 구성
            request.mapping("{\n" +
                    "  \"properties\": {\n" +
                    "    \"text\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"nori_analyzer\"\n" + // 텍스트 필드에 nori_analyzer 분석기 적용
                    "    }\n" +
                    "  }\n" +
                    "}", XContentType.JSON);

            // 인덱스 생성 요청 실행 및 응답 받기
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

            // 응답 결과 확인 및 출력
            if (createIndexResponse.isAcknowledged()) {
                System.out.println("인덱스 생성 성공"); // 인덱스 생성이 성공적으로 수행되었을 때
            } else {
                System.out.println("인덱스 생성 실패"); // 인덱스 생성이 실패했을 때
            }
        }
    }
}
