package documentProcessingFramework;

public class PDFDocumentProcessor implements DocumentProcessor {

    private String fileName;
    private double fileSize;

    PDFDocumentProcessor(String fileName, double fileSize)
            throws InvalidDocumentException {

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new InvalidDocumentException(
                    "PDF file name cannot be empty");
        }

        if (fileSize <= 0) {
            throw new InvalidDocumentException(
                    "PDF file size must be greater than 0");
        }

        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    @Override
    public void processDocument() {

        System.out.println(
                "Processing PDF document: " + fileName);
    }

    @Override
    public void displayDocumentDetails() {

        System.out.println("Document Type: PDF");
        System.out.println("File Name: " + fileName);
        System.out.println("File Size: " + fileSize + " MB");
    }
}