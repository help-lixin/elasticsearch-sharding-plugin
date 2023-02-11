package help.lixin.elasticsearch;

import help.lixin.elasticsearch.config.ElasticsearchPluginConfig;
import help.lixin.elasticsearch.constants.Constants;
import help.lixin.elasticsearch.properties.ShardingPluginProperties;
import help.lixin.elasticsearch.support.ExtElasticsearchRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * 在插件里启用注解,@EnableElasticsearchRepositories,而不需要在开发的应用里指定这个注解,尽可能的透明化掉.
 */
@Configuration(proxyBeanMethods = false)
@Import(ElasticsearchPluginConfig.class)
@EnableElasticsearchRepositories(basePackages = "help.lixin", repositoryFactoryBeanClass = ExtElasticsearchRepositoryFactoryBean.class)
@ConditionalOnProperty(name = Constants.PLUGIN_ENABLED, havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ShardingPluginProperties.class)
public class ElasticsearchAutoConfiguration {

}
