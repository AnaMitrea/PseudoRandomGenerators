package file_handling;

import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {
    String fileName;

    public WriteFile(String fileName) {
        this.fileName = fileName;
    }

    public void printToFile(String text) {
        try {
            FileWriter myWriter = new FileWriter(this.fileName);
            myWriter.write(text);
            myWriter.close();
            System.out.println("Successfully wrote to the file.\n");
        } catch (IOException e) {
            System.out.println("An error occurred.\n");
            e.printStackTrace();
        }
    }

}