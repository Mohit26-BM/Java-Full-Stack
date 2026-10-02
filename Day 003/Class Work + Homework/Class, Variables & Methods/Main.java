class Student {

    int rollNo;
    String name;
    double marks;

    Student(int rollNo, String name, double marks) {
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
    }

    char calculateGrade() {

        if (marks >= 90) {
            return 'A';
        }
        else if (marks >= 75) {
            return 'B';
        }
        else if (marks >= 60) {
            return 'C';
        }
        else if (marks >= 50) {
            return 'D';
        }
        else {
            return 'F';
        }
    }

    void displayStudent() {

        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + calculateGrade());
        System.out.println();
    }
}

public class Main {

    public static void main(String[] args) {

        Student s1 = new Student(101, "Rahul", 92);
        Student s2 = new Student(102, "Amit", 78);
        Student s3 = new Student(103, "Priya", 65);

        s1.displayStudent();
        s2.displayStudent();
        s3.displayStudent();
    }
}