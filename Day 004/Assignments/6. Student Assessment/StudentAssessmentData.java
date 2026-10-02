package studentAssessment;

public class StudentAssessmentData {

    private int id;
    private String name;
    private double marks1;
    private double marks2;
    private double marks3;

    /*
    Constructor
    */

    StudentAssessmentData(int id, String name,
                          double marks1, double marks2,
                          double marks3) {

        this.id = id;
        this.name = name;
        this.marks1 = marks1;
        this.marks2 = marks2;
        this.marks3 = marks3;
    }

    /*
    Getters
    */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMarks1() {
        return marks1;
    }

    public double getMarks2() {
        return marks2;
    }

    public double getMarks3() {
        return marks3;
    }
}