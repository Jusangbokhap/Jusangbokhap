package jsbh.Jusangbokhap.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;

@Configuration
public class ElasticSearchConfig {

    private final String host;
    private final int port;
    private final String login;
    private final String password;
    private final String fingerprint;
    private final ObjectMapper objectMapper;

    public ElasticSearchConfig(
            ObjectMapper objectMapper,
            @Value("${elasticsearch.custom.host}") String host,
            @Value("${elasticsearch.custom.port}") int port,
            @Value("${elasticsearch.custom.username}") String login,
            @Value("${elasticsearch.custom.password}") String password,
            @Value("${elasticsearch.custom.fingerprint}") String fingerprint
    ) {
        this.objectMapper = objectMapper;
        this.host = host;
        this.port = port;
        this.login = login;
        this.password = password;
        this.fingerprint = fingerprint;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        SSLContext sslContext = TransportUtils
                .sslContextFromCaFingerprint(fingerprint);

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(login, password)
        );

        RestClient restClient = RestClient
                .builder(new HttpHost(host, port, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
        return new ElasticsearchClient(transport);
    }


}
