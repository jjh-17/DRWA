package com.a708.drwa.room.config;

import com.a708.drwa.room.domain.Room;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200));
        return new ElasticsearchClient(builder.build()); // RestClientBuilder를 build()로 생성
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }

    @Bean
    public boolean createIndex(ElasticsearchOperations operations) {
        IndexOperations indexOps = operations.indexOps(Room.class);

        if (!indexOps.exists()) {
            return indexOps.create();
            //settings, mappings 생ㅇ성 및 정의
        }
        return false;
    }

}
