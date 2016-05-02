/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.databasetools;

import cz.muni.fi.pv168.carmanager.backend.Car;
import cz.muni.fi.pv168.carmanager.backend.Brand;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;


public class DatabaseTools {
    
    private static final Logger logger = Logger.getLogger(
            DatabaseTools.class.getName());
    
    public static void checkNumberOfUpdates(int count,Object entity,boolean insert) 
            throws IllegalEntityException, ServiceFailureException{
        
        if(!insert && count == 0){
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
                    .getString("entityNotFound"));
        }
        if(count != 1){
            throw new ServiceFailureException(ResourceBundle.getBundle("strings")
                    .getString("errorAffectedRows"));
        }
    }
    
    public static Long getIdFromResultSet(ResultSet rs) throws SQLException{
        
        if(rs.getMetaData().getColumnCount() != 1){
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("resultSetMoreCol"));
        }
        if(rs.next()){
            Long result = rs.getLong(1);
            if(rs.next()){
                throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("resultSetMoreRows"));
            }
            return result;
        }else{
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("resultSetNoRows"));
        }        
    }
    
    public static List<Long> getIdsFromResultSet(ResultSet rs) throws SQLException{

        List<Long> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getLong(1));
            
        }
        return result;        
    }
    
    public static void doRollback(Connection connection) {
        
        if(connection != null){
            
            try{
                if(connection.getAutoCommit()){
                    throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("alreadyAutocmmit"));
                }
                connection.rollback();
            }catch(SQLException ex){
                logger.log(Level.SEVERE,"Error: error when doing rollback",ex);
            }            
        }        
    }
    
    public static void  closeConnectionAndStatements(Connection connection,Statement ...statements) {
        
        for(Statement st : statements){
            if(st != null){
                try{
                    st.close();
                }catch(SQLException ex){
                    logger.log(Level.SEVERE,"Error: error when closing statement:" + st.toString(),ex);
                }
            }
        }
        if(connection != null){
            try{
                connection.setAutoCommit(true);
            }catch(SQLException ex){
                logger.log(Level.SEVERE,"Error: error when switching auto commit on",ex);
            }
            try{
                connection.close();
            }catch(SQLException ex){
                logger.log(Level.SEVERE,"Error: error when closing connection",ex);
            }
        }
        
    }
    
    private static String[] readSqlStatements(URL url) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException(ResourceBundle.getBundle("strings")
                    .getString("cantRead") + url, ex);
        }
    }
    
    public static void executeSqlScript(DataSource ds, URL scriptUrl) throws SQLException {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            for (String sqlStatement : readSqlStatements(scriptUrl)) {
                if (!sqlStatement.trim().isEmpty()) {
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        } finally {
            closeConnectionAndStatements(conn);
        }
    }
    
    public static Car getCarFromResultSet(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getLong("id"));
        car.setBrand(Brand.valueOf(rs.getString("brand")));
        car.setLicencePlate(rs.getString("licencePlate"));
        return car;
    }

    public static List<Car> getCarsFromResultSet(ResultSet rs) throws SQLException {
        List<Car> Cars = new ArrayList<>();
        while (rs.next()) {
            Cars.add(getCarFromResultSet(rs));
        }
        return Cars;
    }
    
}
