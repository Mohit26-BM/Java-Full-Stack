package com.company.wallet.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.company.wallet.exception.ValidationException;
import com.company.wallet.model.BulkCredit;

/*
Reads a bulk credit CSV:

    wallet_number,amount,description
    WAL0000001,250.00,Festival cashback

The header line and blank lines are skipped. The description may
contain commas (the line is split into at most 3 parts).
Only the FORMAT is checked here; business rules are checked by
WalletService.bulkCredit().
*/
public final class BulkCreditFileReader {

    private static final String SAMPLE_FILE = "bulk-credits.csv";

    private BulkCreditFileReader() {
    }

    /*
    filePath = null reads the bundled sample (resources/bulk-credits.csv).
    */
    public static List<BulkCredit> read(String filePath) throws ValidationException {

        List<BulkCredit> credits = new ArrayList<>();
        List<String> problems = new ArrayList<>();

        try (InputStream input = open(filePath);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;
                String trimmed = line.trim();

                if (trimmed.isEmpty()
                        || (lineNumber == 1 && trimmed.toLowerCase().startsWith("wallet_number"))) {
                    continue;
                }

                String[] parts = trimmed.split(",", 3);

                if (parts.length < 2) {
                    problems.add("Line " + lineNumber + ": expected wallet_number,amount[,description]");
                    continue;
                }

                try {

                    credits.add(new BulkCredit(
                            lineNumber,
                            parts[0].trim(),
                            new BigDecimal(parts[1].trim()),
                            parts.length == 3 ? parts[2].trim() : null));

                } catch (NumberFormatException e) {

                    problems.add("Line " + lineNumber + ": amount '" + parts[1].trim()
                            + "' is not a number");
                }
            }

        } catch (IOException e) {

            throw new ValidationException("Cannot read bulk credit file: " + e.getMessage());
        }

        if (!problems.isEmpty()) {
            throw new ValidationException("Bulk credit file has errors, nothing was credited:\n  "
                    + String.join("\n  ", problems));
        }

        return credits;
    }

    private static InputStream open(String filePath) throws IOException {

        if (filePath != null) {
            return new FileInputStream(filePath);
        }

        InputStream sample =
                BulkCreditFileReader.class.getClassLoader().getResourceAsStream(SAMPLE_FILE);

        if (sample == null) {
            throw new IOException(SAMPLE_FILE + " not found on the classpath");
        }

        return sample;
    }
}
