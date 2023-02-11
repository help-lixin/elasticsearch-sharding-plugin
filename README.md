### 1. 项目背景
ElasticSearch是一个分布式可弹性伸缩的全文检索系统,内部会对index进行分片,但是,在Saas环境下,要求各租户之间的数据是隔离的,所以,该组件的主要功能是:创建index是为index配置租户信息(比如:order_0007) 

### 2. 集成方法
1) 添加依赖
```
<dependency>
    <groupId>help.lixin</groupId>
    <artifactId>elasticsearch-sharding-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
2) 自行实现:IndexCoordinateService接口,扔给Spring容器即可(为每个索引添加租户后缀)
```
// 这是一个测试案例
public class TenantIndexCoordinateService implements IndexCoordinateService {

    public static final String UNDER_LINE = "_";

    @Override
    public String decorateForName(String originalName) {
        String indexName = originalName;
        String tenantCode = TenantContext.getTenantCode();
        if (null != tenantCode && !originalName.endsWith(tenantCode.toLowerCase())) {
            indexName = originalName + UNDER_LINE + tenantCode.toLowerCase();
        }
        return indexName;
    }
}
```
