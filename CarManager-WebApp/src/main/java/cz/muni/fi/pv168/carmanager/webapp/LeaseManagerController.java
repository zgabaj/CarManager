/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.webapp;

import cz.muni.fi.pv168.carmanager.backend.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "LeaseManagerController", urlPatterns = {"/Leases/*"})
public class LeaseManagerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listLeases(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                add(request, response);
                break;
            case "/delete":
                delete(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }

    private void listLeases(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            List<Lease> leases = getLeaseManager().listAllLeases();
            request.setAttribute("leases", leases);
            List<Car> cars = getCarManager().listAllCars();
            request.setAttribute("cars", cars);
            request.getRequestDispatcher("/WEB-INF/leases/leaseList.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    private LeaseManagerImpl getLeaseManager() {
        return (LeaseManagerImpl) getServletContext().getAttribute("leaseManager");
    }

    private CarManagerImpl getCarManager() {
        return (CarManagerImpl) getServletContext().getAttribute("carManager");
    }

    private void add(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String name = request.getParameter("customerFullName");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String carId = request.getParameter("carId");

        if (!checkStr(startDateStr, request, response) || !checkStr(endDateStr, request, response)
                || !checkStr(name, request, response)) {
            return;
        }

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        if (!checkDate(startDate, endDate, request, response)) {
            return;
        }

        try {

            List<Car> cars = getLeaseManager().listAllFreeCarsBetweenDates(startDate, endDate);
            if (!cars.contains(getCarManager().getCarById(Long.parseLong(carId)))) {
                request.setAttribute("unavailable", "Auto nieje k dispozicii");
                listLeases(request, response);
                return;
            }
            createLease(carId, name, startDate, endDate, request, response);

        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            getLeaseManager().deleteLease(getLeaseManager().getLeaseById(id));
            response.sendRedirect(request.getContextPath() + "/Leases/list");
        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    private boolean checkStr(String str, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (str == null || str.isEmpty()) {
            request.setAttribute("error", "Vyplnte vsetky udaje");
            listLeases(request, response);
            return false;
        }
        return true;
    }

    private boolean checkDate(LocalDate sd, LocalDate ed, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
        if (ed.isBefore(sd) || sd.isBefore(LocalDate.now())) {
            request.setAttribute("date", "Zle vyplneny datum");
            listLeases(request, response);
            return false;
        }
        return true;
    }

    private void createLease(String id, String name, LocalDate sd, LocalDate ed,
            HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        Lease lease = new Lease();
        lease.setCustomerFullName(name);
        lease.setStartDate(sd);
        lease.setEndDate(ed);
        lease.setCarId(Long.parseLong(id));
        getLeaseManager().createNewLease(lease);
        resp.sendRedirect(req.getContextPath() + "/Leases/list");
    }

}
