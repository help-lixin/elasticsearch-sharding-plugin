package help.lixin.elasticsearch.core;

import help.lixin.elasticsearch.index.IndexCoordinateService;
import help.lixin.elasticsearch.index.IndexManagerService;
import help.lixin.elasticsearch.store.IDocumentStoreService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 功能一:<br/>
 * 1. 拦截所有请求. <br/>
 * 2. 获得租户code,比如:00007 <br/>
 * 3. 获取注解上:@Document(name="test")上信息(name) <br/>
 * 4. 拼接生成新的index名称,比如:name + tenant_code(test_00007)<br/>
 * 功能二:<br/>
 * 1. 在第一次保存,索引信息也是动态的
 */

public class ExtElasticsearchOperations extends ElasticsearchRestTemplate implements ApplicationContextAware {
    private IndexManagerService indexManagerService;
    private IDocumentStoreService documentStoreService;

    private IndexCoordinateService indexCoordinateService;

    // 通过SPI加载实现类,解决循环依赖问题.
    {
        ServiceLoader<IndexManagerService> load = ServiceLoader.load(IndexManagerService.class);
        Iterator<IndexManagerService> iterator = load.iterator();
        if (iterator.hasNext()) {
            IndexManagerService indexManagerService = iterator.next();
            indexManagerService.setElasticsearchOperations(this);
            this.indexManagerService = indexManagerService;
        }
    }

    public ExtElasticsearchOperations(RestHighLevelClient client) {
        super(client);
    }

    public ExtElasticsearchOperations(RestHighLevelClient client,
                                      //
                                      ElasticsearchConverter elasticsearchConverter,
                                      //
                                      IndexCoordinateService indexCoordinateService) {
        super(client, elasticsearchConverter);
        this.indexCoordinateService = indexCoordinateService;
    }

    /**
     * 单个保存切入点
     *
     * @param query
     * @param index
     * @return
     */
    @Override
    public String doIndex(IndexQuery query, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.doIndex(query, indexCoordinateService.decorateIndexCoordinates(index));
    }

    /**
     * 批量保存切入点
     *
     * @param queries
     * @param index
     * @return
     */
    @Override
    public List<IndexedObjectInformation> bulkIndex(List<IndexQuery> queries, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.bulkIndex(queries, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public UpdateResponse update(UpdateQuery query, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.update(query, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public void bulkUpdate(List<UpdateQuery> queries, IndexCoordinates index) {
        createMappingForIndex(index);
        super.bulkUpdate(queries, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public String delete(String id, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.delete(id, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public String delete(String id, String routing, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.delete(id, routing, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public void delete(Query query, Class<?> clazz, IndexCoordinates index) {
        createMappingForIndex(index);
        super.delete(query, clazz, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public <T> T get(String id, Class<T> clazz, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.get(id, clazz, indexCoordinateService.decorateIndexCoordinates(index));
    }

    public long count(Query query, @Nullable Class<?> clazz, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.count(query, clazz, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public List<SearchHits<?>> multiSearch(List<? extends Query> queries, List<Class<?>> classes, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.multiSearch(queries, classes, indexCoordinateService.decorateIndexCoordinates(index));
    }

    @Override
    public <T> SearchHits<T> search(Query query, Class<T> clazz, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.search(query, clazz, indexCoordinateService.decorateIndexCoordinates(index));
    }

    // 针对保存方法,进行Mapping创建
    @Override
    public <T> T save(T entity, IndexCoordinates index) {
        createMappingForIndex(index);
        return super.save(entity, index);
    }

    // 针对保存方法,进行Mapping创建
    @Override
    public <T> Iterable<T> save(T... entities) {
        // 仅取第一个实体信息.
        Streamable.of(entities).stream().map(i -> i.getClass()).findFirst().ifPresent(clazz -> {
            IndexCoordinates tmpIndex = indexCoordinateService.decorateIndexCoordinates(getIndexCoordinatesFor(clazz));
            indexManagerService.createIndex(clazz, tmpIndex);
        });
        return super.save(entities);
    }

    // 针对保存方法,进行Mapping创建
    @Override
    public <T> Iterable<T> save(Iterable<T> entities, IndexCoordinates index) {
        Streamable.of(entities).stream().map(i -> i.getClass()).findFirst().ifPresent(clazz -> {
            createMappingForIndex(index);
        });
        return super.save(entities, index);
    }

    protected void createMappingForIndex(IndexCoordinates index) {
        String indexName = index.getIndexName();
        Class<?> entity = documentStoreService.get(indexName);
        if (null != entity) {
            IndexCoordinates tmpIndex = indexCoordinateService.decorateIndexCoordinates(index);
            indexManagerService.createIndex(entity, tmpIndex);
        }
    }

    @Override
    public IndexOperations indexOps(Class<?> clazz) {
        return new ExtIndexOperations(this, clazz, indexCoordinateService);
    }

    @Override
    public IndexOperations indexOps(IndexCoordinates index) {
        return new ExtIndexOperations(this, index, indexCoordinateService);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.documentStoreService = applicationContext.getBean(IDocumentStoreService.class);
    }
}
