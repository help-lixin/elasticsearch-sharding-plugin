package help.lixin.elasticsearch.support;


import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformationCreator;
import org.springframework.data.elasticsearch.repository.support.MappingElasticsearchEntityInformation;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

public class ExtElasticsearchEntityInformationCreatorImpl implements ElasticsearchEntityInformationCreator {

    private final MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext;

    public ExtElasticsearchEntityInformationCreatorImpl(MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext) {
        Assert.notNull(mappingContext, "MappingContext must not be null!");
        this.mappingContext = mappingContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> ElasticsearchEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        ElasticsearchPersistentEntity<T> persistentEntity = (ElasticsearchPersistentEntity<T>) mappingContext
                .getRequiredPersistentEntity(domainClass);
        Assert.notNull(persistentEntity, String.format("Unable to obtain mapping metadata for %s!", domainClass));
        Assert.notNull(persistentEntity.getIdProperty(), String.format("No id property found for %s!", domainClass));

        if (!isAutoCreateIndex()) {
            disabledCreateIndexAndMapping(persistentEntity);
        }
        return new MappingElasticsearchEntityInformation<>(persistentEntity);
    }

    protected boolean isAutoCreateIndex() {
        // 预留一个口,防止需要启动时创建索引.
        String isAutoCreateIndex = System.getenv("elasticsearch.auto.create.index.and.mapping");
        if (null == isAutoCreateIndex) {
            // 不设置的情况下,则代表进程启动时,不进行索引的自动创建.
            return false;
        } else {
            return Boolean.parseBoolean(isAutoCreateIndex);
        }
    }

    /**
     * 禁止启动时,自动创建索引.
     *
     * @param persistentEntity
     */
    protected void disabledCreateIndexAndMapping(ElasticsearchPersistentEntity persistentEntity) {
        try {
            Field createIndexAndMapping = persistentEntity.getClass().getDeclaredField("createIndexAndMapping");
            createIndexAndMapping.setAccessible(true);
            // 只能通过反射的方式,设置在启动时,不创建索引.
            createIndexAndMapping.set(persistentEntity, Boolean.FALSE.booleanValue());
            createIndexAndMapping.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        }
    }
}
