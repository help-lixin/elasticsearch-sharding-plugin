package help.lixin.elasticsearch.index;

import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

public interface IndexCoordinateService {

    /**
     * 是否忽略索引名称修饰的标识，用于一些特殊场景下创建与租户无关的索引
     */
    public ThreadLocal<Boolean> IGNORE_DECORATE_INDEX_MSK = new ThreadLocal<>();

    default public void setIgnoreDecorateIndexMsk() {
        IGNORE_DECORATE_INDEX_MSK.set(true);
    }

    default public void removeIgnoreDecorateIndexMsk() {
        IGNORE_DECORATE_INDEX_MSK.remove();
    }

    default IndexCoordinates decorateIndexCoordinates(IndexCoordinates indexCoordinates) {
        if (IGNORE_DECORATE_INDEX_MSK.get() != null) {
            return indexCoordinates;
        }
        String[] indexNames = indexCoordinates.getIndexNames();
        for (int i = 0; i < indexNames.length; ++i) {
            indexNames[i] = decorateForName(indexNames[i]);
        }
        return IndexCoordinates.of(indexNames);
    }

    default String decorateForName(String originalName) {
        String indexName = originalName;
        return indexName;
    }
}
