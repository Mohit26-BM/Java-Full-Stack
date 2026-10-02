package documentProcessingFramework;

public class WordDocumentProcessor implements DocumentProcessor {

    private String fileName;
    private double fileSize;

    WordDocumentProcessor(String fileName, double fileSize)
            throws InvalidDocumentException {

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new InvalidDocumentException(
                    "Word file name cannot be empty");
        }

        if (fileSize <= 0) {
            throw new InvalidDocumentException(
                    "Word file size must be greater than 0");
        }

        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    @Override
    public void processDocument() {

        System.out.println(
                "Processing Word document: " + fileName);
    }

    @Override
    public void displayDocumentDetails() {

        System.out.println("Document Type: Word");
        System.out.println("File Name: " + fileName);
        System.out.println("File Size: " + fileSize + " MB");
    }
}