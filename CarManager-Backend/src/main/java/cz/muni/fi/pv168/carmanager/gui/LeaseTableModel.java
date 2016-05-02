/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.gui;

import cz.muni.fi.pv168.carmanager.backend.Car;
import cz.muni.fi.pv168.carmanager.backend.CarManagerImpl;
import cz.muni.fi.pv168.carmanager.backend.Lease;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;


public class LeaseTableModel extends AbstractTableModel{
    
    private CarManagerImpl carManagerImpl;
    private List<Lease> leases = new ArrayList<>();

    public LeaseTableModel(CarManagerImpl carManagerImpl) {
        this.carManagerImpl = carManagerImpl;
    }
    
    
    @Override
    public int getRowCount() {
        return leases.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columIndex) {
        
        Lease lease = leases.get(rowIndex);
        Car car = carManagerImpl.getCarById(lease.getCarId());
        
        switch(columIndex){
            case 0:
                return lease.getCustomerFullName();
            case 1:
                return lease.getStartDate();
            case 2:
                return lease.getEndDate();
            case 3:
                return car.getBrand();
            case 4:
                return car.getLicencePlate();
            default:
                throw new IllegalArgumentException("Illegal columIndex");
        }
        
    }

    @Override
    public String getColumnName(int columnIndex){   
        switch(columnIndex){
            case 0:
                return ResourceBundle.getBundle("strings").getString("nameColumn");
            case 1:
                return ResourceBundle.getBundle("strings").getString("startDateColumn");
            case 2:
                return ResourceBundle.getBundle("strings").getString("endDateColumn");
            case 3:
                return ResourceBundle.getBundle("strings").getString("brandColumn");
            case 4:
                return ResourceBundle.getBundle("strings").getString("licencePlateColumn");
            default:
                throw new IllegalArgumentException("Illegal column index");
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex){  
            if(columnIndex > 0  && columnIndex < 5){
                return String.class;
            }else{
                throw new IllegalArgumentException("Illegal column index");
            }
    }

    
    
    public void addLease(Lease lease ){
        leases.add(lease);
        int lastRow = leases.size() -1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void deleteLease(Lease lease){
        leases.remove(lease);
        int lastRow = leases.size() - 1;
        fireTableRowsDeleted(lastRow, lastRow);
    }
    
    public Lease getSelectetdLease(int rowIndex){
        return leases.get(rowIndex);
    }
    
    public List<Lease> getAllCars(){
        return Collections.unmodifiableList(leases);
    } 
    
}
