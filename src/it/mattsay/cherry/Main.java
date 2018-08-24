package it.mattsay.cherry;

import it.mattsay.cherry.utils.CherryLogger;
import it.mattsay.cherry.utils.Commands;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    public static CherryLogger LOGGER;
    private static boolean HIDE = false;
    static boolean ENABLED = true;
    static LagSwitch lag;
    private Stage stage;

    private void setupTray(){
        if(!SystemTray.isSupported()){
            LOGGER.err("System tray not supported", CherryLogger.ErrorType.UNKNOWN);
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png"));

        PopupMenu menu = new PopupMenu();

        TrayIcon icon = new TrayIcon(image, "CherrySwitch");
        icon.setImageAutoSize(true);

        icon.addActionListener(event -> Platform.runLater(this::showStage));


        MenuItem show = new MenuItem("Show");
        show.addActionListener(event -> Platform.runLater(this::showStage));
        menu.add(show);


        MenuItem close = new MenuItem("Close");
        close.addActionListener(event -> {
            LOGGER.info("Closing...");
            tray.remove(icon);
            Platform.runLater(this::exit);
        });
        menu.add(close);

        icon.setPopupMenu(menu);
        try{
            tray.add(icon);
        } catch (AWTException e){
            LOGGER.handleException(e, true);
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            LOGGER = new CherryLogger();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lag = new LagSwitch();

        if(new File("config.txt").exists()){
            List<String> lines = Files.readAllLines(Paths.get("config.txt"));
            if(lines.get(0).contains("binding=")) lag.setKey(lines.get(0).replace("binding=", ""));
        } else {
            lag.setKey("");
        }



        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(lag);
        GlobalScreen.addNativeMouseListener(lag);


        SwingUtilities.invokeLater(this::setupTray);

        Platform.setImplicitExit(false);
        primaryStage.setTitle("CherrySwitch");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                HIDE = true;
                primaryStage.hide();
            }
        });
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cherry.fxml"));
        loader.setController(new CherryScene());
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.show();
        this.stage = primaryStage;
    }

    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private void exit(){
        Platform.exit();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        lag.setState(false);
        try {
        if(!new File("config.txt").exists()) Files.createFile(Paths.get("config.txt"));
        Files.write(Paths.get("config.txt"), ("binding=" + lag.getKey()).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.handleException(e, false);
        }
        System.exit(0);
    }
}
