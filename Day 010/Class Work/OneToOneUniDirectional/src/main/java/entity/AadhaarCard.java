
package entity;


import jakarta.persistence.*;

@Entity
@Table(name = "AadhaarCard")
public class AadhaarCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String aadhaarNumber;

    public AadhaarCard() {
    }

    public AadhaarCard(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }
}
