package studentAssessment;

public class StudentAssessmentSystem {

    /*
    Configurable evaluation rules
    */

    private static final double DISTINCTION_A_PLUS = 90;
    private static final double DISTINCTION_A = 80;
    private static final double GRADE_B = 70;
    private static final double GRADE_C = 60;
    private static final double PASS_MARKS = 50;


    /*
    Validates student information.
    */

    public static void validateStudent(
            StudentAssessmentData student)
            throws Exception {

        if (student.getId() <= 0) {
            throw new Exception(
                    "Student ID must be greater than 0");
        }

        if (student.getName() == null
                || student.getName().trim().isEmpty()) {

            throw new Exception(
                    "Student name cannot be empty");
        }

        if (student.getMarks1() < 0
                || student.getMarks1() > 100) {

            throw new Exception(
                    "Subject 1 marks must be between 0 and 100");
        }

        if (student.getMarks2() < 0
                || student.getMarks2() > 100) {

            throw new Exception(
                    "Subject 2 marks must be between 0 and 100");
        }

        if (student.getMarks3() < 0
                || student.getMarks3() > 100) {

            throw new Exception(
                    "Subject 3 marks must be between 0 and 100");
        }
    }


    /*
    Calculates average marks.
    */

    public static double calculateAverage(
            StudentAssessmentData student) {

        return (student.getMarks1()
                + student.getMarks2()
                + student.getMarks3()) / 3;
    }


    /*
    Calculates the grade using the configured
    evaluation rules.
    */

    public static String calculateGrade(double average) {

        if (average >= DISTINCTION_A_PLUS) {
            return "A+";
        }

        if (average >= DISTINCTION_A) {
            return "A";
        }

        if (average >= GRADE_B) {
            return "B";
        }

        if (average >= GRADE_C) {
            return "C";
        }

        if (average >= PASS_MARKS) {
            return "D";
        }

        return "F";
    }


    /*
    Determines academic status.
    */

    public static String calculateAcademicStatus(
            double average) {

        if (average >= PASS_MARKS) {
            return "PASS";
        }

        return "FAIL";
    }


    /*
    Determines whether the student has distinction.
    */

    public static String calculateDistinction(
            double average) {

        if (average >= DISTINCTION_A) {
            return "Distinction";
        }

        return "No Distinction";
    }


    /*
    Displays complete assessment information.
    */

    public static void displayAssessment(
            StudentAssessmentData student) {

        double average = calculateAverage(student);

        String grade = calculateGrade(average);

        String status = calculateAcademicStatus(average);

        String distinction = calculateDistinction(average);


        System.out.println("Student ID: "
                + student.getId());

        System.out.println("Student Name: "
                + student.getName());

        System.out.println("Subject 1 Marks: "
                + student.getMarks1());

        System.out.println("Subject 2 Marks: "
                + student.getMarks2());

        System.out.println("Subject 3 Marks: "
                + student.getMarks3());

        System.out.println("Average: "
                + average);

        System.out.println("Grade: "
                + grade);

        System.out.println("Academic Status: "
                + status);

        System.out.println("Result: "
                + distinction);
    }


    public static void main(String[] args) {

        try {

            /*
            Creating student objects
            */

            StudentAssessmentData student1 =
                    new StudentAssessmentData(
                            101,
                            "Ravi",
                            95,
                            92,
                            90
                    );

            StudentAssessmentData student2 =
                    new StudentAssessmentData(
                            102,
                            "Priya",
                            82,
                            85,
                            80
                    );

            StudentAssessmentData student3 =
                    new StudentAssessmentData(
                            103,
                            "Amit",
                            72,
                            75,
                            70
                    );

            StudentAssessmentData student4 =
                    new StudentAssessmentData(
                            104,
                            "Neha",
                            45,
                            50,
                            48
                    );


            /*
            Student 1
            */

            System.out.println("STUDENT 1");

            validateStudent(student1);

            displayAssessment(student1);

            System.out.println("-------------------------");


            /*
            Student 2
            */

            System.out.println();
            System.out.println("STUDENT 2");

            validateStudent(student2);

            displayAssessment(student2);

            System.out.println("-------------------------");


            /*
            Student 3
            */

            System.out.println();
            System.out.println("STUDENT 3");

            validateStudent(student3);

            displayAssessment(student3);

            System.out.println("-------------------------");


            /*
            Student 4
            */

            System.out.println();
            System.out.println("STUDENT 4");

            validateStudent(student4);

            displayAssessment(student4);

            System.out.println("-------------------------");


            /*
            Negative test case
            */

            System.out.println();
            System.out.println("INVALID INPUT TEST");

            StudentAssessmentData invalidStudent =
                    new StudentAssessmentData(
                            105,
                            "Karan",
                            110,
                            85,
                            90
                    );

            validateStudent(invalidStudent);

            displayAssessment(invalidStudent);

        }
        catch (Exception e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}