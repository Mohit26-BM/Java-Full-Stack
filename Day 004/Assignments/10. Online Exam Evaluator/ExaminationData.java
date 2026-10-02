package onlineExamEvaluator;

public class ExaminationData {

    private int examId;
    private String studentName;
    private int totalQuestions;
    private int correctAnswers;

    /*
    Constructor
    */

    ExaminationData(int examId, String studentName,
                    int totalQuestions, int correctAnswers)
            throws ExamEvaluationException {

        if (examId <= 0) {
            throw new ExamEvaluationException(
                    "Exam ID must be greater than 0");
        }

        if (studentName == null
                || studentName.trim().isEmpty()) {

            throw new ExamEvaluationException(
                    "Student name cannot be empty");
        }

        if (totalQuestions <= 0) {
            throw new ExamEvaluationException(
                    "Total questions must be greater than 0");
        }

        if (correctAnswers < 0
                || correctAnswers > totalQuestions) {

            throw new ExamEvaluationException(
                    "Correct answers must be between 0 and total questions");
        }

        this.examId = examId;
        this.studentName = studentName;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
    }

    public int getExamId() {
        return examId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }
}
