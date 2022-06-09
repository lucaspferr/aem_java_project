package com.mysite.core.service;

import com.day.commons.datasource.poolservice.DataSourcePool;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;

@Component(immediate = true, service = DatabaseService.class)
public class DatabaseServiceImpl implements DatabaseService{

    private final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    @Reference
    private DataSourcePool dataSourcePool;

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try{
            DataSource dataSource = (DataSource) dataSourcePool.getDataSource("testdb");
            connection = dataSource.getConnection();
            logger.debug("Connection achieved.");
        }catch (Exception e){logger.debug("Connection with the database not possible. Error message: "+e.getMessage());}
        return connection;
    }
}
