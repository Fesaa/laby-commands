package art.ameliah.brigadier.core.models.types;

import art.ameliah.brigadier.core.models.exceptions.CommandException;

public class StringReaderWrapper {

  private static final char SYNTAX_ESCAPE = '\\';
  private static final char SYNTAX_DOUBLE_QUOTE = '"';
  private static final char SYNTAX_SINGLE_QUOTE = '\'';

  private final String string;
  private int cursor;

  public StringReaderWrapper(final StringReaderWrapper other) {
    this.string = other.string;
    this.cursor = other.cursor;
  }

  public StringReaderWrapper(final String string) {
    this.string = string;
  }

  public StringReaderWrapper(final String string, final int cursor) {
    this.string = string;
    this.cursor = cursor;
  }

  public static boolean isAllowedNumber(final char c) {
    return c >= '0' && c <= '9' || c == '.' || c == '-';
  }

  public static boolean isQuotedStringStart(char c) {
    return c == SYNTAX_DOUBLE_QUOTE || c == SYNTAX_SINGLE_QUOTE;
  }

  public static boolean isAllowedInUnquotedString(final char c) {
    return c >= '0' && c <= '9'
        || c >= 'A' && c <= 'Z'
        || c >= 'a' && c <= 'z'
        || c == '_' || c == '-'
        || c == '.' || c == '+';
  }

  public String getString() {
    return string;
  }

  public int getRemainingLength() {
    return string.length() - cursor;
  }

  public int getTotalLength() {
    return string.length();
  }

  public int getCursor() {
    return cursor;
  }

  public void setCursor(final int cursor) {
    this.cursor = cursor;
  }

  public String getRead() {
    return string.substring(0, cursor);
  }

  public String getRemaining() {
    return string.substring(cursor);
  }

  public boolean canRead(final int length) {
    return cursor + length <= string.length();
  }

  public boolean canRead() {
    return canRead(1);
  }

  public char peek() {
    return string.charAt(cursor);
  }

  public char peek(final int offset) {
    return string.charAt(cursor + offset);
  }

  public char read() {
    return string.charAt(cursor++);
  }

  public void skip() {
    cursor++;
  }

  public void skipWhitespace() {
    while (canRead() && Character.isWhitespace(peek())) {
      skip();
    }
  }

  public String readUnquotedString() {
    final int start = cursor;
    while (canRead() && isAllowedInUnquotedString(peek())) {
      skip();
    }
    return string.substring(start, cursor);
  }

  public int readInt() throws CommandException {
    final int start = cursor;
    while (canRead() && isAllowedNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw new CommandException("Expected an integer");
    }
    try {
      return Integer.parseInt(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw new CommandException("Expected an integer, got: " + number);
    }
  }

  public long readLong() throws CommandException {
    final int start = cursor;
    while (canRead() && isAllowedNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw new CommandException("Expected a long");
    }
    try {
      return Long.parseLong(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw new CommandException("Expected an long, got: " + number);
    }
  }

  public double readDouble() throws CommandException {
    final int start = cursor;
    while (canRead() && isAllowedNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw new CommandException("Expected a double");
    }
    try {
      return Double.parseDouble(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw new CommandException("Expected an double, got: " + number);
    }
  }

  public float readFloat() throws CommandException {
    final int start = cursor;
    while (canRead() && isAllowedNumber(peek())) {
      skip();
    }
    final String number = string.substring(start, cursor);
    if (number.isEmpty()) {
      throw new CommandException("Expected an double");
    }
    try {
      return Float.parseFloat(number);
    } catch (final NumberFormatException ex) {
      cursor = start;
      throw new CommandException("Expected an double, got: " + number);
    }
  }

}
