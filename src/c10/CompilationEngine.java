package c10;

import java.io.BufferedReader;
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
    TokenType next_token_type;

    boolean isComment;
    public static final Pattern digital = Pattern.compile("\\d+");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]+");
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("\\w+");

    CompilationEngine(String fileName) throws IOException {

        fileReader = new BufferedReader(new FileReader(fileName));
        readNextLine();

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


//        while (hasMoreTokens()) {}
    }

    private boolean isNextTokenTheSymbol(Symbol symbol) {
        String nextToken = getNextToken();
        return nextToken.equals(symbol.name());

    }


    //获取下一个token
    private void moveNextToken() {

        while (position == currentLine.length()) {
            try {
                readNextLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int end = position + 1; end < currentLine.length() + 1; end++) {
            String token_sub = currentLine.substring(position, end);
            if (token_sub.equals(" ")) {
                position = position + 1;
                continue;
            }
            if (isSymbol(token_sub)) {
                token = token_sub;
                token_type = SYMBOL;
                position = end;
                return ;
            }

            String firstTokenChar = String.valueOf(token_sub.charAt(0));
            String nextTokenChar = String.valueOf(currentLine.charAt(end));

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
                String s = line.replaceAll(".*\\*/", "").trim();
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
                    currentLine = line.replaceAll("/\\*.*\\*/","").trim();
                    position = 0;

                    return;
                }
                isComment = true;
                currentLine = line.replaceAll("/\\*.*", "").trim();
                position = 0;
                return;
            }
            currentLine = line.trim();
            position = 0;
        }

    }
    private String getNextToken() {

        while (position == currentLine.length()) {
            try {
                readNextLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int end = position + 1; end < currentLine.length() + 1; end++) {

            String token_sub = currentLine.substring(position, end);
            if (token_sub.equals(" ")) {
                position = position + 1;
                continue;
            }
            if (isSymbol(token_sub)) {

                next_token_type = SYMBOL;
                return token_sub;
            }

            String firstTokenChar = String.valueOf(token_sub.charAt(0));
            String nextTokenChar = String.valueOf(currentLine.charAt(end));

            //token_sub is identifier
            if (token_sub.matches(LETTER_PATTERN.pattern()) && !firstTokenChar.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    next_token_type = IDENTIFIER;;

                    return token_sub;
                }
                continue;

            }
            //token_sub is digital
            if (token_sub.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    next_token_type = INT_CONST;
                    return token_sub;
                }
                continue;
            }

            //token_sub is digital
            if (token_sub.matches(digital.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    next_token_type = INT_CONST;
                    return token_sub;
                }
                continue;
            }
            //token_sub is string
            if (firstTokenChar.equals("\"")) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    next_token_type = STRING_CONST;
                    return token_sub;
                }
                continue;
            }
        }
        return "";
    }

    boolean isNextTokenOfType(TokenType type) {
        if (next_token_type == type) {
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
//    private boolean hasMoreTokens() {
//
//        return true;
//    }
    void consumeSymbol(Symbol symbol) {
        moveNextToken();
        if (token.equals(symbol.character)) {
            writeXmlElement("symbol", token);
        } else {
            throw new RuntimeException(symbol + " the token is not expected" + token);
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
        if (isNextTokenInWords(nextToken, STATIC.toString(), FIELD.toString())) {


            consumeKeyword(STATIC, FIELD);
            consumeType();
            consumeIdentifier();
            nextToken = getNextToken();
            while (!nextToken.equals(SEMICOLON.character)) {
                consumeSymbol(COMMA);
                consumeIdentifier();
            }
            consumeSymbol(SEMICOLON);
        }

        //todo }

    }

    private void consumeType() {
        String nextToken = getNextToken();
        if (isNextTokenOfType(KEYWORD)) {
            consumeKeyword(INT, CHAR, BOOLEAN);
        }
        else if (isNextTokenOfType(IDENTIFIER)) {
            consumeIdentifier();
        }
        else {
            throw new RuntimeException("The token of type is not expected!");
        }
    }

    boolean isNextTokenInWords(String next_token, String... words) {

        for (String word : words) {
            if (word.equals(next_token))
                return true;
        }

        return false;
//        throw new RuntimeException("the token not in such words");
    }
    private void parseSubroutine() {
        //todo <>
        String nextToken = getNextToken();

        if (isNextTokenInWords(nextToken, CONSTRUCTOR.toString(), METHOD.toString(), FUNCTION.toString())) {
            consumeKeyword(CONSTRUCTOR, FUNCTION, METHOD);

        }
        if (!nextToken.equals(CONSTRUCTOR.toString()))
            consumeType();
        consumeIdentifier();
        consumeSymbol(LEFT_PARENTHESIS);
        parseParams();
        consumeSymbol(RIGHT_PARENTHESIS);
        parseSubroutineBody();
        //todo </>

    }

    private void parseSubroutineBody() {
        consumeSymbol(LEFT_CURLY_BRACKET);
        String nextToken = getNextToken();

        while (isNextTokenInWords(nextToken, VAR.toString())) {
            parseVar();
            nextToken = getNextToken();

        }
        parseStatement();
        consumeSymbol(RIGHT_CURLY_BRACKET);

    }

    private void parseVar() {
        consumeKeyword(VAR);
        consumeType();
        consumeIdentifier();
        while (isNextTokenTheSymbol(COMMA)) {
            consumeSymbol(COMMA);
            consumeIdentifier(); // varName
        }
        consumeSymbol(SEMICOLON);

    }

    private void parseParams() {

    }

    //such like
    public void parseStatement() {
        while (true) {
            String nextToken = getNextToken();
            if (isNextTokenInWords(nextToken, LET.toString())) {
                parseLet();
            }
            else if (isNextTokenInWords(nextToken, IF.toString())) {
                parseIf();
            }
            else if (isNextTokenInWords(nextToken, WHILE.toString())) {
                parseWhile();
            }
            else if (isNextTokenInWords(nextToken, DO.toString())) {
                parseDo();
            }
            else if (isNextTokenInWords(nextToken, RETURN.toString())) {
                parseReturn();
            }
            else {
                break;
            }
            nextToken = getNextToken();
        }

    }

    private void parseLet() {
        consumeKeyword(LET);
        consumeIdentifier();
        if (isNextTokenTheSymbol(LEFT_SQUARE_BRACKET)) {
            consumeSymbol(LEFT_SQUARE_BRACKET);
            parseExpression();
            consumeSymbol(RIGHT_SQUARE_BRACKET);
        }
        consumeSymbol(EQUAL);
        parseExpression();
        consumeSymbol(SEMICOLON);

    }

    private void parseReturn() {

    }

    private void parseDo() {
        consumeKeyword(DO);
        consumeIdentifier();
        consumeSymbol(DOT);
        consumeIdentifier();
        consumeSymbol(SEMICOLON);
    }

    public void parseWhile() {
        consumeKeyword(WHILE);
        consumeSymbol(LEFT_PARENTHESIS);
        parseExpression();
        consumeSymbol(RIGHT_PARENTHESIS);
        consumeSymbol(LEFT_CURLY_BRACKET);

        consumeSymbol(RIGHT_CURLY_BRACKET);




    }
    public void parseIf() {
        consumeKeyword(IF);
        consumeSymbol(LEFT_PARENTHESIS);
        parseExpression();
        consumeSymbol(RIGHT_PARENTHESIS);

//        consumeStatementBlock();
        String nextToken = getNextToken();
        if (isNextTokenInWords(nextToken,ELSE.toString())) {
            consumeKeyword(ELSE);
//            consumeStatementBlock();
        }

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
        String expectWords = "";
        for (Keyword key : otherwords) {
            expectWords += "," + key;
            if (token.equals(key.toString())) {
                writeXmlElement("keyword", token);
                return;
            }
        }

        throw new RuntimeException(keyword + " the token is not expected" + "(" + expectWords +")");


    }
    void writeXmlElement(String label, String content) {


    }
    public static void main(String[] args) throws IOException {
//        Pattern compile = Pattern.compile("\\d+");
//        String l = "44500";
//        boolean matches = l.matches(compile.pattern());
//        System.out.println(matches);
//        String s = "13213213131ddd/*sdas*/";
//        String s1 = s.replaceAll("/\\*\\w*\\*/", "");
//        System.out.println(s1);

        CompilationEngine compilationEngine = new CompilationEngine("E:\\nand2tetris\\nand2tetris-master\\nand2tetris-master\\projects\\10\\Square\\1\\Main.jack");
        compilationEngine.parseClass();
    }

}
