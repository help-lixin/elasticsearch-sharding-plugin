### 1. 项目背景
ElasticSearch是一个分布式可弹性伸缩的全文检索系统,内部会对index进行分片(一个index一个文件),但是,在Saas环境下,要求各租户之间的数据是完全隔离,且方便迁移,所以,该组件的功能是对index name进行扩展(比如:order_00007)  

### 2. 集成方法
1) 添加依赖
```
<dependency>
    <groupId>help.lixin</groupId>
    <artifactId>elasticsearch-sharding-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
2) 自行实现:IndexCoordinateService接口,扔给Spring容器即可
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
