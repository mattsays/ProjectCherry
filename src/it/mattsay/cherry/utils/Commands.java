package it.mattsay.cherry.utils;

import it.mattsay.cherry.Main;
import java.io.IOException;

public class Commands {

    public static boolean is64(){
        return System.getProperty("os.arch").contains("64");
    }

    public static void execute(String command){
        try {
            Main.LOGGER.debug("Executing \"" + command + "\"", CherryLogger.DebugType.COMMANDS);
            Runtime.getRuntime().exec(command);
        } catch (IOException  e) {
            Main.LOGGER.handleException(e, true);
        }
    }


}
