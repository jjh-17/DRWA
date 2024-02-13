package com.a708.drwa.room.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Value("${spring.elasticsearch.port}")
    private int port;
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        ElasticsearchTransport transport = new RestClientTransport(
                RestClient.builder(
                        new HttpHost(host, port)
                ).build(),
                new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }

    @Bean
    public boolean createIndex(ElasticsearchClient elasticsearchClient) {
        try {
            boolean exists = elasticsearchClient.indices().exists(e -> e.index("room_index")).value();
            if (!exists) {
                var response = elasticsearchClient.indices().create(c -> c
                        .index("room_index")
                        .mappings(m -> m
                                .properties("title", p -> p.text(t -> t.analyzer("nori")))
                                .properties("keywordA", p -> p.text(t -> t.analyzer("nori")))
                                .properties("keywordB", p -> p.text(t -> t.analyzer("nori")))
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