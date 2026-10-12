package com.company.jdbc.model;

/*
One rejected CSV row: where it was, what it contained, and why.
*/
public class ImportError {

    private final int lineNumber;
    private final String rawLine;
    private final String reason;

    public ImportError(int lineNumber, String rawLine, String reason) {
        this.lineNumber = lineNumber;
        this.rawLine = rawLine;
        this.reason = reason;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getRawLine() {
        return rawLine;
    }

    public String getReason() {
        return reason;
    }
}
