public class LoanCustomerData {

    int id;
    String name;
    int age;
    double salary;
    int creditScore;
    String employmentStatus;

    LoanCustomerData(int id, String name, int age, double salary,
                     int creditScore, String employmentStatus) {

        this.id = id;
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.creditScore = creditScore;
        this.employmentStatus = employmentStatus;
    }
}