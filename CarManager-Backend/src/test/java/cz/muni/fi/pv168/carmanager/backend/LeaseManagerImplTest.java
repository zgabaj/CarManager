/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.backend;

import cz.muni.fi.pv168.carmanager.databasetools.DatabaseTools;
import cz.muni.fi.pv168.carmanager.databasetools.IllegalEntityException;
import cz.muni.fi.pv168.carmanager.databasetools.IllegalLeaseException;
import cz.muni.fi.pv168.carmanager.backend.Brand;
import cz.muni.fi.pv168.carmanager.backend.Car;
import cz.muni.fi.pv168.carmanager.backend.CarManagerImpl;
import cz.muni.fi.pv168.carmanager.backend.Lease;
import cz.muni.fi.pv168.carmanager.backend.LeaseManager;
import cz.muni.fi.pv168.carmanager.backend.LeaseManagerImpl;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.assertj.core.api.Assertions.*;
import static java.time.Month.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Erik Macej,433 744
 */
public class LeaseManagerImplTest {
    
    private LeaseManagerImpl leaseManager;
    private CarManagerImpl carManager;
    private DataSource dataSource;
    private Car car1;
    private Car car2;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    private static DataSource prepareDataSource() throws SQLException{
        
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("memory:leasemanagertest");
        dataSource.setCreateDatabase("create");
        
        return dataSource;
    }
    
    @Before
    public void setUp() throws SQLException{
        this.dataSource = prepareDataSource();
        DatabaseTools.executeSqlScript(this.dataSource, LeaseManager.class.getResource("createTable.sql"));
        car1 = newCar( "4A230080", Brand.BMW);
        car2 = newCar( "5B112344", Brand.MERCEDES_BENZ);
        
        this.leaseManager = new LeaseManagerImpl();
        this.leaseManager.setDataSource(dataSource);
        
        this.carManager = new CarManagerImpl(dataSource);
    }
    
    @After
    public void tearDown() throws SQLException{
        DatabaseTools.executeSqlScript(this.dataSource, LeaseManager.class.getResource("dropTables.sql"));
    }
    
    private LeaseBuilder firstLeaseBuilder(){
        return new LeaseBuilder()
                .id(null)
                .customerFullName("Erik Macej")
                .startDate(2016,JANUARY,15)
                .endDate(2016,JANUARY,25)
                .carId(null);             
    }
    
    private LeaseBuilder anotherLeaseBuilder(){
        return new LeaseBuilder()
                .id(null)
                .customerFullName("Marek Zgabaj")
                .startDate(2016,FEBRUARY,15)
                .endDate(2016,FEBRUARY,25)
                .carId(null);             
    }
    
    @Test
    public void createNewLeaseTest(){
        this.carManager.createCar(car1);
        Lease firstLease = firstLeaseBuilder().carId(car1.getId()).build();
        
        this.leaseManager.createNewLease(firstLease);
        
        Long firstLeaseId = firstLease.getCarId();
        assertThat(firstLeaseId).isNotNull();
        assertThat(this.leaseManager.getLeaseById(firstLeaseId))
                .isNotSameAs(firstLease).isEqualToComparingFieldByField(firstLease);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createNullNewLease(){
        this.leaseManager.createNewLease(null);
    }
    
    @Test
    public void createNewLeaseWithId() {
        Lease lease = firstLeaseBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        this.leaseManager.createNewLease(lease);
    }
    
    @Test
    public void createNewLeaseWithNullCustomerFullName() {
        Lease lease = firstLeaseBuilder().customerFullName(null).build();
        expectedException.expect(IllegalLeaseException.class);
        this.leaseManager.createNewLease(lease);
    }
    
    @Test
    public void createNewLeaseWithNullStartDate() {
        Lease lease = firstLeaseBuilder().startDate(null).build();
        expectedException.expect(IllegalLeaseException.class);
        this.leaseManager.createNewLease(lease);
    }
    
    @Test
    public void createNewLeaseWithNullEndDate() {
        Lease lease = firstLeaseBuilder().endDate(null).build();
        expectedException.expect(IllegalLeaseException.class);
        this.leaseManager.createNewLease(lease);
    }
    
    @Test
    public void createNewLeaseWithIllegalDates() {
        Lease lease = firstLeaseBuilder().endDate(2016,JANUARY,19).startDate(2016,JANUARY,25).build();
        expectedException.expect(IllegalLeaseException.class);
        this.leaseManager.createNewLease(lease);
    }
    
    @Test
    public void createNewLeaseWithIllegalaCarId() {
        Lease lease = firstLeaseBuilder().carId(null).build();
        expectedException.expect(IllegalLeaseException.class);
        this.leaseManager.createNewLease(lease);
    }
    
    @Test
    public void createNewLeaseWithNonExistingCar(){
        Lease lease = firstLeaseBuilder().carId(1L).build();
        expectedException.expect(IllegalLeaseException.class);
        this.leaseManager.createNewLease(lease);
    }
    
    @Test
    public void createNewLeaseWithUnavailableCare(){
        this.carManager.createCar(car1);
        Lease lease = firstLeaseBuilder().carId(car1.getId()).build();
        Lease anotherLease = firstLeaseBuilder().carId(car1.getId()).build();
        this.leaseManager.createNewLease(lease);
        expectedException.expect(IllegalLeaseException.class);
        this.leaseManager.createNewLease(anotherLease);
    }
    
    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }
    
    private void testUpdateLease(Operation<Lease> updateOperation) {
        this.carManager.createCar(car1);
        this.carManager.createCar(car2);
        Lease leaseForUpdate = firstLeaseBuilder().carId(car1.getId()).build();
        Lease anotherLease = anotherLeaseBuilder().carId(car2.getId()).build();
        this.leaseManager.createNewLease(leaseForUpdate);
        this.leaseManager.createNewLease(anotherLease);

        updateOperation.callOn(leaseForUpdate);

        this.leaseManager.updateLease(leaseForUpdate);
        assertThat(this.leaseManager.getLeaseById(leaseForUpdate.getId()))
                .isEqualToComparingFieldByField(leaseForUpdate);
        
        assertThat(this.leaseManager.getLeaseById(anotherLease.getId()))
                .isEqualToComparingFieldByField(anotherLease);
    }
    
    @Test
    public void updateCustomerFullName() {
        testUpdateLease((lease) -> lease.setCustomerFullName("Abdul"));
    }

    @Test
    public void updateLeaseStartDate() {
        testUpdateLease((lease) -> lease.setStartDate(LocalDate.of(2016,JANUARY,10)));
    }

    @Test
    public void updateLeaseEndDate() {
        testUpdateLease((lease) -> lease.setEndDate(LocalDate.of(2016,JANUARY,20)));
    }
    
    @Test
    public void deleteLease(){
        carManager.createCar(car1);
        carManager.createCar(car2);
        
        Lease lease1 = firstLeaseBuilder().carId(car1.getId()).build();
        Lease lease2 = firstLeaseBuilder().carId(car2.getId()).build();
        
        leaseManager.createNewLease(lease1);
        leaseManager.createNewLease(lease2);
        
        Long leasId = lease1.getId();
        
        leaseManager.deleteLease(lease1);
        
        assertThat(leaseManager.getLeaseById(lease2.getId()))
                .isEqualToComparingFieldByField(lease2);
        assertThat(leaseManager.getLeaseById(leasId)).isNull();        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteLeaseWithNullLease() {
        leaseManager.deleteLease(null);
    }
    
    @Test(expected = IllegalEntityException.class)
    public void deleteLeaseWithNullLeaseId() {
        carManager.createCar(car1);
        Lease lease = firstLeaseBuilder().carId(car1.getId()).build();
        leaseManager.deleteLease(lease);
    }
    
    @Test
    public void listAllLeases(){
        assertThat(leaseManager.listAllLeases().isEmpty());
        
        carManager.createCar(car1);
        carManager.createCar(car2);
        
        Lease lease1 = firstLeaseBuilder().carId(car1.getId()).build();
        Lease lease2 = firstLeaseBuilder().carId(car2.getId()).build();
        
        leaseManager.createNewLease(lease1);
        leaseManager.createNewLease(lease2);
            
        assertThat(leaseManager.listAllLeases()).usingFieldByFieldElementComparator()
                .containsOnly(lease1,lease2);;
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void listAllLeasesForCarWhitNullCar(){
        leaseManager.listAllLeasesForCar(null);
    
    }
    
    @Test(expected = IllegalEntityException.class)
    public void listAllLeasesForCarWhitNullCarId(){
        leaseManager.listAllLeasesForCar(car1);
    
    }
    
    @Test
    public void getLeaseByIdTest(){
        
        carManager.createCar(car1);
        Lease lease = firstLeaseBuilder().carId(car1.getId()).build();
        
        leaseManager.createNewLease(lease);
        Long leaseId = lease.getId();
        assertThat(leaseManager.getLeaseById(leaseId)).isNotNull()
                .isEqualToComparingFieldByField(lease);
        
    }
    
    @Test
    public void listAllLeasesBetweenDates(){
        
        carManager.createCar(car1);
        carManager.createCar(car2);
        
        Lease lease = firstLeaseBuilder().carId(car1.getId()).build();
        Lease anotherLease = anotherLeaseBuilder().carId(car2.getId()).build();
        
        leaseManager.createNewLease(lease);
        leaseManager.createNewLease(anotherLease);
        
        assertThat(leaseManager.listAllLeasesBetweenDates(LocalDate.of(2016, JANUARY, 13),
                LocalDate.of(2016, JANUARY, 26)))
                .usingFieldByFieldElementComparator()
                .containsOnly(lease);
        
        
    }
    
    @Test
    public void listAllFreeCarsBetweenDates(){
        
        carManager.createCar(car1);
        carManager.createCar(car2);
        Car car3 = newCar("6B112384",Brand.KIA);
        carManager.createCar(car3);
        
        Lease lease = firstLeaseBuilder().carId(car1.getId()).build();
        Lease anotherLease = anotherLeaseBuilder().carId(car2.getId()).build();
        
        leaseManager.createNewLease(lease);
        leaseManager.createNewLease(anotherLease);
        
        assertThat(leaseManager.listAllFreeCarsBetweenDates(LocalDate.of(2016, JANUARY, 13),
                LocalDate.of(2016, FEBRUARY, 26)))
                .usingFieldByFieldElementComparator()
                .containsOnly(car3);
        
        assertThat(leaseManager.listAllFreeCarsBetweenDates(LocalDate.of(2016, JANUARY, 1),
                LocalDate.of(2016, JANUARY, 26)))
                .usingFieldByFieldElementComparator()
                .containsOnly(car3,car2);
        
    }
     
    private static Car newCar( String lp, Brand brand) {
        Car car = new Car();
        car.setBrand(brand);
        car.setLicencePlate(lp);
        return car;
    }
        
}
