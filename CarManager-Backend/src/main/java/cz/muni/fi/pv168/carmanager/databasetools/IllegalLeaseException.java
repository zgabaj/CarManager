/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.databasetools;


public class IllegalLeaseException extends RuntimeException{
    
    public IllegalLeaseException(){
    }
    
    public IllegalLeaseException(String message){
        super(message);
    }
    
}
