class Person {

    String name;
    int age;

    void displayPersonDetails() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
    }
}

class Student extends Person {

    int rollNumber;
    String course;

    void displayStudentDetails() {
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Course: " + course);
    }
}

public class Main {

    public static void main(String[] args) {

        Student s1 = new Student();

        s1.name = "Mohit";
        s1.age = 22;
        s1.rollNumber = 101;
        s1.course = "Java";

        s1.displayPersonDetails();
        s1.displayStudentDetails();
    }
}