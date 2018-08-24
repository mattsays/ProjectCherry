package it.mattsay.cherry.utils;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CherryLogger {

    private Path outputName;
    private SimpleDateFormat dateFormat;
    private boolean debug = false;

    /**
     * Creates the log file and if the file count is equal or more than 8 the logger deletes all the log files
     */
    public CherryLogger() throws IOException {
        this.dateFormat = new SimpleDateFormat();
        String outputName = "logs/" + this.getCurrentTime("yyyy-MM-dd-HH-mm-ss") + ".log";
        File file = new File(outputName);

        if(!new File("logs").exists())
            Files.createDirectory(Paths.get("logs"));

        if (file.getParentFile().listFiles().length >= 8) {
            for (File f :
                    file.getParentFile().listFiles()) {
                f.delete();
            }
        }
        file.createNewFile();

        this.outputName = file.toPath();

    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Prints the message with the correct log level and appends to the log file
     *
     * @param level
     * @param message
     */
    private void log(Level level, String message) {
        String log = "[" + getCurrentTime("MM/dd/yyyy - HH:mm:ss") + "] " + "{" + level.name() + "} " + message;
        System.out.println(level.getColor() + log + "\033[0m");
        try {
            Files.write(this.outputName, (log + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(String message) {
        this.log(Level.INFO, message);
    }

    public void debug(String message, DebugType debug) {
        if (!this.debug) return;
        if (debug == null) debug = DebugType.UNKNOWN;
        this.log(Level.DEBUG, "[" + debug.name() + "] " + message);
    }

    public void debug(String message, String debug) {
        if (!this.debug) return;
        if (debug == null) debug = DebugType.UNKNOWN.name();
        this.log(Level.DEBUG, "[" + debug.toUpperCase() + "] " + message);
    }

    public void warn(String message) {
        this.log(Level.WARNING, message);
    }

    public void err(String message, ErrorType error) {
        this.err(message, error.name());
    }

    public void err(String message, String error) {
        if (error == null) error = ErrorType.UNKNOWN.name();
        this.log(Level.ERROR, "[" + error.toUpperCase() + "] " + message);
    }

    public void handleException(Exception e, boolean exit){
        err("Exception was thrown", ErrorType.EXCEPTION);
        err(e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "no message" : e.getMessage()), ErrorType.EXCEPTION);
        for(StackTraceElement element : e.getStackTrace()){
            err(element.toString(), ErrorType.EXCEPTION);
        }
        if(exit) System.exit(-1);
    }

    public void goodBye(){
        System.out.println("//////////////////////////////////");
        System.out.println("/                                /");
        System.out.println("/           Good Bye!            /");
        System.out.println("/                                /");
        System.out.println("//////////////////////////////////");
    }


    /**
     * Gets the current time from the format
     *
     * @param format
     * @return current time
     */
    private String getCurrentTime(String format) {
        dateFormat.applyPattern(format);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public enum Level {
        INFO("\033[0m"), ERROR("\033[0;31m"), WARNING("\033[0;33m"), DEBUG("\033[0;36m");

        private String color;

        Level(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public enum DebugType {
        UNKNOWN, COMMANDS
    }

    public enum ErrorType {
        UNKNOWN, EXCEPTION, COMMANDS
    }

}
