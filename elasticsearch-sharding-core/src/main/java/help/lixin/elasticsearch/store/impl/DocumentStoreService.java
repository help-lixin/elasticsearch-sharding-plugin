package help.lixin.elasticsearch.store.impl;

import help.lixin.elasticsearch.store.IDocumentStoreService;

import java.util.concurrent.ConcurrentHashMap;

public class DocumentStoreService implements IDocumentStoreService {
    private final ConcurrentHashMap<String, Class<?>> cache = new ConcurrentHashMap<>();
    @Override
    public void add(String indexName, Class<?> clazz) {
        cache.putIfAbsent(indexName, clazz);
    }

    @Override
    public Class<?> get(String indexName) {
        return cache.get(indexName);
    }
}
