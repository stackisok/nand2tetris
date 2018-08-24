package c10;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import static c10.Keyword.*;
import static c10.Symbol.*;
import static c10.TokenType.*;


public class CompilationEngine {
    BufferedReader fileReader;
    String currentLine;
    int position;
    String token;
    TokenType token_type;

    boolean isComment;
    public static final Pattern digital = Pattern.compile("\\d+");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]+");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("\\w+");

    CompilationEngine(String fileName) throws FileNotFoundException {

        fileReader = new BufferedReader(new FileReader(fileName));

    }

    public void parseClass() {

        consumeKeyword(CLASS);
        consumeIdentifier(); // className
        consumeSymbol(LEFT_CURLY_BRACKET);
        while (!isNextTokenTheSymbol(RIGHT_CURLY_BRACKET)) {
            parseClassVarDec();
            parseSubroutine();
        }
        consumeSymbol(RIGHT_CURLY_BRACKET);


        while (hasMoreTokens()) {}
    }

    private boolean isNextTokenTheSymbol(Symbol symbol) {
        String nextToken = getNextToken();
        return nextToken.equals(symbol.name());

    }


    //获取下一个token
    private void moveNextToken() {

        for (int end = position + 1; end < currentLine.length(); end++) {
            String token_sub = currentLine.substring(position, end);
            if (isSymbol(token_sub)) {
                token = token_sub;
                token_type = SYMBOL;
                position = end;
                return ;
            }

            String firstTokenChar = String.valueOf(token_sub.charAt(0));
            String nextTokenChar = String.valueOf(currentLine.charAt(end + 1));

            //token_sub is identifier
            if (token_sub.matches(LETTER_PATTERN.pattern()) && !firstTokenChar.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    token_type = IDENTIFIER;
                    token = token_sub;
                    position = end;
                    return;
                }
                continue;

            }
            //token_sub is digital
            if (token_sub.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    token = token_sub;
                    position = end;
                    token_type = INT_CONST;
                    return;
                }
                continue;
            }

            //token_sub is digital
            if (token_sub.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    token = token_sub;
                    position = end;
                    token_type = INT_CONST;
                    return;
                }
                continue;
            }
            //token_sub is string
            if (firstTokenChar.equals("\"")) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    token = token_sub;
                    position = end;
                    token_type = STRING_CONST;
                    return;
                }
                continue;
            }
        }

    }

    private boolean isSymbolOrSpace (String letter){

        return letter.equals(" ") || isSymbol(letter);

    }

    void readNextLine() throws IOException {
        String line = fileReader.readLine().trim();

        if (line != null) {
            if (line.equals("")) {
                readNextLine();
                return;
            }
            if (isComment) {
                while (!line.contains("*/")) {
                    line = fileReader.readLine();
                }
                String s = line.replaceAll("\\w*\\*/", "").trim();
                isComment = false;
                if (s.equals("")) {
                    readNextLine();
                    return;
                } else {
                    currentLine = s;
                    position = 0;
                    return;
                }

            }
            while (line.startsWith("//")) {
                line = fileReader.readLine();
            }
            if (line.contains("/*")) {
                if (line.contains("*/")) {
                    currentLine = line.replaceAll("/\\*\\w*\\*/","").trim();
                    position = 0;

                    return;
                }
                isComment = true;
                currentLine = line.replaceAll("/\\*\\w*", "").trim();
                position = 0;
            }
        }

    }
    private String getNextToken() {

        for (int end = position + 1; end < currentLine.length(); end++) {
            String token_sub = currentLine.substring(position, end);
            if (isSymbol(token_sub)) {

                return token_sub;
            }

            String firstTokenChar = String.valueOf(token_sub.charAt(0));
            String nextTokenChar = String.valueOf(currentLine.charAt(end + 1));

            //token_sub is identifier
            if (token_sub.matches(LETTER_PATTERN.pattern()) && !firstTokenChar.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    return token_sub;
                }
                continue;

            }
            //token_sub is digital
            if (token_sub.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    return token_sub;
                }
                continue;
            }

            //token_sub is digital
            if (token_sub.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    return token_sub;
                }
                continue;
            }
            //token_sub is string
            if (firstTokenChar.equals("\"")) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    return token_sub;
                }
                continue;
            }
        }
        return "";
    }

    boolean isTokenOfType(TokenType type) {
        if (token_type == type) {
                return true;
        }
        return false;
    }
    boolean isSymbol(String token) {
        for (Symbol e : Symbol.values()) {
            if (e.character.equals(token))
                return true;
        }

        return false;
    }
    int scanSymbol(String token) {


        return 1;
    }
    private boolean hasMoreTokens() {

        return true;
    }
    void consumeSymbol(Symbol symbol) {
        moveNextToken();
        if (token.equals(symbol.toString())) {
            writeXmlElement("symbol", token);
        } else {
            throw new RuntimeException("the token is not expected");
        }
    }

    private void consumeIdentifier() {
        moveNextToken();
        writeXmlElement("identifier", token);

    }
    private void consumeSymbol() {

    }

    private void parseClassVarDec() {
        //todo classVarDec{

        String nextToken = getNextToken();
        if (isTokenInWords(nextToken, STATIC.toString(), FIELD.toString()))
        consumeKeyword(STATIC, FIELD);
        consumeType();
        consumeIdentifier();
        nextToken = getNextToken();
        while (!nextToken.equals(SEMICOLON.character)) {
            consumeSymbol(COMMA);
            consumeIdentifier();
        }
        consumeSymbol(SEMICOLON);

        //todo }

    }

    private void consumeType() {
        moveNextToken();
        if (isTokenOfType(KEYWORD)) {
            consumeKeyword(INT, CHAR, BOOLEAN);
        }
        else if (isTokenOfType(IDENTIFIER)) {
            consumeIdentifier();
        }
        else {
            throw new RuntimeException("The token of type is not expected!");
        }
    }

    boolean isTokenInWords (String next_token,String... words) {

        for (String word : words) {
            if (word.equals(next_token))
                return true;
        }

        throw new RuntimeException("the token not in such words");
    }
    private void parseSubroutine() {

    }
    //such like
    public void parseStatement() {

    }
    public void parseWhileStatement() {

    }
    public void parseIfStatement() {

    }
    public void parseStatementSequence() {

    }
    public void parseExpression() {

    }


    void consumeKeyword(Keyword keyword, Keyword... otherwords) {
        moveNextToken();
        if (token.equals(keyword.toString())) {
            writeXmlElement("keyword", token);
            return;
        }
        for (Keyword key : otherwords) {
            if (token.equals(keyword.toString())) {
                writeXmlElement("keyword", token);
                return;
            }
        }
        throw new RuntimeException("the token is not expected");


    }
    void writeXmlElement(String label, String content) {


    }
    public static void main(String[] args) {
        Pattern compile = Pattern.compile("\\d+");
        String l = "44500";
        boolean matches = l.matches(compile.pattern());
        System.out.println(matches);
        String s = "13213213131ddd/*sdas*/";
        String s1 = s.replaceAll("/\\*\\w*\\*/", "");
        System.out.println(s1);

    }

}
