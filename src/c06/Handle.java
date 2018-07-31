package c06;



import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Handle {
    {
        LOCAL_VARIABLE.put("SP", 0);
        LOCAL_VARIABLE.put("LCL", 1);
        LOCAL_VARIABLE.put("ARG", 2);
        LOCAL_VARIABLE.put("THIS", 3);
        LOCAL_VARIABLE.put("THAT", 4);
        LOCAL_VARIABLE.put("SCREEN", 16384);
        LOCAL_VARIABLE.put("KBD", 24576);
        LOCAL_VARIABLE.put("R0", 0);
        LOCAL_VARIABLE.put("R1", 1);
        LOCAL_VARIABLE.put("R2", 2);
        LOCAL_VARIABLE.put("R3", 3);
        LOCAL_VARIABLE.put("R4", 4);
        LOCAL_VARIABLE.put("R5", 5);
        LOCAL_VARIABLE.put("R6", 6);
        LOCAL_VARIABLE.put("R7", 7);
        LOCAL_VARIABLE.put("R8", 8);
        LOCAL_VARIABLE.put("R9", 9);
        LOCAL_VARIABLE.put("R10", 10);
        LOCAL_VARIABLE.put("R11", 11);
        LOCAL_VARIABLE.put("R12", 12);
        LOCAL_VARIABLE.put("R13", 13);
        LOCAL_VARIABLE.put("R14", 14);
        LOCAL_VARIABLE.put("R15", 15);


    }

    public static final Map<String, Integer> LOCAL_VARIABLE = new HashMap();
    public static final Map<String, Integer> LABEL = new HashMap();
    public static Integer LOCAL = 16;
    public static Integer index = 0;




    public boolean isNums(String code){
        char[] chars = code.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                continue;
            }
            return false;
        }
        return true;
    }
    public String num2binary(long l) {
        String res = "";
        while (l > 0) {
            if ((l & 1) == 0) {
                res = 0 + res;
            } else {
                res =1 + res;
            }
            l >>>= 1;
        }
        for (int i = res.length(); i < 16; i++) {
            res = 0 + res;
        }
        return res;
    }
    public void handleAsm(String line, FileWriter fileWriter) throws IOException {

        StringBuilder sb = new StringBuilder();
        if (line.startsWith("@")) {
            line = line.substring(1, line.length());
            if (isNums(line)) {
                sb.append(num2binary(Long.parseLong(line)));
                fileWriter.write(sb.toString() + "\n");
                index++;
                return;
            } else {

                Integer address = LABEL.get(line);
                if (address != null) {
                    sb.append(num2binary(address));
                    fileWriter.write(sb.toString() + "\n");
                    index++;
                    return;
                }
                address = LOCAL_VARIABLE.get(line);
                if (address == null) {
                    LOCAL_VARIABLE.put(line, LOCAL++);
                }
                address = LOCAL_VARIABLE.get(line);
                sb.append(num2binary(address));
                fileWriter.write(sb.toString() + "\n");
                index++;
                return;

            }


        } else if (line.startsWith("(")) {
            line = line.substring(1,line.length() - 1);
            LABEL.put(line, index);
            return;

        } else if (line.startsWith("D;")) {
            line = line.substring(2, line.length());
            sb.append("1110001100000");
            switch (line) {
                case "JGT":
                    sb.append("001");
                    break;
                case "JEQ":
                    sb.append("010");
                    break;
                case "JGE":
                    sb.append("011");
                    break;
                case "JLT":
                    sb.append("100");
                    break;
                case "JNE":
                    sb.append("101");
                    break;
                case "JLE":
                    sb.append("110");
                    break;
            }
            fileWriter.write(sb.toString() + "\n");
            index++;
            return;

        } else if (line.startsWith("0;JMP")) {
            sb.append("1110101010000111");
            fileWriter.write(sb.toString() + "\n");
            index++;
            return;

        } else {

            sb.append("111");
            String[] split = line.split("=");
            String dest = split[0];
            String comp = split[1];


            switch (comp) {
                case "0":
                    sb.append("0101010");
                    break;
                case "1":
                    sb.append("0111111");
                    break;
                case "-1":
                    sb.append("0111010");
                    break;
                case "D":
                    sb.append("0001100");

                    break;
                case "A":
                    sb.append("0110000");

                    break;
                case "!D":
                    sb.append("0001101");

                    break;
                case "!A":
                    sb.append("0110001");

                    break;
                case "-D":
                    sb.append("0001111");

                    break;
                case "-A":
                    sb.append("0110011");

                    break;
                case "D+1":
                    sb.append("0011111");

                    break;
                case "A+1":
                    sb.append("0110111");

                    break;
                case "D-1":
                    sb.append("0001110");

                    break;
                case "A-1":
                    sb.append("0110010");

                    break;
                case "D+A":
                    sb.append("0000010");

                    break;
                case "D-A":
                    sb.append("0010011");

                    break;
                case "A-D":
                    sb.append("0000111");

                    break;
                case "D&A":
                    sb.append("0000000");

                    break;
                case "D|A":
                    sb.append("0010101");

                    break;
                case "M":
                    sb.append("1110000");

                    break;

                case "!M":
                    sb.append("1110001");

                    break;

                case "-M":
                    sb.append("1110011");

                    break;

                case "M+1":
                    sb.append("1110111");

                    break;
                case "M-1":
                    sb.append("1110010");

                    break;
                case "D+M":
                    sb.append("1000010");

                    break;
                case "D-M":
                    sb.append("1010011");

                    break;
                case "M-D":
                    sb.append("1000111");

                    break;
                case "D&M":
                    sb.append("1000000");

                    break;
                case "D|M":
                    sb.append("1010101");

                    break;



            }
            switch (dest) {
                case "":
                    sb.append("000");
                    break;
                case "M":
                    sb.append("001");
                    break;
                case "D":
                    sb.append("010");
                    break;
                case "MD":
                    sb.append("011");
                    break;
                case "A":
                    sb.append("100");
                    break;
                case "AM":
                    sb.append("101");
                    break;
                case "AD":
                    sb.append("110");
                    break;
                case "AMD":
                    sb.append("111");
                    break;
            }
            sb.append("000");
            index++;
            fileWriter.write(sb.toString() + "\n");
            return;

        }





    }

    public static void main(String[] args) {
        Handle handle = new Handle();
        System.out.println( handle.num2binary(31));
    }


    public void init(Scanner code) {
        while (code.hasNextLine()) {

            String line = code.nextLine();
            if (line.startsWith("(")) {
                LABEL.put(line.substring(1, line.length() - 1), index);
                continue;
            }
            index++;

        }
        index = 0;
    }
    public void handleAsm(Scanner code, FileWriter fileWriter) throws IOException {
        while (code.hasNextLine()) {
            String line = code.nextLine();
            handleAsm(line, fileWriter);
        }


    }

}
