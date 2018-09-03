package c10;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

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


    public static Map<String, Symbol> symbolMap = Stream.of(values()).collect(toMap(Symbol::toString, Function.identity()));
    public  String character;
    public  String xmlExpress;

    Symbol(String character) {
        this(character, null);
    }

    Symbol(String character, String xmlExpress) {
        this.character = character;
        this.xmlExpress = xmlExpress;
    }

    public static void main(String[] args) {
        System.out.println(1);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
