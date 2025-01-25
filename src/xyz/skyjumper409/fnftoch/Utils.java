package xyz.skyjumper409.fnftoch;

public class Utils {
    private Utils() { }
    public static void chPrintln(java.io.PrintWriter pr, String s) {
        pr.print(s);
        pr.print(Constants.chChartNewline);
    }
    // the three-fingered claw (except try(), so it's the two-fingered claw, i guess)
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
}
