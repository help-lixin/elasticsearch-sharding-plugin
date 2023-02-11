package help.lixin.elasticsearch.support;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 1. 配置:spring.data.elasticsearch.repositories.enabled=false,禁用:ElasticsearchRepositoriesAutoConfiguration. <br/>
 * 2. 注解指定,repositoryFactoryBeanClass属性(@EnableElasticsearchRepositories(basePackages = "com.gerp",repositoryFactoryBeanClass = GerpElasticsearchRepositoryFactoryBean.class)) <br/>
 */
public class ExtElasticsearchRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends RepositoryFactoryBeanSupport<T, S, ID> {
    @Nullable
    private ElasticsearchOperations operations;

    /**
     * Creates a new {@link ElasticsearchRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public ExtElasticsearchRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Configures the {@link ElasticsearchOperations} to be used to create Elasticsearch repositories.
     *
     * @param operations the operations to set
     */
    public void setElasticsearchOperations(ElasticsearchOperations operations) {
        Assert.notNull(operations, "ElasticsearchOperations must not be null!");
        setMappingContext(operations.getElasticsearchConverter().getMappingContext());
        this.operations = operations;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(operations, "ElasticsearchOperations must be configured!");
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        Assert.notNull(operations, "operations are not initialized");
        return new ExtElasticsearchRepositoryFactory(operations);
    }
}
