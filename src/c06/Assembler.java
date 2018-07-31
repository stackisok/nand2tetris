package c06;
import java.io.*;
import java.util.Scanner;

public class Assembler {
    public static void main(String[] args) throws IOException {

        String fileName = args[0];
        File file = new File(fileName);
        String property = System.getProperty("user.dir");
        if (!file.isAbsolute()) {
            file = new File(property+ "\\" + file.getPath());
        }
        Assembler assembler = new Assembler();
        if (file.isDirectory()) {
            assembler.parseDirectory(fileName);
        } else {
            if (file.getName().endsWith(".asm")) {
                assembler.parseFile(file.getAbsolutePath());
            }
        }



    }
    public void parseDirectory(String directoryName) throws IOException {

        File directory = new File(directoryName);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                parseDirectory(file.getAbsolutePath());
            } else {
                if (file.getName().endsWith(".asm")) {
                    parseFile(file.getAbsolutePath());
                }
            }
        }
    }


    public void parseFile(String fileName) throws IOException {


        System.out.println("进来了" + fileName);
        StringBuilder sb = new StringBuilder();

        File file = new File(fileName);
        Scanner scan = new Scanner(file);


        while (scan.hasNextLine()) {
            String s = scan.nextLine();

            if (s.equals("") || s.startsWith("//")) {
                continue;
            }
            if (s.indexOf("//") > 0 )
                s = s.substring(0, s.indexOf("//")).trim();
            sb.append(s.trim() + "\n");
        }


        BufferedReader fr = new BufferedReader(new FileReader(file));
        File hack = new File(file.getAbsolutePath().replaceAll(".asm$", ".hack"));
        System.out.println(hack.getAbsolutePath());
        if (!hack.exists())
            hack.createNewFile();
        FileWriter fileWriter = new FileWriter(hack);

        Scanner code = new Scanner(sb.toString());
        Handle handle = new Handle();

        handle.init(code);
        code = new Scanner(sb.toString());
        handle.handleAsm(code, fileWriter);
        fileWriter.flush();

    }
}
