package com.lyl.igreport.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by liuyanling on 2020/2/6
 */
@Configuration
@MapperScan(basePackages = TidbDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "tidbSqlSessionFactory")

public class TidbDataSourceConfig {
    // 精确到 cluster 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.lyl.igreport.dao.tidb";
    static final String MAPPER_LOCATION = "classpath:mybatis-mapper/tidb/*.xml";

    @Value("${tidb.datasource.url}")
    private String url;

    @Value("${tidb.datasource.username}")
    private String user;

    @Value("${tidb.datasource.password}")
    private String password;

    @Value("${tidb.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "tidbDataSource")
    public DataSource clusterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "tidbTransactionManager")
    public DataSourceTransactionManager clusterTransactionManager() {
        return new DataSourceTransactionManager(clusterDataSource());
    }

    @Bean(name = "tidbSqlSessionFactory")
    public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("tidbDataSource") DataSource clusterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(TidbDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
