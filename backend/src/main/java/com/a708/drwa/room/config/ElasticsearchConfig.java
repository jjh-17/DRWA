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
    public ElasticsearchClient elasticsearchClient() {
        // ElasticsearchTransport를 사용하여 ElasticsearchClient 생성
        ElasticsearchTransport transport = new RestClientTransport(
                RestClient.builder(new HttpHost("localhost", 9200)).build(), // RestClient.builder().build()를 사용하여 RestClient 인스턴스 생성
                new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }

    @Bean
    public boolean createIndex(ElasticsearchClient elasticsearchClient) {
        try {
            var existsResponse = elasticsearchClient.indices().exists(e -> e.index("room_index"));
            if (!existsResponse.value()) {
                elasticsearchClient.indices().create(c -> c
                        .index("room_index")
                        .mappings(m -> m
                                .properties("title", p -> p
                                        .text(t -> t
                                                .analyzer("korean")
                                        )
                                )
                                .properties("keyword", p -> p
                                        .text(t -> t
                                                .analyzer("korean")
                                        )
                                )
                        )
                        .settings(s -> s
                                .analysis(a -> a
                                        .tokenizer("nori_tokenizer", t -> t
                                                .type("nori_tokenizer") // nori_tokenizer 설정
                                        )
                                        .analyzer("korean", an -> an
                                                .tokenizer("nori_tokenizer") // korean 분석기에 nori_tokenizer 사용
                                                .addCharFilters("html_strip") // 필요한 경우 추가 설정
                                        )
                                )
                        )
                );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
