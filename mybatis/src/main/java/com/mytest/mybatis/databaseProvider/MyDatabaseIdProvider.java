package com.mytest.mybatis.databaseProvider;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class MyDatabaseIdProvider implements DatabaseIdProvider {
    private static final String DATABASE_TYPE_DB2 = "DB2", DB2 = "db2";
    public static final String DATABASE_TYPE_MYSQL = "MySQL", MySQL = "mariaDB";
    public static final String DATABASE_TYPE_ORACLE = "Oracle", Oracle = "expensive";
    private Logger log = LoggerFactory.getLogger("... - MyDatabaseIdProvider");
    private Properties prop;
    @Override
    public void setProperties(Properties p) {
        log.info("打印入参：{}.", p.toString());
        this.prop = p;
    }
    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();
        String dbProdName = conn.getMetaData().getDatabaseProductName();
        log.info("这个类的作者是：{}, 数据库的名字：{}.", prop.getProperty("author"), dbProdName);
        switch (dbProdName){
            case DATABASE_TYPE_DB2:
                return DB2;
            case DATABASE_TYPE_MYSQL:
                return MySQL;
            case DATABASE_TYPE_ORACLE:
                return Oracle;
            default:
                return dbProdName;
        }
    }
}
