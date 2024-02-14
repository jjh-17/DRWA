package com.a708.drwa.room.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Value("${spring.elasticsearch.port}")
    private int port;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    /**
     * Elasticsearch 클라이언트 빈 등록
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//        ElasticsearchTransport transport = new RestClientTransport(
//                RestClient.builder(
//                        new HttpHost(host, port, "https")
//                ).build(),
//                new JacksonJsonpMapper()
//        );
//
//        return new ElasticsearchClient(transport);
        // SSL 컨텍스트 설정
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((chain, authType) -> true) // 모든 인증서 신뢰 (주의: 실제 프로덕션 환경에서는 적절한 인증서 검증 필요)
                .build();

        // 기본 인증 설정
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // 인증 정보 설정
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        // RestClient 빌더를 통해 Elasticsearch 클라이언트 생성
        RestClient restClient = RestClient.builder(new HttpHost(host, port, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> { // HttpClient 설정
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider); // 기본 인증 정보 설정
                    return httpClientBuilder.setSSLContext(sslContext); // SSL 컨텍스트 설정
                }).build();

        // Elasticsearch 클라이언트 생성
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
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