package com.a708.drwa.elasticsearch;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;

import java.util.List;

/**
 * Elasticsearch Configuration
 */
@Configuration
public class config {

    /**
     * Elasticsearch Rest Client Bean
     * @return RestClient
     */
    @Bean
    public RestClient getRestClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("", ""));
        return RestClient.builder(new HttpHost("localhost", 9200))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.disableAuthCaching();
                    httpClientBuilder.setDefaultHeaders(List.of(
                            new BasicHeader(
                                    HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())));
                    httpClientBuilder.addInterceptorLast((HttpResponseInterceptor)
                            (response, context) ->
                                    response.addHeader("X-Elastic-Product", "Elasticsearch"));
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }).build();
    }

    /**
     * Elasticsearch Transport Bean
     * @return ElasticsearchTransport
     */
    @Bean
    public ElasticsearchTransport getElasticsearchTransport() {
        return new RestClientTransport(getRestClient(), new JacksonJsonpMapper());
    }

    /**
     * Elasticsearch Client Bean
     * @return ElasticsearchClient
     */
    @Bean
    public ElasticsearchClient getElasticsearchClient() {
        return new ElasticsearchClient(getElasticsearchTransport());
    }

}