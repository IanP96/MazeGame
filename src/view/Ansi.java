package view;

/**
 * A utility class for ANSI characters used to print to the console in colour.
 */
class Ansi {

    private static final String RESET = "\u001B[0m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";

    /**
     * Wraps a string in ANSI characters so it will be coloured when printed.
     * @param text The text to format.
     * @param colour The ANSI character corresponding to the desired colour.
     * @return The string surrounded by the appropriate ANSI characters.
     */
    public static String colour(String text, String colour) {
        return colour + text + RESET;
    }

}
