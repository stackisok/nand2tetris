package c06;
import java.io.*;
import java.net.URL;

public class Assembler {
    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        URL resource = Assembler.class.getResource("");


        File file = new File(resource.getPath() + fileName);
        BufferedReader fr = new BufferedReader(new FileReader(file));

        File hack = new File(resource.getPath() + fileName.split("\\.")[0] + ".hack");
        if (!hack.exists())
            hack.createNewFile();
        FileWriter fileWriter = new FileWriter(hack);
        String line = null;
        Handle handle = new Handle();
        while ((line = fr.readLine()) != null) {
            line = line.trim();
            if (line.equals("") || line.startsWith("//")) {
                continue;
            }
            fileWriter.write(line+"\n");
            System.out.println(line);

        }
        fileWriter.flush();


    }
}
