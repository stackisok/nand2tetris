package c10;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static c10.Keyword.CLASS;
import static c10.Symbol.LEFT_CURLY_BRACKET;
import static c10.Symbol.RIGHT_CURLY_BRACKET;


public class CompilationEngine {
    BufferedReader fileReader;
    String currentLine;
    int position;
    String token;

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
    private String getNextToken() {
        return "";
    }
    private boolean hasMoreTokens() {

        return true;
    }
    void consumeSymbol(Symbol symbol) {

    }

    private void consumeIdentifier() {

    }
    private void consumeSymbol() {

    }

    private void parseClassVarDec() {

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


    void consumeKeyword(Keyword  keyword) {

    }

}
