package fileAuditLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileAuditLogger {

    private String logFileName;
    private String archiveFileName;

    /*
    Constructor
    */

    FileAuditLogger(String logFileName, String archiveFileName)
            throws AuditLogException {

        if (logFileName == null
                || logFileName.trim().isEmpty()) {

            throw new AuditLogException(
                    "Log file name cannot be empty");
        }

        if (archiveFileName == null
                || archiveFileName.trim().isEmpty()) {

            throw new AuditLogException(
                    "Archive file name cannot be empty");
        }

        this.logFileName = logFileName;
        this.archiveFileName = archiveFileName;
    }


    /*
    Writes a new log entry to the audit file.
    */

    public void writeLog(String message)
            throws AuditLogException {

        if (message == null
                || message.trim().isEmpty()) {

            throw new AuditLogException(
                    "Log message cannot be empty");
        }

        try {

            FileWriter writer =
                    new FileWriter(logFileName, true);

            writer.write(message);
            writer.write(System.lineSeparator());

            writer.close();

            System.out.println(
                    "Log written successfully.");

        }
        catch (IOException e) {

            throw new AuditLogException(
                    "Unable to write to log file", e);
        }
    }


    /*
    Reads and displays the audit log.
    */

    public void readLog()
            throws AuditLogException {

        File file = new File(logFileName);

        if (!file.exists()) {

            throw new AuditLogException(
                    "Log file does not exist");
        }

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(logFileName));

            String line;

            System.out.println("AUDIT LOG");

            while ((line = reader.readLine()) != null) {

                System.out.println(line);
            }

            reader.close();

        }
        catch (IOException e) {

            throw new AuditLogException(
                    "Unable to read log file", e);
        }
    }


    /*
    Archives the current audit log.
    */

    public void archiveLog()
            throws AuditLogException {

        File logFile = new File(logFileName);

        if (!logFile.exists()) {

            throw new AuditLogException(
                    "Cannot archive. Log file does not exist");
        }

        try {

            File archiveFile =
                    new File(archiveFileName);

            Files.move(
                    logFile.toPath(),
                    archiveFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println(
                    "Log archived successfully.");

        }
        catch (IOException e) {

            throw new AuditLogException(
                    "Unable to archive log file", e);
        }
    }
}