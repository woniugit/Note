package com.mstar.qs.support.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mstar.qs.common.jdbc.DatabaseConnectiveInfo;
import com.mstar.qs.common.jdbc.JDBCManager;
import com.mstar.qs.common.jdbc.JDBCManager.DBProvider;
import com.mstar.qs.support.service.IdeaGenatorService.AlertData;

public class MSSQLTest {

    public static void main(String[] args) {
        MSSQLTest ts = new MSSQLTest();
        List<AlertData> tsList = ts.getCoverageInitiationsData("2017-06-15");
        System.out.println(tsList.size());
    }

    public List<AlertData> getCoverageInitiationsData(String startDate) {
        Connection conn = null;
        CallableStatement callableStatement = null;
        List<AlertData> beanList = new ArrayList<AlertData>();
        try {
            DatabaseConnectiveInfo dbInfo = new DatabaseConnectiveInfo();
            dbInfo.setHost("eqdb.morningstar.com");
            dbInfo.setSchema("SelectEquity");
            dbInfo.setUser("SelectEquity");
            dbInfo.setPort(1433);
            dbInfo.setPwd("4SEE0Page!");
            dbInfo.setDbType(DBProvider.MSSQL);
            conn = JDBCManager.obtainConnection(dbInfo);
            String proc = "{call GetAlertDataInAPeriod(?)}";
            callableStatement = conn.prepareCall(proc);
            callableStatement.setString(1, startDate);
            ResultSet rs = callableStatement.executeQuery();
            while (rs.next()) {
                String alertId = rs.getString(1);
                if (null != alertId && alertId.equals("S2501")) {
                    AlertData data = new AlertData();
                    data.setAlertId(alertId);
                    data.setShareClassId(rs.getString(2));
                    data.setCompanyId(rs.getString(3));
                    data.setSecId(rs.getString(5));
                    data.setTicker(rs.getString(6));
                    data.setCountryId(rs.getString(7));
                    data.setAlertDate(rs.getString(8));
                    data.setNewValue(rs.getString(10));
                    beanList.add(data);
                }
            }
            return beanList;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    callableStatement.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return beanList;
    }
}
