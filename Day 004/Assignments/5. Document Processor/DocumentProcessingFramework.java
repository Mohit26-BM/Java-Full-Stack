package documentProcessingFramework;

public class DocumentProcessingFramework {

    public static void main(String[] args) {

        try {

            /*
            Creating different document processors.
            */

            DocumentProcessor pdfDocument =
                    new PDFDocumentProcessor(
                            "Report.pdf",
                            5.5
                    );

            DocumentProcessor wordDocument =
                    new WordDocumentProcessor(
                            "Assignment.docx",
                            2.5
                    );

            DocumentProcessor textDocument =
                    new TextDocumentProcessor(
                            "Notes.txt",
                            1.2
                    );


            /*
            Runtime Polymorphism
            The same DocumentProcessor reference
            can refer to different implementations.
            */

            System.out.println("PDF DOCUMENT");

            pdfDocument.displayDocumentDetails();
            pdfDocument.processDocument();

            System.out.println("-------------------------");


            System.out.println();
            System.out.println("WORD DOCUMENT");

            wordDocument.displayDocumentDetails();
            wordDocument.processDocument();

            System.out.println("-------------------------");


            System.out.println();
            System.out.println("TEXT DOCUMENT");

            textDocument.displayDocumentDetails();
            textDocument.processDocument();

            System.out.println("-------------------------");


            /*
            Invalid input test
            */

            System.out.println();
            System.out.println("INVALID DOCUMENT TEST");

            DocumentProcessor invalidDocument =
                    new PDFDocumentProcessor(
                            "",
                            5
                    );

        }
        catch (InvalidDocumentException e) {

            System.out.println(
                    "Error: " + e.getMessage());
        }
    }
}