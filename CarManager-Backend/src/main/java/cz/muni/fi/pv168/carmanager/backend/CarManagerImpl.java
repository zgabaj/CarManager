package cz.muni.fi.pv168.carmanager.backend;

import cz.muni.fi.pv168.carmanager.databasetools.DatabaseTools;
import cz.muni.fi.pv168.carmanager.databasetools.IllegalEntityException;
import cz.muni.fi.pv168.carmanager.databasetools.ServiceFailureException;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;


public class CarManagerImpl implements CarManager {

    private static final Logger logger = Logger.getLogger(
            CarManagerImpl.class.getName());

    private final DataSource dataSource;

    public CarManagerImpl(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
            .getString("dataSourceIsNotset"));
        }
        this.dataSource = dataSource;
    }

    
    @Override
    public void createCar(Car car) throws ServiceFailureException {

        checkCar(car);
        if (car.getId() != null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
            .getString("carIdAlreadySet"));
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            st = connection.prepareStatement(
                    "INSERT INTO CAR (brand,licencePlate) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, car.getBrand().name());
            st.setString(2, car.getLicencePlate());

            DatabaseTools.checkNumberOfUpdates(st.executeUpdate(), st, true);
            Long id = DatabaseTools.getIdFromResultSet(st.getGeneratedKeys());

            car.setId(id);
            connection.commit();
        } catch (SQLException ex) {
            String msg= ResourceBundle.getBundle("strings")
                        .getString("addCarError");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.doRollback(connection);
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public void updateCar(Car car) {
        checkCar(car);
        if (car.getId() == null) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
            .getString("carIdNull"));
        }
        Connection connection = null;
        PreparedStatement st = null;

        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            st = connection.prepareStatement(
                    "UPDATE Car SET brand = ?, licencePlate = ? WHERE id = ?");
            st.setString(1, car.getBrand().name());
            st.setString(2, car.getLicencePlate());
            st.setLong(3, car.getId());

            DatabaseTools.checkNumberOfUpdates(st.executeUpdate(), car, true);
            connection.commit();
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                        .getString("updateCarError");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.doRollback(connection);
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public void deleteCar(Car car) {
        checkCar(car);
        if (car.getId() == null) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
            .getString("carIdNull"));
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "DELETE FROM car WHERE id = ?");
            connection.setAutoCommit(false);
            st.setLong(1, car.getId());

            DatabaseTools.checkNumberOfUpdates(st.executeUpdate(), car, false);
            connection.commit();
        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                        .getString("deleteCarError");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.doRollback(connection);
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }

    }

    @Override
    public Car getCarById(Long id) throws ServiceFailureException {

        if (id == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
            .getString("carIdNull"));
        }
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,brand,licencePlate FROM Car WHERE id = ?");

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Car car = DatabaseTools.getCarFromResultSet(rs);
                if (rs.next()) {
                    throw new ServiceFailureException(ResourceBundle.getBundle("strings")
                    .getString("carErrorMoreEntities"));
                }
                return car;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                        .getString("getCarError");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public List<Car> listAllCarsByBrand(Brand brand) throws ServiceFailureException {

        Connection connection = null;
        PreparedStatement st = null;

        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,brand,licencePlate FROM Car WHERE brand = ?");

            st.setString(1, brand.name());
            ResultSet rs = st.executeQuery();
            return DatabaseTools.getCarsFromResultSet(rs);

        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                        .getString("listCarsError");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }
    }

    @Override
    public List<Car> listAllCars() throws ServiceFailureException {
        Connection connection = null;
        PreparedStatement st = null;

        try {
            connection = dataSource.getConnection();
            st = connection.prepareStatement(
                    "SELECT id,licencePlate,brand FROM Car");

            ResultSet rs = st.executeQuery();
            return DatabaseTools.getCarsFromResultSet(rs);

        } catch (SQLException ex) {
            String msg = ResourceBundle.getBundle("strings")
                        .getString("listCarsError");
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DatabaseTools.closeConnectionAndStatements(connection, st);
        }

    }

    private void checkCar(Car car) throws IllegalArgumentException {
        if (car == null) {
            throw new IllegalArgumentException(ResourceBundle.getBundle("strings")
            .getString("carIdNull"));
        }
        if (car.getLicencePlate() == null) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
            .getString("licencePlateNull"));

        }
        if (!car.getLicencePlate().matches("[0-9]*[A-Z]+[0-9]*")) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
            .getString("wrongLicencePlate"));

        }
        if (car.getLicencePlate().length() != 8) {
            throw new IllegalEntityException(ResourceBundle.getBundle("strings")
            .getString("wrongLicencePlate"));

        }

    }

}
