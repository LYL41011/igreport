package com.lyl.igreport.config;

import org.springframework.beans.factory.annotation.Value;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * Created by liuyanling on 2020/2/6
 */
@Configuration
// 扫描 Mapper 接口并容器管理
@MapperScan(basePackages = IgReportDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "igReportSqlSessionFactory")

public class IgReportDataSourceConfig {
    // 精确到 igReport 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.lyl.igreport.dao.mysql";
    static final String MAPPER_LOCATION = "classpath:mybatis-mapper/mysql/*.xml";

    @Value("${igreport.datasource.url}")
    private String url;

    @Value("${igreport.datasource.username}")
    private String user;

    @Value("${igreport.datasource.password}")
    private String password;

    @Value("${igreport.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "igReportDataSource")
    @Primary
    public DataSource igReportDataSource() {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "igReportTransactionManager")
    @Primary
    public DataSourceTransactionManager igReportTransactionManager() {
        return new DataSourceTransactionManager(igReportDataSource());
    }

    @Bean(name = "igReportSqlSessionFactory")
    @Primary
    public SqlSessionFactory igReportSqlSessionFactory(@Qualifier("igReportDataSource") DataSource igReportDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(igReportDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(IgReportDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }
}
