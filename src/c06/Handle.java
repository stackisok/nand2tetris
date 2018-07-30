package c06;

import java.io.FileWriter;

public class Handle {

    public static final Integer SCRENN_ADDRESS = 16384;
    public static final Integer KEYBOARD_ADDRESS = 24576;

    public void handleAsm(String line, FileWriter fileWriter) {
        if (line.startsWith("@")) {
            line = line.substring(1, line.length());


        }
    }
}
