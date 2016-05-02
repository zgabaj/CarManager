/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.backend;

import cz.muni.fi.pv168.carmanager.databasetools.DatabaseTools;
import cz.muni.fi.pv168.carmanager.databasetools.IllegalEntityException;
import cz.muni.fi.pv168.carmanager.databasetools.IllegalLeaseException;
import cz.muni.fi.pv168.carmanager.databasetools.ServiceFailureException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;


public class LeaseManagerImpl implements LeaseManager {

    private static final Logger logger = Logger.getLogger(
            LeaseManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException(ResourceBundle.getBundle("strings")
                    .getString("dataSourceIsNotset"));
        }
    }

    @Override
    public void createNewLease(Lease lease) throws IllegalEntityException, ServiceFailureException {
        checkDataSource();
        checkLease(lease);
        if (lease.getId() != null) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
                    .getString("errorLeaseIdAlreadySet"));
        }
        if (lease.getCarId() == null) {
            throw new IllegalLeaseException(ResourceBundle.getBundle("strings")
                    .getString("carIdNull"));
        }
        checkCar(lease.getCarId());
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            checkCarAvaibility(connection, lease.getCarId(), lease.getStartDate(), lease.getEndDate(), false);
            st = connection.prepareStatement(
                    "INSERT INTO LEASE (customerfullname,startdate,enddate,carid) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, lease.getCustomerFullName());
            st.setDate(2, toSqlDate(lease.getStartDate()));
            st.setDate(3, toSqlDate(lease.getEndDate()));
            st.setLong(4, lease.getCarId());

            int count = st.executeUpdate();
            DatabaseTools.checkNumberOfUpdates(count, Lease.class, true);

            Long id = DatabaseTools.getIdFromResultSet(st.getGeneratedKeys());
            lease.setId(id);
            connection.commit();

        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                    .getString("addLeaseError");
            logger.log(Level.SEVERE, msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.doRollback(connection);
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }

    }

    @Override
    public void deleteLease(Lease lease) throws IllegalLeaseException, IllegalEntityException, ServiceFailureException {

        checkDataSource();
        if (lease == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("leaseIsNull"));
        }
        if (lease.getId() == null) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
                    .getString("leaseIdIsNull"));
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            st = connection.prepareStatement(
                    "DELETE FROM LEASE WHERE id = ?");
            st.setLong(1, lease.getId());

            int count = st.executeUpdate();
            DatabaseTools.checkNumberOfUpdates(count, Lease.class, false);
            connection.commit();

        } catch (SQLException ex) {

            String msg = ResourceBundle.getBundle("strings")
                    .getString("deleteLeaseError");
            logger.log(Level.SEVERE, msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.doRollback(connection);
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }

    }

    @Override
    public void updateLease(Lease lease) throws IllegalLeaseException, IllegalEntityException, ServiceFailureException {
        checkDataSource();
        checkLease(lease);
        if (lease.getId() == null) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
                    .getString("leaseIdIsNull"));
        }
        checkCar(lease.getCarId());
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            checkCarAvaibility(connection, lease.getCarId(), lease.getStartDate(), lease.getEndDate(), true);
            st = connection.prepareStatement(
                    "UPDATE LEASE SET customerfullname = ?, startdate = ?, "
                    + "enddate = ?, carid = ? WHERE id = ?");
            st.setString(1, lease.getCustomerFullName());
            st.setDate(2, toSqlDate(lease.getStartDate()));
            st.setDate(3, toSqlDate(lease.getEndDate()));
            st.setLong(4, lease.getCarId());
            st.setLong(5, lease.getId());

            int count = st.executeUpdate();
            DatabaseTools.checkNumberOfUpdates(count, Lease.class, true);

            connection.commit();

        } catch (SQLException ex) {

            String msg = ResourceBundle.getBundle("strings")
                    .getString("updateLeaseError");
            logger.log(Level.SEVERE, msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.doRollback(connection);
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public List<Lease> listAllLeases() throws ServiceFailureException {
        checkDataSource();
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,customerfullname,startdate,enddate,carid FROM LEASE");
            ResultSet rs = st.executeQuery();
            return getLeasesFromResultSet(rs);
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                    .getString("listLeasesError");
            logger.log(Level.SEVERE, msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public List<Lease> listAllLeasesForCar(Car car) throws IllegalEntityException,
            ServiceFailureException {

        checkDataSource();
        if (car == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("carIsNull"));
        }
        if (car.getId() == null) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
                    .getString("carIdNull"));
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT customerfullname,startdate,enddate,carid FROM LEASE WHERE carid = ?");
            st.setLong(1, car.getId());
            ResultSet rs = st.executeQuery();
            return getLeasesFromResultSet(rs);
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                    .getString("listLeasesError");
            logger.log(Level.SEVERE, msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public Lease getLeaseById(Long id) throws ServiceFailureException {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("leaseIdIsNull"));
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,customerfullname,startdate,enddate,carid FROM LEASE WHERE id = ?");
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            return getLeaseFromResultSet(rs);
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                    .getString("getLeaseError");
            logger.log(Level.SEVERE, msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public List<Lease> listAllLeasesBetweenDates(LocalDate startDate, LocalDate endDate)
            throws ServiceFailureException {

        checkDataSource();
        checkDate(startDate, endDate);

        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,customerfullname,startdate,enddate,carid FROM LEASE "
                    + "WHERE startdate >= ? AND enddate <= ?");

            st.setDate(1, toSqlDate(startDate));
            st.setDate(2, toSqlDate(endDate));
            ResultSet rs = st.executeQuery();
            return getLeasesFromResultSet(rs);
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                    .getString("errorFindLease");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public List<Car> listAllFreeCarsBetweenDates(LocalDate startDate, LocalDate endDate)
            throws ServiceFailureException {

        checkDataSource();
        checkDate(startDate, endDate);
        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT CAR.id,CAR.brand,CAR.licenceplate "
                    + "FROM CAR LEFT OUTER JOIN LEASE ON CAR.id = LEASE.carid "
                    + "WHERE LEASE.id IS NULL OR ((startdate NOT BETWEEN ? AND ?) AND "
                    + "(enddate NOT BETWEEN ? AND ?) AND NOT(startdate <= ? AND enddate >= ?))");

            st.setDate(1, toSqlDate(startDate));
            st.setDate(2, toSqlDate(endDate));
            st.setDate(3, toSqlDate(startDate));
            st.setDate(4, toSqlDate(endDate));
            st.setDate(5, toSqlDate(startDate));
            st.setDate(6, toSqlDate(endDate));

            ResultSet rs = st.executeQuery();
            return DatabaseTools.getCarsFromResultSet(rs);
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                    .getString("errorFindLease");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg , ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    static Lease getLeaseFromResultSet(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Lease lease = rowToLease(rs);
            if (rs.next()) {
                throw new ServiceFailureException(ResourceBundle.getBundle("strings")
                    .getString("leasesHaveSameId"));
                
            }
            return lease;
        } else {
            return null;
        }
    }

    static List<Lease> getLeasesFromResultSet(ResultSet rs) throws SQLException {
        List<Lease> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowToLease(rs));
        }
        return result;
    }

    private static Lease rowToLease(ResultSet rs) throws SQLException {
        Lease lease = new Lease();
        lease.setId(rs.getLong("id"));
        lease.setCustomerFullName(rs.getString("customerFullName"));
        lease.setStartDate(toLocalDate(rs.getDate("startDate")));
        lease.setEndDate(toLocalDate(rs.getDate("endDate")));
        lease.setCarId(rs.getLong("carId"));

        return lease;
    }

    private void checkLease(Lease lease) throws IllegalLeaseException {
        if (lease == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("leaseIsNull"));
        }
        if (lease.getCustomerFullName() == null) {
            throw new IllegalLeaseException(ResourceBundle.getBundle("strings")
                    .getString("nullName"));
        }
        if (lease.getStartDate() == null) {
            throw new IllegalLeaseException(ResourceBundle.getBundle("strings")
                    .getString("nullStartDate"));
        }
        if (lease.getEndDate() == null) {
            throw new IllegalLeaseException(ResourceBundle.getBundle("strings")
                    .getString("nullEndDate"));
        }
        if (lease.getStartDate().isAfter(lease.getEndDate())) {
            
            throw new IllegalLeaseException(ResourceBundle.getBundle("strings")
                    .getString("endDateBeforStartDate"));
        }
    }

    private void checkCar(Long carId) throws IllegalLeaseException {

        CarManagerImpl carManagerImpl = new CarManagerImpl(dataSource);
        Car car = new Car();
        car = carManagerImpl.getCarById(carId);
        if (car == null) {
            throw new IllegalLeaseException(ResourceBundle.getBundle("strings")
                    .getString("notExitCar"));
        }
    }

    private static void checkCarAvaibility(Connection connection,
            Long carId, LocalDate startDate, LocalDate endDate, boolean update) throws
            IllegalLeaseException, SQLException {

        PreparedStatement st = null;
        try {
            if (!update) {
                st = connection.prepareStatement(
                        "SELECT CAR.id FROM LEASE INNER JOIN CAR ON LEASE.carid = CAR.id "
                        + "WHERE (startdate BETWEEN ? AND ?) OR (enddate BETWEEN ? AND ?) OR "
                        + "(startdate <= ? AND enddate >= ?)");
                st.setDate(1, toSqlDate(startDate));
                st.setDate(2, toSqlDate(endDate));
                st.setDate(3, toSqlDate(startDate));
                st.setDate(4, toSqlDate(endDate));
                st.setDate(5, toSqlDate(startDate));
                st.setDate(6, toSqlDate(endDate));

            } else {
                st = connection.prepareStatement(
                        "SELECT CAR.id FROM LEASE INNER JOIN CAR ON LEASE.carid = CAR.id "
                        + "WHERE (startdate BETWEEN ? AND ? AND carid != ?) OR (enddate BETWEEN ? AND ? AND carid != ?) OR "
                        + "(startdate <= ? AND enddate >= ? AND carid != ?)");
                st.setDate(1, toSqlDate(startDate));
                st.setDate(2, toSqlDate(endDate));
                st.setLong(3, carId);
                st.setDate(4, toSqlDate(startDate));
                st.setDate(5, toSqlDate(endDate));
                st.setLong(6, carId);
                st.setDate(7, toSqlDate(startDate));
                st.setDate(8, toSqlDate(endDate));
                st.setLong(9, carId);
            }

            ResultSet rs = st.executeQuery();
            List<Long> ids = DatabaseTools.getIdsFromResultSet(rs);
            if (ids != null) {
                if (ids.contains(carId)) {
                    throw new IllegalLeaseException(ResourceBundle.getBundle("strings")
                    .getString("carNotAvailable"));
                }
            }
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                    .getString("failCheckingCar");
                        logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(null, st);
        }
    }

    private static Date toSqlDate(LocalDate localDate) {
        if (localDate != null) {
            return Date.valueOf(localDate);
        } else {
            return null;
        }
    }

    private static LocalDate toLocalDate(Date date) {
        if (date != null) {
            return date.toLocalDate();
        } else {
            return null;
        }
    }

    private void checkDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("nullStartDate"));
        }
        if (endDate == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("nullEndDate"));
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
                    .getString("endDateBeforStartDate"));
        }
    }
}
