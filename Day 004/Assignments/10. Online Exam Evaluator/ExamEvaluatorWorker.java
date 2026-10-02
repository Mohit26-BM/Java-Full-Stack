package onlineExamEvaluator;

public class ExamEvaluatorWorker implements Runnable {

    private ExaminationData examination;

    ExamEvaluatorWorker(ExaminationData examination) {
        this.examination = examination;
    }

    /*
    The run() method contains the work
    performed by the worker thread.
    */

    @Override
    public void run() {

        System.out.println(
                Thread.currentThread().getName()
                + " started evaluation for "
                + examination.getStudentName()
        );

        /*
        Simulate examination evaluation.
        */

        double percentage =
                ((double) examination.getCorrectAnswers()
                / examination.getTotalQuestions()) * 100;

        String result;

        if (percentage >= 40) {
            result = "PASS";
        }
        else {
            result = "FAIL";
        }

        /*
        Display evaluation result.
        */

        System.out.println(
                "Exam ID: "
                + examination.getExamId()
        );

        System.out.println(
                "Student: "
                + examination.getStudentName()
        );

        System.out.println(
                "Score: "
                + examination.getCorrectAnswers()
                + "/"
                + examination.getTotalQuestions()
        );

        System.out.println(
                "Percentage: "
                + percentage
                + "%"
        );

        System.out.println(
                "Result: "
                + result
        );

        System.out.println(
                Thread.currentThread().getName()
                + " completed evaluation."
        );

        System.out.println("-------------------------");
    }
}
