package help.lixin.elasticsearch.config;

import help.lixin.elasticsearch.core.ExtElasticsearchOperations;
import help.lixin.elasticsearch.index.IndexCoordinateService;
import help.lixin.elasticsearch.properties.ShardingPluginProperties;
import help.lixin.elasticsearch.scan.IDocumentScanService;
import help.lixin.elasticsearch.scan.impl.DefaultDocumentScanService;
import help.lixin.elasticsearch.store.IDocumentStoreService;
import help.lixin.elasticsearch.store.impl.DocumentStoreService;
import help.lixin.elasticsearch.index.impl.IndexCoordinateProcessServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;

@Configuration
public class ElasticsearchPluginConfig {

    @Bean(value = {"elasticsearchOperations", "elasticsearchTemplate", "elasticsearchRestTemplate"})
    @ConditionalOnMissingBean
    public ElasticsearchOperations extElasticsearchOperations(RestHighLevelClient client,
                                                              //
                                                              ElasticsearchConverter elasticsearchConverter,
                                                              //
                                                              @Autowired(required = true) IndexCoordinateService indexCoordinateService) {
        return new ExtElasticsearchOperations(client, elasticsearchConverter, indexCoordinateService);
    }

    // 允许开发自己定制实现
    @Bean
    @ConditionalOnMissingBean
    public IndexCoordinateService indexCoordinateService() {
        return new IndexCoordinateProcessServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public IDocumentStoreService documentStoreService() {
        return new DocumentStoreService();
    }

    @Bean(initMethod = "scan")
    @ConditionalOnMissingBean
    public IDocumentScanService documentScanService(ShardingPluginProperties shardingPluginProperties, IDocumentStoreService documentStoreService) {
        return new DefaultDocumentScanService(shardingPluginProperties, documentStoreService);
    }
}
