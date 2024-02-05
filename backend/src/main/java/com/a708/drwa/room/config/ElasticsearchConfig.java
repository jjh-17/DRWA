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

    @Value("${spring.data.elasticsearch.client.rest.uris}")
    private String url;

    @Value("${spring.data.elasticsearch.client.rest.port}")
    private int port;
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        ElasticsearchTransport transport = new RestClientTransport(
                RestClient.builder(new HttpHost(url, port)).build(),
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
                                .properties("title", p -> p
                                        .text(t -> t.analyzer("nori"))
                                )
                                .properties("keyword", p -> p
                                        .text(t -> t.analyzer("nori"))
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