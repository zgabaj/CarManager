package cz.muni.fi.pv168.carmanager.backend;

import cz.muni.fi.pv168.carmanager.databasetools.ServiceFailureException;
import java.util.List;


public interface CarManager {
    
    /**
     * Stores new car into database. Id for car is is automatically
     * generated and stored into id attribute.
     *
     * @param car car to by created.
     * @throws ServiceFailureException when database operation fails
     * @throws IllegalArgumentException when car is null, or car has already
     * assigned id. 
     */
    void createCar(Car car) throws ServiceFailureException;

    /**
     * Update car in database.
     *
     * @param car updated car to be stored in database.
     * @throws ServiceFailureException when database operation fails.
     * @throws IllegalArgumentException when car is null, or car has null id.
     */
    void updateCar(Car car) throws ServiceFailureException;

    /**
     * Deletes car from database.
     * 
     * @param car car  to be deleted from database.
     * @throws ServiceFailureException when database operation fails.
     * @throws IllegalArgumentException when car is null, or car has null id.
     */
    void deleteCar(Car car) throws ServiceFailureException;

    /**
     * Returns car with given id.
     *
     * @param id primary key of requested car.
     * @return car with given id or null if such car does not exist.
     * @throws ServiceFailureException when database operation fails.
     * @throws IllegalArgumentException when given id is null.
     */
    Car getCarById(Long id) throws ServiceFailureException;

    /**
     * Returns list of all cars whit given brand.
     *
     * @param brand brand of car.
     * @return list of all cars whit given brand or null if such car does 
     * not exist.
     * @throws ServiceFailureException when db operation fails
     */
    List<Car> listAllCarsByBrand(Brand brand) throws ServiceFailureException;

    /**
     * Returns list of all cars in the database.
     *
     * @return list of all cars in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    List<Car> listAllCars() throws ServiceFailureException;
}
