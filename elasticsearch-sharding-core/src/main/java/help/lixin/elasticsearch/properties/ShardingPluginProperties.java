package help.lixin.elasticsearch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.elasticsearch.sharding.plugin")
public class ShardingPluginProperties {
    // 定义要扫描的包路径
    private String scanPackage = "help.lixin";

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    @Override
    public String toString() {
        return "ShardingPluginProperties{" +
                "scanPackage='" + scanPackage + '\'' +
                '}';
    }
}
