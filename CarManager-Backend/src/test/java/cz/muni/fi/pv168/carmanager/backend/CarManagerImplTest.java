package cz.muni.fi.pv168.carmanager.backend;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import cz.muni.fi.pv168.carmanager.backend.Brand;
import cz.muni.fi.pv168.carmanager.backend.CarManager;
import cz.muni.fi.pv168.carmanager.backend.Car;
import cz.muni.fi.pv168.carmanager.backend.CarManagerImpl;
import cz.muni.fi.pv168.carmanager.databasetools.DatabaseTools;
import cz.muni.fi.pv168.carmanager.databasetools.IllegalEntityException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author marek
 */
public class CarManagerImplTest {

    private DataSource ds;
    private CarManagerImpl carManager;

    private Car car1;
    private Car car2;
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:carmgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DatabaseTools.executeSqlScript(ds,CarManager.class.getResource("createTable.sql"));

        car1 = newCar( "4A238000", Brand.BMW);
        car2 = newCar( "5B117234", Brand.MERCEDES_BENZ);
        carManager = new CarManagerImpl(ds);

    }
    
    @After
    public void tearDown() throws SQLException {
        DatabaseTools.executeSqlScript(ds, CarManager.class.getResource("dropTables.sql"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createCarNull() throws Exception {
        carManager.createCar(null);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void createCarWithWrongLicencePlate() throws Exception{
        car1.setLicencePlate("1234");
        carManager.createCar(car1);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void createCarWithNullLicencePlate() throws Exception{
        car1.setLicencePlate(null);
        carManager.createCar(car1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createCarWithExistingId() {
        car1.setId(1L);
        carManager.createCar(car1);
    }

    @Test
    public void createCar() {
        carManager.createCar(car1);
        Long idCar = car1.getId();

        assertDeepEquals(car1, carManager.getCarById(idCar));
    }

    @Test
    public void updateCar() {
        
        carManager.createCar(car1);
        Long idCar = car1.getId();

        carManager.updateCar(car1);
        Car carFromManager = carManager.getCarById(idCar);
        assertDeepEquals(carFromManager, car1);

        car1.setBrand(Brand.MERCEDES_BENZ);
        carManager.updateCar(car1);
        carFromManager = carManager.getCarById(idCar);
        assertDeepEquals(carFromManager, car1);

        car1.setLicencePlate("5B181234");
        carManager.updateCar(car1);
        carFromManager = carManager.getCarById(idCar);
        assertDeepEquals(carFromManager, car1);

    }

    @Test
    public void getAllCars() {
        assertTrue(carManager.listAllCars().isEmpty());

        carManager.createCar(car1);
        carManager.createCar(car2);

        List<Car> myList = Arrays.asList(car1, car2);
        List<Car> carManagerList = carManager.listAllCars();

        Collections.sort(carManagerList, idCompare);
        Collections.sort(myList, idCompare);
        

        assertEquals(myList.size(), carManagerList.size());
        assertDeepEquals(myList, carManagerList);

        carManagerList.remove(car1);

        assertNotEquals(carManagerList.size(), carManager.listAllCars().size());

    }
    @Test 
    public void deleteCar(){
        carManager.createCar(car1);
        carManager.createCar(car2);
        
        carManager.deleteCar(car1);
        assertDeepEquals(car2, carManager.getCarById(car2.getId()));
        expectedException.expect(IllegalEntityException.class);
        carManager.deleteCar(car1);
    }
    
    @Test 
    public void deleteCarWhitNegativeId(){
        car1.setId(-1L);
        expectedException.expect(IllegalEntityException.class);
        carManager.deleteCar(car1);
    }
    
    @Test
    public void getAllCarsByBrant() {
        assertTrue(carManager.listAllCarsByBrand(Brand.BMW).isEmpty());

        Car car3 = newCar("8C143721", Brand.MERCEDES_BENZ);

        carManager.createCar(car1);
        carManager.createCar(car2);
        carManager.createCar(car3);

        List<Car> myList = Arrays.asList(car1);
        List<Car> carManagerList = carManager.listAllCarsByBrand(Brand.BMW);

        assertEquals(myList.size(), carManagerList.size());
        assertDeepEquals(myList, carManagerList);

        myList = Arrays.asList(car2, car3);
        carManagerList = carManager.listAllCarsByBrand(Brand.MERCEDES_BENZ);

        Collections.sort(myList, idCompare);
        Collections.sort(carManagerList, idCompare);

        assertEquals(myList.size(), carManagerList.size());
        assertDeepEquals(carManagerList, carManagerList);

        assertTrue(carManager.listAllCarsByBrand(Brand.KIA).isEmpty());

        carManagerList.remove(car1);

        assertNotEquals(carManagerList.size(), carManager.listAllCars().size());

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void updateCarWhitNullCar(){
        carManager.updateCar(null);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void updateCarWhitNullCarID(){
        carManager.updateCar(car1);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void updateCarWhitWrongLicencePlate(){
        carManager.createCar(car1);
        car1.setLicencePlate("wrong licence plate");
        carManager.updateCar(car1);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void updateCarWhitNullLicencePlate(){
        carManager.createCar(car1);
        car1.setLicencePlate(null);
        carManager.updateCar(car1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteCarWhitNullCar(){
        carManager.deleteCar(null);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void deleteCarWhitNullCarId(){
        carManager.deleteCar(car1);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void deleteCarWhitWrongLicencePlate(){
        carManager.createCar(car1);
        car1.setLicencePlate("wrong licence plate");
        carManager.deleteCar(car1);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void deleteCarWhitNullLicencePlate(){
        carManager.createCar(car1);
        car1.setLicencePlate(null);
        carManager.deleteCar(car1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getCarByIdWhitNullId(){
        carManager.getCarById(null);
    }
    
    @Test
    public void getCarById(){
        carManager.createCar(car1);
        Long id = car1.getId();
        
        assertEquals(carManager.getCarById(id),car1);
    }
    

    private static Car newCar( String lp, Brand brand) {
        Car car = new Car();
        car.setBrand(brand);
        car.setLicencePlate(lp);
        return car;
    }

    private void assertDeepEquals(List<Car> expectedList, List<Car> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Car expected = expectedList.get(i);
            Car actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Car expected, Car actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBrand(), actual.getBrand());
        assertEquals(expected.getLicencePlate(), actual.getLicencePlate());
    }
    
    


    private static Comparator<Car> idCompare = new Comparator<Car>() {

        @Override
        public int compare(Car o1, Car o2) {
            return o1.getId().compareTo(o2.getId());
        }
    
    };
}
