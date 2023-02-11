package help.lixin.elasticsearch.example.index;

import help.lixin.elasticsearch.example.ctx.TenantContext;
import help.lixin.elasticsearch.index.IndexCoordinateService;

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
