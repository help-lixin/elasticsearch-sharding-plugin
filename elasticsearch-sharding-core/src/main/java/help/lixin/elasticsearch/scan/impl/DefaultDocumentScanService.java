package help.lixin.elasticsearch.scan.impl;

import help.lixin.elasticsearch.properties.ShardingPluginProperties;
import help.lixin.elasticsearch.scan.IDocumentScanService;
import help.lixin.elasticsearch.store.IDocumentStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.util.ClassUtils;


/**
 * 扫描@Document注解
 */
public class DefaultDocumentScanService implements IDocumentScanService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private ShardingPluginProperties shardingPluginProperties;

    private IDocumentStoreService documentStoreService;

    public DefaultDocumentScanService(ShardingPluginProperties shardingPluginProperties, IDocumentStoreService documentStoreService) {
        this.shardingPluginProperties = shardingPluginProperties;
        this.documentStoreService = documentStoreService;
    }

    @Override
    public void scan() {
        String scanPackage = shardingPluginProperties.getScanPackage();
        GenericApplicationContext context = new GenericApplicationContext();
        // 要禁用默认的扫描,否则会扫描到@Component
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context, false);
        // 指定要扫描的注解
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));
        scanner.scan(scanPackage);
        context.refresh();
        String[] documents = context.getBeanNamesForAnnotation(Document.class);
        for (String documentItem : documents) {
            BeanDefinition beanDefinition = context.getBeanDefinition(documentItem);
            String clazz = beanDefinition.getBeanClassName();
            try {
                Class<?> documentClazz = ClassUtils.forName(clazz, Thread.currentThread().getContextClassLoader());
                Document annotation = documentClazz.getAnnotation(Document.class);
                String indexName = annotation.indexName();
                documentStoreService.add(indexName, documentClazz);
            } catch (ClassNotFoundException ignore) {
                logger.warn("ignore @Document:[{}],error:[{}]", clazz, ignore);
            }
        }
    }
}
