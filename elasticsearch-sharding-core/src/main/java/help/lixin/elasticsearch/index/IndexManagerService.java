package help.lixin.elasticsearch.index;


import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

public interface IndexManagerService {

    void setElasticsearchOperations(ElasticsearchOperations elasticsearchOperations);

    void createIndex(Class<?> clazz, IndexCoordinates index);
}
