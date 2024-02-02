package com.a708.drwa.room.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() { // Elasticsearch 서버와의 통신
        ElasticsearchTransport transport = new RestClientTransport(
                RestClient.builder(new HttpHost("localhost", 9200)).build(),
                new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }

    @Bean
    public boolean createIndex(ElasticsearchClient elasticsearchClient) { // 인덱스 생성 (+ 한국어 텍스트 분석을 위한 매핑)
        try {
            boolean exists = elasticsearchClient.indices().exists(e -> e.index("room_index")).value();
            if (!exists) { // 인덱스 없으면
                // 인덱스 생성 요청
                var response = elasticsearchClient.indices().create(c -> c
                        .index("room_index")
                        .mappings(m -> m
                                .properties("title", p -> p
                                        .text(t -> t.analyzer("korean"))
                                )
                                .properties("keyword", p -> p
                                        .text(t -> t.analyzer("korean"))
                                )
                        )
                );
                return response.acknowledged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
