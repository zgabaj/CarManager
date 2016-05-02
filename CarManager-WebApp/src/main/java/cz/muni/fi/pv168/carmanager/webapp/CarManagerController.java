/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.carmanager.webapp;

import cz.muni.fi.pv168.carmanager.backend.*;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "CarManagerController", urlPatterns = {"/Cars/*"})
public class CarManagerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listCars(request, response);

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
            case "/update/licencePlate":
                updateLicencePlate(request, response);
                break;
            case "/update/brand":
                updateBrand(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Unknown action: " + action);
        }
    }

    private CarManager getCarManager() {
        return (CarManager) getServletContext().getAttribute("carManager");
    }

    private void listCars(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Car> cars = getCarManager().listAllCars();
            request.setAttribute("cars", cars);
            request.getRequestDispatcher("/WEB-INF/cars/carList.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String licencePlate = request.getParameter("licencePlate");
        String brand = request.getParameter("brand");
        
        if (!checkNoLicencePlate(licencePlate, request, response)) {
            return;
        }

        if (!checkLicencePlate(licencePlate, request, response)) {
            return;
        }

        try {
            Car car = new Car();
            car.setBrand(Brand.valueOf(brand));
            car.setLicencePlate(licencePlate);
            car.setId(null);
            getCarManager().createCar(car);
            response.sendRedirect(request.getContextPath() + "/Cars/list");

        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            getCarManager().deleteCar(getCarManager().getCarById(id));
            response.sendRedirect(request.getContextPath() + "/Cars/list");
        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateLicencePlate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String newLicencePlate = request.getParameter("newLicencePlate");

            if (!checkLicencePlate(newLicencePlate, request, response)) {
                return;
            }

            Long id = Long.valueOf(request.getParameter("id"));
            Car car = getCarManager().getCarById(id);

            car.setLicencePlate(newLicencePlate);
            getCarManager().updateCar(car);
            response.sendRedirect(request.getContextPath() + "/Cars/list");

        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateBrand(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String newBrand = request.getParameter("newBrand");
            Long id = Long.valueOf(request.getParameter("id"));
            Car car = getCarManager().getCarById(id);

            car.setBrand(Brand.valueOf(newBrand));
            getCarManager().updateCar(car);
            response.sendRedirect(request.getContextPath() + "/Cars/list");

        } catch (Exception e) {
            System.err.println(e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private boolean checkLicencePlate(String lp, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!lp.matches("[0-9]*[A-Z]+[0-9]*") || lp.length() != 8) {
            request.setAttribute("wrongPlate", "Zadali ste zlu SPZ.");
            listCars(request, response);
            return false;
        }
        return true;
    }

    private boolean checkNoLicencePlate(String lp, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (lp == null || lp.isEmpty()) {
            request.setAttribute("noPlate", "prosim zadajte SPZ.");
            listCars(request, response);
            return false;
        }
        return true;
    }
}
