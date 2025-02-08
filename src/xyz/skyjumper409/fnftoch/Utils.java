package xyz.skyjumper409.fnftoch;

public class Utils {
    private Utils() { }
    public static void chPrintln(java.io.PrintWriter pr, String s) {
        pr.print(s);
        pr.print(Constants.chChartNewline);
    }
    // the three-fingered claw (without try(), so it's the two-fingered claw, i guess)
    public static void yell(String message) {
        System.err.println(message);
    }
    public static void die() {
        die(1);
    }
    public static void die(int exitcode) {
        System.exit(exitcode);
    }
    public static void die(String message) {
        yell(message); die();
    }
    public static void die(String message, int exitcode) {
        yell(message); die(exitcode);
    }

    public static boolean parseHumanBoolean(String s) {
        return parseHumanBoolean(s, false);
    }
    public static boolean parseHumanBoolean(String s, boolean defaultIfAmbigous) {
        boolean result = defaultIfAmbigous;
        s = s.toLowerCase();
        if(s.equals("yse") || (s.startsWith("y") && "yes".contains(s))) {
            result = true;
        } else if(s.equals("no") || s.equals("n")) {
            result = false;
        }
        return result;
    }
}
