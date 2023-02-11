package help.lixin.elasticsearch.example.ctx;

public class TenantContext {
    private static ThreadLocal<String> tenantCodeHolder = new ThreadLocal();

    public TenantContext() {
    }

    public static final String getTenantCode() {
        return (String)tenantCodeHolder.get();
    }

    public static final void setTenantCode(String tenantCode) {
        tenantCodeHolder.set(tenantCode);
    }

    public static final void removeTenantCode() {
        tenantCodeHolder.remove();
    }
}
