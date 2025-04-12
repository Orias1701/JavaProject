package model;

public class ConsoleApiLogger implements ApiLogger {
    @Override
    public void logInfo(String message) {
        System.out.println(message);
    }

    @Override
    public void logError(String message) {
        System.err.println(message);
    }
}