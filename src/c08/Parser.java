package c08;


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
    static int n = 0;
    String funcName;
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
            s = s.replaceAll("//.*$", "").trim();
            System.out.println(s);
            String[] split = s.split(" +");
            String commmand = split[0];
            System.out.println(commmand);
            String arg1 = "";
            String arg2 = "";
            if (split.length > 1)
                arg1 = split[1];
            if (split.length > 2)
                arg2 = split[2];


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
                                append("@SP\n").append("A=M-1\n").append("M=0\n").append("@LT.after." + n + "\n").append( "0;JMP\n").append("(LT.true." + n + ")\n").
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
                    case "return": {
                        sb.append("@LCL\n").append("D=M\n").append("@5\n").append("A=D-A\n").append("D=M\n").append("@R13\n").append("M=D\n"). //save return address *(LCL - 5) -> R13
                           append("@SP\n").append("A=M-1\n").append("D=M\n").append("@ARG\n").append("A=M\n").append("M=D \n").     // the value will return  *(SP - 1) -> *ARG
                           append("D=A+1\n").append("@SP\n" ).append("M=D\n"). // ARG + 1 -> SP
                           append("@LCL\n").append("AM=M-1\n").append("D=M\n").append("@THAT\n").append("M=D\n"). // the 1st is that *(LCL - 1) -> THAT; LCL--
                           append("@LCL\n").append("AM=M-1\n").append("D=M\n").append("@THIS\n").append("M=D\n").// the second is this *(LCL - 1) -> THIS; LCL--
                           append("@LCL\n").append("AM=M-1\n").append("D=M\n").append("@ARG\n").append("M=D\n").// the third is arg *(LCL - 1) -> ARG; LCL--
                           append("@LCL\n").append("A=M-1\n").append("D=M\n").append("@LCL\n").append("M=D\n").// the fourth is LCL *(LCL - 1) -> LCL
                           append("@R13\n").append("A=M\n").append("0;JMP\n");  // Back to the place where he came from  R13 -> A
                        break;
                    }
                }

            }else if (commmand.endsWith("push")) {


                switch (arg1) {
                    case "constant":
                        sb.append("@" + arg2 + "\n").append("D=A\n");
                        break;
                    case "local":
                        sb.append("@LCL\n").append("D=M\n").append("@" + arg2 + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "argument":
                        sb.append("@ARG\n").append("D=M\n").append("@" + arg2 + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "this":
                        sb.append("@THIS\n").append("D=M\n").append("@" + arg2 + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "that":
                        sb.append("@THAT\n").append("D=M\n").append("@" + arg2 + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "temp":
                        sb.append("@R5\n").append("D=A\n").append("@" + arg2 + "\n").append("A=D+A\n").append("D=M\n");
                        break;
                    case "pointer":
                        if (arg2.equals("0"))
                            sb.append("@THIS\n").append("D=M\n");
                        else
                            sb.append("@THAT\n").append("D=M\n");
                        break;
                    case "static":
                        sb.append("@" + currFileName + "." + arg2 + "\n").append("D=M\n");
                        break;
                    default:

                        throw new RuntimeException("Unkonw type,can not parse!");
                }
                sb.append("@SP\n").append("A=M\n").append("M=D\n").append("@SP\n").append("M=M+1\n");

            } else if (commmand.equals("pop")) {


                switch (arg1) {
                    case "local":
                        sb.append("@LCL\n").append("D=M\n").append("@" + arg2 + "\n").append("D=D+A\n");
                        break;
                    case "argument":
                        sb.append("@ARG\n").append("D=M\n").append("@" + arg2 + "\n").append("D=D+A\n");
                        break;
                    case "this":
                        sb.append("@THIS\n").append("D=M\n").append("@" + arg2 + "\n").append("D=D+A\n");
                        break;
                    case "that":
                        sb.append("@THAT\n").append("D=M\n").append("@" + arg2 + "\n").append("D=D+A\n");
                        break;
                    case "temp":
                        sb.append("@R5\n").append("D=A\n").append("@" + arg2 + "\n").append("D=D+A\n");
                        break;
                    case "pointer":
                        if (arg2.equals("0"))
                            sb.append("@THIS\n").append("D=A\n");
                        else
                            sb.append("@THAT\n").append("D=A\n");
                        break;
                    case "static":
                        sb.append("@" + currFileName + "." + arg2 + "\n").append("D=A\n");
                        break;
                    default:

                        throw new RuntimeException("Unkonw type,can not parse!");
                }
                sb.append("@R13\n").append("M=D\n").append("@SP\n").append("AM=M-1\n").append("D=M\n").append("@R13\n").append("A=M\n").append("M=D\n");

            } else if (commmand.equals("if-goto")){

                sb.append("@SP\n").append("AM=M-1\n").append("D=M\n").append("@" + funcName + "$" + arg1 + "\n").append("D;JNE\n");

            } else if (commmand.equals("goto")) {
                sb.append("@" + funcName + "$" + arg1 + "\n").append("0;JMP\n");

            } else if (commmand.equals("function")) {
                funcName = arg1;
                sb.append( "(" + arg1 + ")\n").append("@SP\n").append("A=M\n");
                for (int i = 0; i < Integer.parseInt(arg2); i++) {
                    sb.append("M=0\n").append("A=A+1\n");
                }
                sb.append("D=A\n").append("@SP\n").append("M=D\n");

            } else if (commmand.equals("label")) {
                sb.append("(" + funcName + "$" + arg1 + ")\n");

            } else if (commmand.equals("call")) {
                n++;
                sb.append("@SP\n").append("D=M\n").append("@R13\n").append("M=D\n").             // SP -> R13
                   append("@RET." + n + "\n").append("D=A\n").append("@SP\n").append("A=M\n").append("M=D\n").               // @RET -> *SP
                   append("@SP\n").append("M=M+1\n"). // SP++
                   append("@LCL\n").append("D=M\n" ).append("@SP\n").append("A=M\n").append("M=D\n").                    // LCL -> *SP
                   append("@SP\n").append("M=M+1\n").                    // SP++
                   append("@ARG\n").append("D=M\n").append("@SP\n").append("A=M\n").append("M=D\n").// ARG -> *SP
                   append("@SP\n").append("M=M+1\n").// SP++
                   append("@THIS\n").append("D=M\n").append("@SP\n").append("A=M\n").append("M=D\n").// THIS -> *SP
                   append("@SP\n").append("M=M+1\n").// SP++
                   append("@THAT\n").append("D=M\n").append("@SP\n").append("A=M\n").append("M=D\n").// THAT -> *SP
                   append("@SP\n").append("M=M+1\n").// SP++
                   append("@R13\n").append("D=M\n").append("@" + arg2 + "\n").append("D=D-A\n").append("@ARG\n").append("M=D\n"). // R13 - n -> ARG
                   append("@SP\n").append("D=M\n").append("@LCL\n").append("M=D\n").append( "@" + arg1 + "\n").append("0;JMP\n").append("(RET." + n + ")\n");// SP -> LCL
            }


            codeWriter.write(sb.toString());
            s = bufferedReader.readLine();


        }


    }

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        File file = new File(fileName);
        System.out.println(fileName + file.toPath().getFileName().toString() + ".asm");
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

        if (!file.isAbsolute()) {
            file = new File(property+ "\\" + file.getPath());
        }
        if (file.isDirectory()) {
            integrateDirectory(fileName);

        } else {
            if (file.getName().endsWith(".vm")) {
                new Parser(file.getPath()).parse();
            }
        }

    }
    static void integrateDirectory(String fileName) throws IOException {
        File file = new File(fileName);
        File[] files = file.listFiles();
        FileWriter fileWriter = null;
        for (File f : files) {
            if (f.isDirectory()) {
                integrateDirectory(f.getAbsolutePath());
            } else if (f.getName().endsWith(".asm")) {

                if (fileWriter == null) {

                    fileWriter = new FileWriter(fileName + "\\" + file.toPath().getFileName().toString() + ".asm");
                    n++;
                    StringBuilder sb = new StringBuilder();
                    sb.append("@SP\n").append("D=M\n").append("@R13\n").append("M=D\n").             // SP -> R13
                            append("@RET." + n + "\n").append("D=A\n").append("@SP\n").append("A=M\n").append("M=D\n").               // @RET -> *SP
                            append("@SP\n").append("M=M+1\n"). // SP++
                            append("@LCL\n").append("D=M\n" ).append("@SP\n").append("A=M\n").append("M=D\n").                    // LCL -> *SP
                            append("@SP\n").append("M=M+1\n").                    // SP++
                            append("@ARG\n").append("D=M\n").append("@SP\n").append("A=M\n").append("M=D\n").// ARG -> *SP
                            append("@SP\n").append("M=M+1\n").// SP++
                            append("@THIS\n").append("D=M\n").append("@SP\n").append("A=M\n").append("M=D\n").// THIS -> *SP
                            append("@SP\n").append("M=M+1\n").// SP++
                            append("@THAT\n").append("D=M\n").append("@SP\n").append("A=M\n").append("M=D\n").// THAT -> *SP
                            append("@SP\n").append("M=M+1\n").// SP++
                            append("@R13\n").append("D=M\n").append("@" + 0 + "\n").append("D=D-A\n").append("@ARG\n").append("M=D\n"). // R13 - n -> ARG
                            append("@SP\n").append("D=M\n").append("@LCL\n").append("M=D\n").append( "@" + "Sys.init" + "\n").append("0;JMP\n").append("(RET." + n + ")\n");
                    fileWriter.write( "@256\n" +
                            "D=A\n" +
                            "@SP\n" +
                            "M=D\n" +
                            sb.toString() +
                            "0;JMP\n");
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        fileWriter.write(line + "\n");
                    }
                    fileWriter.flush();
                } else {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        fileWriter.write(line + "\n");
                    }
                    fileWriter.flush();
                }
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
