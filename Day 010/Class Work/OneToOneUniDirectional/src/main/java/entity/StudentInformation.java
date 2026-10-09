
package entity;


import jakarta.persistence.*;
@Entity
@Table(name = "Student")
public class StudentInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rollno;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "aadhaar_id")
    private AadhaarCard aadhaarCard;

    public StudentInformation() {
    }

    public StudentInformation(String name, AadhaarCard aadhaarCard) {
        this.name = name;
        this.aadhaarCard = aadhaarCard;
    }

    public int getRollno() {
        return rollno;
    }

    public void setRollno(int rollno) {
        this.rollno = rollno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AadhaarCard getAadhaarCard() {
        return aadhaarCard;
    }

    public void setAadhaarCard(AadhaarCard aadhaarCard) {
        this.aadhaarCard = aadhaarCard;
    }
}
