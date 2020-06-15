package com.shinemo.wangge.dal.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.shinemo.ds.ConfProxy;
import com.shinemo.ds.Database;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author htdong
 * @date 2020年5月27日 上午11:24:27
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DalConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer config = new MapperScannerConfigurer();
        config.setBasePackage("com.shinemo.wangge.dal.mapper");
        config.setSqlSessionFactoryBeanName("mybatisSqlSessionFactory");
        return config;
    }

    @Primary
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource(@Value("${jdbc.dataName}") String dataName) {
        Database database = ConfProxy.get(dataName);
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(database.getUser());
        dataSource.setPassword(database.getPasswd());
        dataSource.setUrl(database.getJdbcUrl());
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(50);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        return dataSource;
    }

    @Bean(name = "mybatisSqlSessionFactory")
    @DependsOn("dataSource")
    public SqlSessionFactoryBean mybatisSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
            throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:sqlmap/**/*.xml"));
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(
                new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean
    @DependsOn("mybatisSqlSessionFactory")
    public SqlSessionTemplate sqlSession(
            @Qualifier("mybatisSqlSessionFactory") SqlSessionFactoryBean mybatisSqlSessionFactory) throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(mybatisSqlSessionFactory.getObject());
        return sqlSessionTemplate;
    }

    @Bean
    @DependsOn("dataSource")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    @Bean
    public MapperScannerConfigurer slaveMapperScannerConfigurer() {
        MapperScannerConfigurer config = new MapperScannerConfigurer();
        config.setBasePackage("com.shinemo.wangge.dal.slave.mapper");
        config.setSqlSessionFactoryBeanName("slaveMybatisSqlSessionFactory");
        return config;
    }

    @Primary
    @Bean(name = "slaveDataSource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource slaveDataSource(@Value("${jdbc.slaveDataName}") String dataName) {
        Database database = ConfProxy.get(dataName);
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(database.getUser());
        dataSource.setPassword(database.getPasswd());
        dataSource.setUrl(database.getJdbcUrl());
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(50);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        return dataSource;
    }

    @Bean(name = "slaveMybatisSqlSessionFactory")
    @DependsOn("slaveDataSource")
    public SqlSessionFactoryBean slaveMybatisSqlSessionFactory(@Qualifier("slaveDataSource") DataSource dataSource)
            throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:sqlmap/slave/**/*.xml"));
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(
                new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        return sqlSessionFactoryBean;
    }

    @Bean
    @DependsOn("slaveMybatisSqlSessionFactory")
    public SqlSessionTemplate slaveSqlSession(@Qualifier("slaveMybatisSqlSessionFactory") SqlSessionFactoryBean factory)
            throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(factory.getObject());
        return sqlSessionTemplate;
    }

    @Bean
    @DependsOn("slaveDataSource")
    public DataSourceTransactionManager slaveDataSourceTransactionManager(
            @Qualifier("slaveDataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }
}