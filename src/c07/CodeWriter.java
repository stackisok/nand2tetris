package c07;

import java.io.*;

public class CodeWriter {

    FileWriter fileWriter;
    public CodeWriter(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        fileWriter = new FileWriter(file);
    }
    void write(String str) throws IOException {

        fileWriter.write(str);
        fileWriter.flush();
    }
}
