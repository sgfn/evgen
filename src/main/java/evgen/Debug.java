package evgen;

public class Debug {
    public static boolean debug = false;

    public static void print(Object s) {
        if (debug) {
            System.out.print("[DEBUG] ");
            System.out.print(s);
        }
    }

    public static void println(Object s) {
        if (debug) {
            System.out.print("[DEBUG] ");
            System.out.println(s);
        }
    }
}
