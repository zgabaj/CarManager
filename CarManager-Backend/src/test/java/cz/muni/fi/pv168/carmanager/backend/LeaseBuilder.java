/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.backend;

import cz.muni.fi.pv168.carmanager.backend.Lease;
import java.time.LocalDate;
import java.time.Month;

/**
 *
 * @author Erik Macej,433 744
 */
public class LeaseBuilder {
    
    private Long id;
    private String customerFullName;;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long carId;
    
    public LeaseBuilder id(Long id){
        this.id  = id;
        return this;
    }
    
    public LeaseBuilder customerFullName(String customerFullName){
        this.customerFullName  = customerFullName;
        return this;
    }
    
    public LeaseBuilder startDate(LocalDate startDate){
        this.startDate = startDate;
        return this;
    }
    
    public LeaseBuilder startDate(int year,Month month,int day){
        this.startDate = LocalDate.of(year,month,day);
        return this;
    }
     
    public LeaseBuilder endDate(LocalDate endDate){
        this.endDate  = endDate;
        return this;
    }
    
    public LeaseBuilder endDate(int year,Month month,int day){
        this.endDate = LocalDate.of(year,month,day);
        return this;
    }
     
    public LeaseBuilder carId(Long carId){
        this.carId  = carId;
        return this;
    }
    
    public Lease build(){
        
        Lease lease = new Lease();
        lease.setId(id);
        lease.setCustomerFullName(customerFullName);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setCarId(carId);
        
        return lease;
    }
     
}
