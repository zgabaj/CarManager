package cz.muni.fi.pv168.carmanager.backend;

import cz.muni.fi.pv168.carmanager.databasetools.IllegalEntityException;
import cz.muni.fi.pv168.carmanager.databasetools.ServiceFailureException;
import java.time.LocalDate;
import java.util.List;


public interface LeaseManager {
    
    
    void createNewLease(Lease lease) throws IllegalEntityException,ServiceFailureException;
    
    
    void deleteLease(Lease lease) throws IllegalEntityException,ServiceFailureException;
    
   
    void updateLease(Lease lease) throws IllegalEntityException,ServiceFailureException;
   
    List<Lease> listAllLeases() throws ServiceFailureException;
        
    List<Lease> listAllLeasesForCar(Car car) throws IllegalEntityException,ServiceFailureException;
    
    Lease getLeaseById(Long id) throws ServiceFailureException;
   
    List<Lease> listAllLeasesBetweenDates(LocalDate startDate, LocalDate endDate) throws ServiceFailureException;
    
    List<Car> listAllFreeCarsBetweenDates(LocalDate startDate,LocalDate endDate) throws ServiceFailureException;
    
}
