package help.lixin.elasticsearch.store;


/**
 * @Document存储服务
 */
public interface IDocumentStoreService {

    void add(String indexName, Class<?> clazz);

    Class<?> get(String indexName);
}
