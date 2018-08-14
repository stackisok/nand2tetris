package c07;


import java.io.*;

/**
 * @Author: Wang Chenjun
 * @Description: SP
 *                LCL
 *                ARG
 *                THIS
 *                THAT
 * @Date: 11:53 2018/8/13
 *
 *
 */
public class Parser {

    BufferedReader bufferedReader;
    CodeWriter codeWriter;
    String currFileName ;
    int n = 0;
    //support absolutely path
    public Parser(String filePath) throws IOException {
        if (!filePath.endsWith(".vm")){
            throw new RuntimeException("文件名不对");
        }
        FileReader fileReader = new FileReader(filePath);
        bufferedReader = new BufferedReader(fileReader, 1024);
        File file = new File(filePath);
        currFileName = file.getName().replaceAll(".*/", "");
        String out = filePath.replaceAll(".vm$", ".asm");
        codeWriter = new CodeWriter(out);

    }

    void parse() throws IOException {
        String s = bufferedReader.readLine();
        while (s != null) {
            s = s.trim();
            System.out.println(s);
            StringBuilder sb = new StringBuilder();

            if (s.startsWith("//")) {   //skip comment
                s = bufferedReader.readLine();
                continue;
            }
            String[] split = s.split(" +");
            String commmand = split[0];
            String address = "";
            String position = "";
            if (split.length > 1)
                address = split[1];
            if (split.length > 2)
                position = split[2];


            if (split.length == 1) {
                switch (s) {
                    case "add": {
                        sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("A=A-1\n").append("M=D+M\n");
                        break;
                    }
                    case "sub": {
                        sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("A=A-1\n").append("M=M-D\n");
                        break;

                    }
                    case "neg": {
                        sb.append("@SP\n").append("A=M-1\n").append("M=-M\n");
                        break;
                    }
                    case "eq": {
                        n++;
                        sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("A=A-1\n").append("D=M-D\n").append("@EQ.true." + n + "\n" ).append("D;JEQ\n").
                                append("@SP\n").append("A=M-1\n").append("M=0\n").append("@EQ.after." + n + "\n").append( "0;JMP\n").append( "(EQ.true." + n + ")\n").
                                append("@SP\n").append("A=M-1\n").append("M=-1\n").append("(EQ.after." + n + ")\n");
                        break;
                    }
                    case "gt": {
                        n++;
                        sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("A=A-1\n").append("D=M-D\n").append("@GT.true." + n + "\n" ).append("D;JGT\n").
                                append("@SP\n").append("A=M-1\n").append("M=0\n").append("@GT.after." + n + "\n").append( "0;JMP\n").append( "(GT.true." + n + ")\n").
                                append("@SP\n").append("A=M-1\n").append("M=-1\n").append("(GT.after." + n + ")\n");
                        break;
                    }
                    case "lt": {
                        n++;
                        sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("A=A-1\n").append("D=M-D\n").append("@LT.true." + n + "\n" ).append("D;JLT\n").
                                append("@SP\n").append("A=M-1\n").append("M=0\n").append("@LT.after." + n + "\n").append( "0;JMP\n").append( "(LT.true." + n + ")\n").
                                append("@SP\n").append("A=M-1\n").append("M=-1\n").append("(LT.after." + n + ")\n");
                        break;
                    }
                    case "and": {
                        sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("A=A-1\n").append("M=D&M\n");
                        break;
                    }
                    case "or": {
                        sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("A=A-1\n").append("M=D|M\n");
                        break;
                    }
                    case "not": {
                        sb.append("@SP\n").append("A=M-1\n").append("M=!M\n");
                        break;
                    }
                }

            }else if (commmand.endsWith("push")) {


                switch (address) {
                    case "constant":
                        sb.append("@" + position + "\n").append("D=A\n");
                        break;
                    case "local":
                        sb.append("@LCL\n").append("D=M\n").append("@" + position + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "argument":
                        sb.append("@ARG\n").append("D=M\n").append("@" + position + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "this":
                        sb.append("@THIS\n").append("D=M\n").append("@" + position + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "that":
                        sb.append("@THAT\n").append("D=M\n").append("@" + position + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "temp":
                        sb.append("@R5\n").append("D=A\n").append("@" + position + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "pointer":
                        if (position.equals("0"))
                            sb.append("@THIS\n").append("D=M\n");
                        else
                            sb.append("@THAT\n").append("D=M\n");
                        break;
                    case "static":
                        sb.append("@" + currFileName + "." + position + "\n").append("D=M\n");
                        break;
                    default:

                        throw new RuntimeException("Unkonw type,can not parse!");
                }
                sb.append("@SP\n").append("A=M\n").append("M=D\n").append("@SP\n").append("M=M+1\n");

            } else if (commmand.equals("pop")) {


                switch (address) {
                    case "local":
                        sb.append("@LCL\n").append("D=M\n").append("@" + position + "\n").append("D=D+A\n");
                        break;
                    case "argument":
                        sb.append("@ARG\n").append("D=M\n").append("@" + position + "\n").append("D=D+A\n");
                        break;
                    case "this":
                        sb.append("@THIS\n").append("D=M\n").append("@" + position + "\n").append("D=D+A\n");
                        break;
                    case "that":
                        sb.append("@THAT\n").append("D=M\n").append("@" + position + "\n").append("D=D+A\n");
                        break;
                    case "temp":
                        sb.append("@R5\n").append("D=M\n").append("@" + position + "\n").append("D=D+A\n");
                        break;
                    case "pointer":
                        if (position.equals("0"))
                            sb.append("@THIS\n").append("D=A\n");
                        else
                            sb.append("@THAT\n").append("D=A\n");
                        break;
                    case "static":
                        sb.append("@" + currFileName + "." + position + "\n").append("D=M\n");
                        break;
                    default:

                        throw new RuntimeException("Unkonw type,can not parse!");
                }
                sb.append("@R13\n").append("M=D\n").append("@SP\n").append("AM=M-1\n").append("D=M\n").append("@R13\n").append("A=M\n").append("M=D\n");

            }
            codeWriter.write(sb.toString());
            s = bufferedReader.readLine();


        }


    }

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        File file = new File(fileName);
        String property = System.getProperty("user.dir");
        if (!file.isAbsolute()) {
            file = new File(property+ "\\" + file.getPath());
        }
        if (file.isDirectory()) {
            parseDirectory(fileName);

        } else {
            if (file.getName().endsWith(".vm")) {
                new Parser(file.getPath()).parse();
            }
        }

    }
    static void parseDirectory(String fileName) throws IOException {
        File directory = new File(fileName);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                parseDirectory(file.getAbsolutePath());
            } else {
                if (file.getName().endsWith(".vm")) {
                    new Parser(file.getPath()).parse();
                }
            }
        }
    }


}
