package c10;

public enum Symbol {
    LEFT_CURLY_BRACKET("{"),
    RIGHT_CURLY_BRACKET("}"),
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    LEFT_SQUARE_BRACKET("["),
    RIGHT_SQUARE_BRACKET("]"),
    DOT("."),
    COMMA(","),
    SEMICOLON(";"),
    PLUS("+"),
    MINUS("-"),
    ASTERISK("*"),
    SLASH("/"),
    AND("&", "&amp;"),
    OR("|"),
    NOT("~"),
    LESS("<", "&lt;"),
    GREATER(">", "&gt;"),
    EQUAL("=");

    private final String character;
    private final String xmlExpress;

    Symbol(String character) {
        this(character, null);
    }

    Symbol(String character, String xmlExpress) {
        this.character = character;
        this.xmlExpress = xmlExpress;
    }


}
