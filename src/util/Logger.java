package util;

public class Logger {

    public static void log(String message) {
        System.out.println(message);
    }

    public static void logError(String message) {
        System.err.println(message);
    }

    public static void logStackTrace(Exception e) {
        e.printStackTrace();
    }
}