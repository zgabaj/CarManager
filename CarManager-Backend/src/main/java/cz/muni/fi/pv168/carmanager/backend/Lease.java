package cz.muni.fi.pv168.carmanager.backend;

import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;


public class Lease {
    
    private Long id;
    private String customerFullName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long carId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
    
    @Override
    public String toString(){
        return  ResourceBundle.getBundle("strings").getString("leaseToSTring")
                + id + customerFullName + startDate + endDate + carId;
    }
    
    
    @Override
    public boolean equals(Object object){
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Lease other = (Lease) object;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
}
