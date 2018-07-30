package c06;



import java.io.FileWriter;

public class Handle {

    public static final Integer SCRENN_ADDRESS = 16384;
    public static final Integer KEYBOARD_ADDRESS = 24576;

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
            if (l % 2 == 0) {
                res = 0 + res;
            } else {
                res =1 + res;
            }
            l /= 2;
        }
        for (int i = res.length(); i < 16; i++) {
            res = 0 + res;
        }
        return res;
    }
    public void handleAsm(String line, FileWriter fileWriter) {

        StringBuilder sb = new StringBuilder();
        if (line.startsWith("@")) {
            line = line.substring(1, line.length());
            if (isNums(line)) {
                sb.append(Long.parseLong(line));
            }





        }
    }

    public static void main(String[] args) {
        Handle handle = new Handle();
        System.out.println( handle.num2binary(2));
    }


}
