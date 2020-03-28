package com.lyl.igreport.service.impl;

import com.lyl.igreport.dto.QueryPrestoDto;
import com.lyl.igreport.enums.DataSourceEnum;
import com.lyl.igreport.service.PrestoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static java.lang.String.format;

/**
 * @author: Liuyanling
 * @date: 2020/1/16
 * @description:
 */
@Service("prestoService")
public class PrestoServiceImpl implements PrestoService {
    private static final Logger LOGGER = LogManager.getLogger(PrestoServiceImpl.class);
    @Value("${presto.url}")
    private String prestoUrl;

    @Value("${presto.tidb.schema}")
    private String tidbSchema;
    @Value("${presto.tidb.username}")
    private String tidbUsername;


    @Override
    public List<HashMap> queryDataByPresto(String sql) throws Exception {
        List<HashMap> list = new ArrayList();

        try {
            Connection connection = getConnenction();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int col = rsmd.getColumnCount();
            List<String> columnNameList = new ArrayList<String>();
            for (int i = 1; i <= col; i++) {
                String columnName = rsmd.getColumnLabel(i);
                columnNameList.add(columnName);
            }

            while (rs.next()) {
                HashMap columnValueMap = new HashMap();
                for (int i = 1; i <= col; i++) {
                    String key = columnNameList.get(i - 1);

                    try {
                        if (Types.DOUBLE == rsmd.getColumnType(i)) {
                            columnValueMap.put(key, rs.getDouble(key));
                        } else if (Types.DECIMAL == rsmd.getColumnType(i)) {
                            columnValueMap.put(key, rs.getBigDecimal(key).doubleValue());
                        } else if (Types.INTEGER == rsmd.getColumnType(i)) {
                            columnValueMap.put(key, rs.getInt(key));
                        } else if (Types.DATE == rsmd.getColumnType(i)) {
                            columnValueMap.put(key, rs.getDate(key));
                        } else if (Types.TIMESTAMP == rsmd.getColumnType(i)) {
                            columnValueMap.put(key, rs.getTimestamp(key));
                        } else {
                            columnValueMap.put(key, rs.getString(key));
                        }
                    }catch (Exception e){
                        columnValueMap.put(key, rs.getString(key));
                    }


                }
                list.add(columnValueMap);

            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            LOGGER.error("commonReportWithPresto异常 sql={}", sql, e);
            throw new Exception(e);

        }

        return list;
    }

    public Connection getConnenction() {
        Connection connection = null;
        //presto默认用tidb的链接
        try {
            Class.forName("com.facebook.presto.jdbc.PrestoDriver");
//            if (DataSourceEnum.getEnumByCode(dataSource).equals(DataSourceEnum.hive)) {
//                String url = format("jdbc:presto://%s/%s/%s", prestoUrl, DataSourceEnum.hive.getCode(), hiveSchema);
//                Properties properties = new Properties();
//                properties.setProperty("user", hiveUsername);
//                connection = DriverManager.getConnection(url, properties);
//            } else if (DataSourceEnum.getEnumByCode(dataSource).equals(DataSourceEnum.tidb)) {
            String url = format("jdbc:presto://%s/%s/%s", prestoUrl, DataSourceEnum.tidb.getCode(), tidbSchema);
            Properties properties = new Properties();
            properties.setProperty("user", tidbUsername);

            connection = DriverManager.getConnection(url, properties);

        } catch (Exception e) {
            LOGGER.error("commonReportWithPresto getConnenction失败 ",  e);
        }
        LOGGER.info("commonReportWithPresto getConnenction成功");
        return connection;
    }
}
