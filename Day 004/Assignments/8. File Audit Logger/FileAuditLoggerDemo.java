package fileAuditLogger;

public class FileAuditLoggerDemo {

    public static void main(String[] args) {

        try {

            /*
            Create the audit logger.
            */

            FileAuditLogger logger =
                    new FileAuditLogger(
                            "audit.log",
                            "audit_archive.log"
                    );


            /*
            Write audit log entries.
            */

            System.out.println("WRITING LOGS");

            logger.writeLog(
                    "User Ravi logged into the system.");

            logger.writeLog(
                    "User Ravi viewed account details.");

            logger.writeLog(
                    "User Ravi completed a transaction.");


            /*
            Read the audit log.
            */

            System.out.println();

            logger.readLog();


            /*
            Archive the audit log.
            */

            System.out.println();

            System.out.println("ARCHIVING LOG");

            logger.archiveLog();


            /*
            Negative test.
            Try to read the log after it has
            been archived.
            */

            System.out.println();

            System.out.println("INVALID OPERATION TEST");

            logger.readLog();

        }
        catch (AuditLogException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}
