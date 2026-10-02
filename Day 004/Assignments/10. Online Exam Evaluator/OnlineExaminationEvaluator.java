package onlineExamEvaluator;

public class OnlineExaminationEvaluator {

    public static void main(String[] args) {

        try {

            /*
            Creating examination objects.
            */

            ExaminationData exam1 =
                    new ExaminationData(
                            101,
                            "Ravi",
                            50,
                            45
                    );

            ExaminationData exam2 =
                    new ExaminationData(
                            102,
                            "Priya",
                            50,
                            38
                    );

            ExaminationData exam3 =
                    new ExaminationData(
                            103,
                            "Amit",
                            40,
                            32
                    );

            ExaminationData exam4 =
                    new ExaminationData(
                            104,
                            "Neha",
                            40,
                            15
                    );


            /*
            Creating worker threads.
            */

            Thread worker1 =
                    new Thread(
                            new ExamEvaluatorWorker(exam1),
                            "Worker-1"
                    );

            Thread worker2 =
                    new Thread(
                            new ExamEvaluatorWorker(exam2),
                            "Worker-2"
                    );

            Thread worker3 =
                    new Thread(
                            new ExamEvaluatorWorker(exam3),
                            "Worker-3"
                    );

            Thread worker4 =
                    new Thread(
                            new ExamEvaluatorWorker(exam4),
                            "Worker-4"
                    );


            /*
            Start all worker threads.
            */

            System.out.println(
                    "Starting examination evaluation..."
            );

            worker1.start();
            worker2.start();
            worker3.start();
            worker4.start();


            /*
            Wait for all worker threads to finish.
            */

            worker1.join();
            worker2.join();
            worker3.join();
            worker4.join();


            System.out.println(
                    "All examinations have been evaluated."
            );


            /*
            Negative test case.
            */

            System.out.println();
            System.out.println("INVALID INPUT TEST");

            ExaminationData invalidExam =
                    new ExaminationData(
                            105,
                            "Karan",
                            50,
                            60
                    );

        }
        catch (ExamEvaluationException e) {

            System.out.println(
                    "Error: " + e.getMessage()
            );
        }
        catch (InterruptedException e) {

            System.out.println(
                    "Thread was interrupted."
            );
        }
    }
}