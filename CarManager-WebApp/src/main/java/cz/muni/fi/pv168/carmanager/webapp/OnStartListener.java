/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.webapp;

import cz.muni.fi.pv168.carmanager.backend.CarManagerImpl;
import cz.muni.fi.pv168.carmanager.backend.LeaseManagerImpl;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;


@WebListener
public class OnStartListener implements ServletContextListener {

    @Resource(name = "jdbc/CarLease")
    private DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        CarManagerImpl carManager = new CarManagerImpl(dataSource);
        LeaseManagerImpl leaseManager = new LeaseManagerImpl();
        leaseManager.setDataSource(dataSource);
        
        ServletContext servletContext = sce.getServletContext();
        
        servletContext.setAttribute("carManager", carManager);
        servletContext.setAttribute("leaseManager", leaseManager);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
