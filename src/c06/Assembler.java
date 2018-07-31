package c06;
import java.io.*;
import java.net.URL;
import java.util.Scanner;

public class Assembler {
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        URL resource = Assembler.class.getResource("");

        StringBuilder sb = new StringBuilder();

        File file = new File(resource.getPath() + fileName);
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

        File hack = new File(resource.getPath() + fileName.split("\\.")[0] + ".hack");
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
