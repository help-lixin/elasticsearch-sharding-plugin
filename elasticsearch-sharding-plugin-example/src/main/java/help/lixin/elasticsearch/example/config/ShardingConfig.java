package help.lixin.elasticsearch.example.config;

import help.lixin.elasticsearch.example.index.TenantIndexCoordinateService;
import help.lixin.elasticsearch.index.IndexCoordinateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShardingConfig {
    @Bean
    public IndexCoordinateService indexCoordinateService() {
        return new TenantIndexCoordinateService();
    }
}
