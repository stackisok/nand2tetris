package c10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    private static Set<Symbol> UNARY_OPERATORS = Stream.of(MINUS, NOT).collect(Collectors.toSet());
    boolean isComment;
    public static final Pattern DIGITAL = Pattern.compile("\\d+");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]+");

    CompilationEngine(String fileName) throws IOException {

        fileReader = new BufferedReader(new FileReader(fileName));
        readNextLine();

    }

    public void parseClass() {

        writeBegXmlElement("class");
        consumeKeyword(CLASS);
        consumeIdentifier(); // className
        consumeSymbol(LEFT_CURLY_BRACKET);
        while (!isNextTokenTheSymbol(RIGHT_CURLY_BRACKET)) {
            parseClassVarDec();
            parseSubroutine();
        }
        consumeSymbol(RIGHT_CURLY_BRACKET);
        writeEndXmlElement("class");



//        while (hasMoreTokens()) {}
    }

    private boolean isNextTokenTheSymbol(Symbol symbol) {
        String nextToken = getNextToken();
        return nextToken.equals(symbol.character);

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
            if (token_sub.matches(LETTER_PATTERN.pattern()) && !firstTokenChar.matches(DIGITAL.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    if (KEYWORDS.get(token_sub) != null) {
                        token_type = KEYWORD;
                        token = token_sub;
                        position = end;
                        return ;
                    }
                    token_type = IDENTIFIER;
                    token = token_sub;
                    position = end;
                    return;
                }
                continue;

            }
            //token_sub is DIGITAL
            if (token_sub.matches(DIGITAL.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    token = token_sub;
                    position = end;
                    token_type = INT_CONST;
                    return;
                }
                continue;
            }

            //token_sub is DIGITAL
            if (token_sub.matches(DIGITAL.pattern())) {
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
            if (token_sub.matches(LETTER_PATTERN.pattern()) && !firstTokenChar.matches(DIGITAL.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    if (KEYWORDS.get(token_sub) != null) {
                        next_token_type = KEYWORD;

                        return token_sub;
                    }
                    next_token_type = IDENTIFIER;

                    return token_sub;
                }
                continue;

            }
            //token_sub is DIGITAL
            if (token_sub.matches(DIGITAL.pattern())) {
                if (isSymbolOrSpace(nextTokenChar)) {
                    next_token_type = INT_CONST;
                    return token_sub;
                }
                continue;
            }

            //token_sub is DIGITAL
            if (token_sub.matches(DIGITAL.pattern())) {
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

    private String consumeIdentifier() {
        moveNextToken();
        writeXmlElement("identifier", token);
        return token;

    }
    private void consumeSymbol() {

    }

    private void parseClassVarDec() {

        String nextToken = getNextToken();
        if (isNextTokenInWords(nextToken, STATIC.toString(), FIELD.toString())) {
            writeBegXmlElement("classVarDec");


            consumeKeyword(STATIC, FIELD);
            consumeType();
            consumeIdentifier();
            nextToken = getNextToken();
            while (!nextToken.equals(SEMICOLON.character)) {
                consumeSymbol(COMMA);
                consumeIdentifier();
                nextToken = getNextToken();

            }
            consumeSymbol(SEMICOLON);
            writeEndXmlElement("classVarDec");

        }


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
        String nextToken = getNextToken();
        if (!isNextTokenInWords(nextToken, CONSTRUCTOR.toString(), FUNCTION.toString(), METHOD.toString())) {
            return;
        }
        writeBegXmlElement("subroutineDec");


        if (isNextTokenInWords(nextToken, CONSTRUCTOR.toString(), METHOD.toString(), FUNCTION.toString())) {
            consumeKeyword(CONSTRUCTOR, FUNCTION, METHOD);

        }
//        if (!nextToken.equals(CONSTRUCTOR.toString())) {
//
//
//        }
        nextToken = getNextToken();
        if (nextToken.equals("void"))
            consumeKeyword(VOID);

        else
            consumeType();
        consumeIdentifier();
        consumeSymbol(LEFT_PARENTHESIS);
        parseParams();
        consumeSymbol(RIGHT_PARENTHESIS);
        parseSubroutineBody();
        writeEndXmlElement("subroutineDec");

    }

    private void parseSubroutineBody() {
        writeBegXmlElement("subroutineBody");

        consumeSymbol(LEFT_CURLY_BRACKET);
        String nextToken = getNextToken();

        while (isNextTokenInWords(nextToken, VAR.toString())) {
            parseVar();
            nextToken = getNextToken();

        }
        parseStatement();
        consumeSymbol(RIGHT_CURLY_BRACKET);
        writeEndXmlElement("subroutineBody");

    }

    private void parseVar() {
        writeBegXmlElement("varDec");

        consumeKeyword(VAR);
        consumeType();
        consumeIdentifier();
        while (isNextTokenTheSymbol(COMMA)) {
            consumeSymbol(COMMA);
            consumeIdentifier(); // varName
        }
        consumeSymbol(SEMICOLON);
        writeEndXmlElement("varDec");


    }

    private void parseParams() {
        writeBegXmlElement("parameterList");
        String nextToken = getNextToken();
        if (!isNextTokenInWords(nextToken, INT.toString(), CHAR.toString(), BOOLEAN.toString()) && !isNextTokenOfType(IDENTIFIER)) {
            writeEndXmlElement("parameterList");
            return;
        }
        consumeType();
        consumeIdentifier(); // varName
        while (isNextTokenTheSymbol(COMMA)) {
            consumeSymbol(COMMA);
            consumeType();
            consumeIdentifier(); // varName
        }
        writeEndXmlElement("parameterList");
    }

    //such like
    public void parseStatement() {
        writeBegXmlElement("statements");

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
        writeEndXmlElement("statements");


    }

    private void parseLet() {
        writeBegXmlElement("letStatement");

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
        writeEndXmlElement("letStatement");


    }

    private void parseReturn() {
        writeBegXmlElement("returnStatement");

        consumeKeyword(RETURN);
        if (!isNextTokenTheSymbol(SEMICOLON)) {
            parseExpression();
        }
        consumeSymbol(SEMICOLON);
        writeEndXmlElement("returnStatement");

    }

    private void parseDo() {
        writeBegXmlElement("doStatement");

        consumeKeyword(DO);
        parseSubroutineCall("nothing");
        consumeSymbol(SEMICOLON);
        writeEndXmlElement("doStatement");

    }

    public void parseWhile() {
        writeBegXmlElement("whileStatement");

        consumeKeyword(WHILE);
        consumeSymbol(LEFT_PARENTHESIS);
        parseExpression();
        consumeSymbol(RIGHT_PARENTHESIS);
        consumeStatementBlock();
        writeEndXmlElement("whileStatement");


    }
    public void parseIf() {
        writeBegXmlElement("ifStatement");

        consumeKeyword(IF);
        consumeSymbol(LEFT_PARENTHESIS);
        parseExpression();
        consumeSymbol(RIGHT_PARENTHESIS);

        consumeStatementBlock();
        String nextToken = getNextToken();
        if (isNextTokenInWords(nextToken,ELSE.toString())) {
            consumeKeyword(ELSE);
            consumeStatementBlock();
        }
        writeEndXmlElement("ifStatement");

    }

    private void consumeStatementBlock() {
        consumeSymbol(LEFT_CURLY_BRACKET);
        parseStatement();
        consumeSymbol(RIGHT_CURLY_BRACKET);
    }


    //Expression = tokenizer integer string constant | call | 
    public void parseExpression() {
        writeBegXmlElement("expression");
        compileTerm();
        while (isNextTokenAnOperator()) {
            consumeSymbol(Symbol.symbolMap.get(token));
            compileTerm();
        }
        writeEndXmlElement("expression");

    }

    private boolean isNextTokenAnOperator() {
        String nextToken = getNextToken();
        return next_token_type == SYMBOL && isNextTokenInWords(nextToken,PLUS.character, PLUS.character, MINUS.character,
                ASTERISK.character, SLASH.character, AND.character, OR.character, LESS.character, GREATER.character, EQUAL.character);
    }

    private void compileTerm() {
        writeBegXmlElement("term");

        String nextToken = getNextToken();
        if (next_token_type == INT_CONST) {
            writeXmlElement(INT_CONST.toString(), nextToken);
            moveNextToken();
//            tokenConsumed();
        } else if (next_token_type == STRING_CONST) {
            writeXmlElement(INT_CONST.toString(), nextToken);
            moveNextToken();
//            tokenConsumed();
        } else if (next_token_type == KEYWORD) {
            consumeKeyword(KEYWORDS.get(nextToken));
        } else if (next_token_type == IDENTIFIER) {
            String identifier = consumeIdentifier(); // varName or subroutineCall's subroutineName|className|varName
            if (isNextTokenTheSymbol(LEFT_SQUARE_BRACKET)) { // varName[...]
                consumeSymbol(LEFT_SQUARE_BRACKET);
                parseExpression();
                consumeSymbol(RIGHT_SQUARE_BRACKET);
            }
            else {
                if (isNextTokenTheSymbol(LEFT_PARENTHESIS)) { // with subroutineName left of parenthesis
                    consumeExpressionListWithParenthesis();
                }
                else if (isNextTokenTheSymbol(DOT)) { // with className or varName left of dot
                    consumeSymbol(DOT);
                    consumeIdentifier(); // subroutineName
                    consumeExpressionListWithParenthesis();
                }
            }
        } else if (next_token_type == SYMBOL) {
            if (token == LEFT_PARENTHESIS.character) {
                consumeSymbol(LEFT_PARENTHESIS);
                parseExpression();
                consumeSymbol(RIGHT_PARENTHESIS);
            }
            else if (UNARY_OPERATORS.contains(symbolMap.get(nextToken))) {
                consumeSymbol(symbolMap.get(nextToken));
                compileTerm();
            }
        } else {
            throw new RuntimeException("unkonw errors.");
        }
        writeEndXmlElement("term");

    }


    private void parseSubroutineCall(String identifier) {
        consumeIdentifier();
        if (isNextTokenTheSymbol(LEFT_PARENTHESIS)) { // with subroutineName left of parenthesis
            consumeExpressionListWithParenthesis();
        }
        else if (isNextTokenTheSymbol(DOT)) { // with className or varName left of dot
            consumeSymbol(DOT);
            consumeIdentifier(); // subroutineName
            consumeExpressionListWithParenthesis();
        }
    }
    private void consumeExpressionListWithParenthesis() {
        consumeSymbol(LEFT_PARENTHESIS);
        compileExpressionList();
        consumeSymbol(RIGHT_PARENTHESIS);
    }

    private void compileExpressionList() {
        writeBegXmlElement("expressionList");


        if (isNextTokenTheSymbol(RIGHT_PARENTHESIS)) {
            writeEndXmlElement("expressionList");
            return;
        }

        parseExpression();
        while (isNextTokenTheSymbol(COMMA)) {
            consumeSymbol(COMMA);
            parseExpression();
        }
        writeEndXmlElement("expressionList");

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
        System.out.println("<" + label + "> " + content + " </" + label + ">");
    }
    void writeBegXmlElement(String label) {
        System.out.println("<" + label + ">");
    }
    void writeEndXmlElement(String label) {
        System.out.println("</" + label + ">");
    }
    public static void main(String[] args) throws IOException {
//        Pattern compile = Pattern.compile("\\d+");
//        String l = "44500";
//        boolean matches = l.matches(compile.pattern());
//        System.out.println(matches);
//        String s = "13213213131ddd/*sdas*/";
//        String s1 = s.replaceAll("/\\*\\w*\\*/", "");
//        System.out.println(s1);

        System.out.println();
        CompilationEngine compilationEngine = new CompilationEngine("E:\\nand2tetris\\nand2tetris-master\\nand2tetris-master\\projects\\10\\ExpressionlessSquare\\Square.jack");
        compilationEngine.parseClass();
    }

}
