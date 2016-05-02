/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.databasetools;


public class IllegalCarException extends RuntimeException{
    
    public IllegalCarException(){
    }
    
    public IllegalCarException(String message){
        super(message);
    }
    
}
