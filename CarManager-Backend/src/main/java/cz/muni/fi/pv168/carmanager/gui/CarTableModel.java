/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.gui;

import cz.muni.fi.pv168.carmanager.backend.Car;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;


public class CarTableModel extends AbstractTableModel {

    private List<Car> cars = new ArrayList<>();

    @Override
    public int getRowCount() {
        return cars.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Car car = cars.get(rowIndex);
        
        switch(columnIndex){
            case 0:
                return car.getBrand();
            case 1:
                return car.getLicencePlate();
            default:
                throw new IllegalArgumentException("Column index out of bounds");
        }
    }
    
    @Override
    public String getColumnName(int columnIndex){
        switch(columnIndex){
            case 0:
                return ResourceBundle.getBundle("strings").getString("brandColumn");
            case 1:
                return ResourceBundle.getBundle("strings").getString("licencePlateColumn");
            default:
                throw new IllegalArgumentException("Illegal column index");
        }
    }
    @Override
    public Class<?> getColumnClass(int columnIndex){  
        switch(columnIndex){
            case 0:
                return String.class;
            case 1:
                return String.class;
            default:
                throw new IllegalArgumentException("Illegal column index");
        }
    }
    
    
    public void addCar(Car car){
        cars.add(car);
        int lastRow = cars.size() -1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void deleteCar(Car car){
        cars.remove(car);
        int lastRow = cars.size() - 1;
        fireTableRowsDeleted(lastRow, lastRow);
    }
    
    public Car getSelectetdCar(int rowIndex){
        return cars.get(rowIndex);
    }
    
    public List<Car> getAllCars(){
        return Collections.unmodifiableList(cars);
    }
    
    public void deleteAllCars(){
        cars.clear();
    }
}
