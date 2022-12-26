package evgen.lib;

public final class ConsoleColour {
    public enum Colour {
        BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE
    }

    public static final String ANSI_RESET     = "\u001B[0m";
    public static final String ANSI_BRIGHT    = "\u001B[1m";
    public static final String ANSI_DIM       = "\u001B[2m";
    public static final String ANSI_BLACK     = "\u001B[30m";
    public static final String ANSI_RED       = "\u001B[31m";
    public static final String ANSI_GREEN     = "\u001B[32m";
    public static final String ANSI_YELLOW    = "\u001B[33m";
    public static final String ANSI_BLUE      = "\u001B[34m";
    public static final String ANSI_PURPLE    = "\u001B[35m";
    public static final String ANSI_CYAN      = "\u001B[36m";
    public static final String ANSI_WHITE     = "\u001B[37m";
    public static final String ANSI_BG_BLACK  = "\u001B[40m";
    public static final String ANSI_BG_RED    = "\u001B[41m";
    public static final String ANSI_BG_GREEN  = "\u001B[42m";
    public static final String ANSI_BG_YELLOW = "\u001B[43m";
    public static final String ANSI_BG_BLUE   = "\u001B[44m";
    public static final String ANSI_BG_PURPLE = "\u001B[45m";
    public static final String ANSI_BG_CYAN   = "\u001B[46m";
    public static final String ANSI_BG_WHITE  = "\u001B[47m";

    public static final String fromColour(Colour c) {
        return switch (c) {
            case BLACK  -> ANSI_BLACK;
            case RED    -> ANSI_RED;
            case GREEN  -> ANSI_GREEN;
            case YELLOW -> ANSI_YELLOW;
            case BLUE   -> ANSI_BLUE;
            case PURPLE -> ANSI_PURPLE;
            case CYAN   -> ANSI_CYAN;
            case WHITE  -> ANSI_WHITE;
        };
    }

    public static final String bgFromColour(Colour c) {
        return switch (c) {
            case BLACK  -> ANSI_BG_BLACK;
            case RED    -> ANSI_BG_RED;
            case GREEN  -> ANSI_BG_GREEN;
            case YELLOW -> ANSI_BG_YELLOW;
            case BLUE   -> ANSI_BG_BLUE;
            case PURPLE -> ANSI_BG_PURPLE;
            case CYAN   -> ANSI_BG_CYAN;
            case WHITE  -> ANSI_BG_WHITE;
        };
    }

    public static final String colourise(String s, Colour c) {
        return fromColour(c) + s + (s.endsWith(ANSI_RESET) ? "" : ANSI_RESET);
    }

    public static final String colourise(String s, Colour fgColour, Colour bgColour) {
        return fromColour(fgColour) + bgFromColour(bgColour) + s + (s.endsWith(ANSI_RESET) ? "" : ANSI_RESET);
    }

    public static final String colouriseBackground(String s, Colour c) {
        return bgFromColour(c) + s + (s.endsWith(ANSI_RESET) ? "" : ANSI_RESET);
    }

    public static final String brighter(String s) {
        return ANSI_BRIGHT + s + (s.endsWith(ANSI_RESET) ? "" : ANSI_RESET);
    }

    public static final String dimmer(String s) {
        return ANSI_DIM + s + (s.endsWith(ANSI_RESET) ? "" : ANSI_RESET);
    }
}
