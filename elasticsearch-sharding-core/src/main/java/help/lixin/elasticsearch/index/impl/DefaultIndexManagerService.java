package help.lixin.elasticsearch.index.impl;

import help.lixin.elasticsearch.index.IndexManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.HashSet;
import java.util.Set;

public class DefaultIndexManagerService implements IndexManagerService {
    private Logger logger = LoggerFactory.getLogger(DefaultIndexManagerService.class);

    private ElasticsearchOperations elasticsearchOperations;

    private Object lock = new Object();

    private final Set<String> indexs = new HashSet<>();

    @Override
    public void setElasticsearchOperations(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public void createIndex(Class<?> clazz, IndexCoordinates index) {
        String indexName = index.getIndexName();
        if (!indexs.contains(indexName)) {
            synchronized (lock) {
                if (!indexs.contains(indexName)) { // 双重检测
                    logger.info("向ES创建索引:[{}]", indexName);
                    IndexOperations indexOperations = elasticsearchOperations.indexOps(index);
                    try {
                        // ES中判断索引是否存在
                        if (!indexOperations.exists()) {
                            //根据类注解获取setting配置
                            Document settings = indexOperations.createSettings(clazz);
                            // ES创建索引名称
                            indexOperations.create(settings);
                            // ES为索引创建mapping
                            indexOperations.putMapping(clazz);
                        }
                        // 如果索引在ES中已经存在,可能Cache里没有,这种情况也需要,配置成在ES中已经存在.
                        indexs.add(indexName);
                    } catch (Exception exception) {
                        logger.warn("Cannot create index: {}", exception.getMessage());
                    }
                }
            }
        }
    }
}
