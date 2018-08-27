package it.mattsay.cherry.utils;

import it.mattsay.cherry.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
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

    public static synchronized void playSound(final String url, float volume) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                        new BufferedInputStream(Main.class.getResourceAsStream(url)));

                clip.open(inputStream);

                FloatControl gainControl = (FloatControl) clip
                        .getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume / 100f) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);

                clip.start();
            } catch (Exception e) {
                Main.LOGGER.handleException(e, false);
            }
        }
        ).start();
    }

}
