package help.lixin.elasticsearch.env;

import help.lixin.elasticsearch.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类的主要目的是禁用Spring Data ElasticSearch自动启用注解@EnableElasticsearchRepositories.期望一个jar包组件加进来之后,连配置都不需要去做,尽可能的透明掉.
 */
public class ElasticsearchEnvironmentConfigService implements EnvironmentPostProcessor {
    private Logger logger = LoggerFactory.getLogger(ElasticsearchEnvironmentConfigService.class);
    private final String name = "_framework_elasticsearch_repositories";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 判断有没有开启插件
        Boolean pluginEnabled = environment.getProperty(Constants.PLUGIN_ENABLED, Boolean.class, Constants.PLUGIN_ENABLED_VALUE);
        if (pluginEnabled.booleanValue() == true) {
            Map<String, Object> frameworkEnvironment = new HashMap<>();
            // 添加默认的,允许对bean进行重写.
            frameworkEnvironment.put(Constants.REPOSITORIES_ENABLED, Constants.REPOSITORIES_ENABLED_VALUE);
            PropertySource<Map<String, Object>> propertySource = new MapPropertySource(name, frameworkEnvironment);
            environment.getPropertySources().addFirst(propertySource);
            logger.debug("ElasticsearchEnvironmentConfigService setting: spring.data.elasticsearch.repositories.enabled=false");
        }
    }
}
