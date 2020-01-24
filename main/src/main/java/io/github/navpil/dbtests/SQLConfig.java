package io.github.navpil.dbtests;

public enum SQLConfig {
    SQL_SERVER("jdbc:sqlserver://localhost;databaseName=%DBNAME%;integratedSecurity=true;",
            "",
            "",
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "org.hibernate.dialect.SQLServerDialect"),

    HSQLDB("jdbc:hsqldb:%DBNAME%",
            "su",
            "",
            "org.hsqldb.jdbcDriver",
            "org.hibernate.dialect.HSQLDialect");

    private final String url;
    public final String username;
    public final String password;
    public final String driver;
    public final String dialect;

    SQLConfig(String url, String username, String password, String driver, String jpaSqlDialect) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driver = driver;
        dialect = jpaSqlDialect;
    }

    public static SQLConfig getDefault() {
        return HSQLDB;
    }

    public String getUrl(String name) {
        return url.replace("%DBNAME%", name);
    }

    public String getUrl() {
        return getUrl("carrental");
    }
}
