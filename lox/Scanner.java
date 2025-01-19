package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
        keywords.put("break", BREAK);
    }

    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = consumeCurrentChar();

        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);
            case '?' -> addToken(QUESTION);
            case ':' -> addToken(COLON);
            case '!' -> addToken(currentCharMatches('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(currentCharMatches('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(currentCharMatches('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(currentCharMatches('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if (currentCharMatches('/')) {
                    // A comment goes until the end of the line.
                    while (currentChar() != '\n' && !isAtEnd())
                        consumeCurrentChar();
                } else if (currentCharMatches('*')) {
                    blockComment();
                } else {
                    addToken(SLASH);
                }
            }
            case ' ', '\r', '\t' -> {
            }
            // Ignore whitespace.
            case '\n' -> line++;
            case '"' -> string();
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
            }
        }
    }

    private void blockComment() {
        while (!isAtEnd()) {
            if (currentChar() == '*' && nextChar() == '/') {
                consumeCurrentChar(); // eat *
                consumeCurrentChar(); // eat /
                return;
            }

            if (currentCharMatches('\n')) {
                line++;
                continue;
            }

            consumeCurrentChar();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated block comment.");
        }
    }

    private void identifier() {
        while (isAlphaNumeric(currentChar()))
            consumeCurrentChar();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(currentChar()))
            consumeCurrentChar();

        // Look for a fractional part.
        if (currentChar() == '.' && isDigit(nextChar())) {
            // Consume the "."
            consumeCurrentChar();

            while (isDigit(currentChar()))
                consumeCurrentChar();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (currentChar() != '"' && !isAtEnd()) {
            if (currentChar() == '\n')
                line++;
            consumeCurrentChar();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        consumeCurrentChar();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean currentCharMatches(char expected) {
        if (isAtEnd())
            return false;

        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private char currentChar() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char nextChar() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char consumeCurrentChar() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
