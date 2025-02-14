package jsbh.Jusangbokhap.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Configuration
public class ElasticSearchConfig {

    private final String host;
    private final int port;
    private final String login;
    private final String password;
    private final String certificateBase64;
    private final ObjectMapper objectMapper;

    private ElasticsearchClient esClient;

    public ElasticSearchConfig(
            ObjectMapper objectMapper,
            @Value("${elasticsearch.custom.host}") String host,
            @Value("${elasticsearch.custom.port}") int port,
            @Value("${elasticsearch.custom.username}") String login,
            @Value("${elasticsearch.custom.password}") String password,
            @Value("${elasticsearch.custom.certificate}") String certificateBase64
    ) {
        this.objectMapper = objectMapper;
        this.host = host;
        this.port = port;
        this.login = login;
        this.password = password;
        this.certificateBase64 = certificateBase64;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() throws Exception {

        SSLContext sslContext = getSSLContext();

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

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));
        return new ElasticsearchClient(transport);
    }

    private SSLContext getSSLContext() throws Exception {
        byte[] decodedCertificate = Base64.getMimeDecoder().decode(certificateBase64);

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate ca;
        try (InputStream certificateInputStream = new ByteArrayInputStream(decodedCertificate)) {
            ca = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);
        }

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
        return sslContext;
    }

    @PreDestroy
    public void closeClient() {
        if (esClient != null) {
            try {
                esClient.close();
                System.out.println("Elasticsearch RestClient closed successfully.");
            } catch (IOException e) {
                System.err.println("Failed to close RestClient: " + e.getMessage());
            }
        }
    }


}
