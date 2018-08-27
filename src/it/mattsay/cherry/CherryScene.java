package it.mattsay.cherry;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class CherryScene {

    @FXML
    private Button bindBtn;

    @FXML
    private TextField bindField;

    private static CherryScene instance;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label volumeLabel;
    @FXML
    private Label stateLabel;

    private static CherryScene getInstance() {
        return instance;
    }

    static void setStateText(boolean state) {
        Platform.runLater(() -> {
            getInstance().stateLabel.setText(state ? "ENABLED" : "DISABLED");
            getInstance().stateLabel.setTextFill((state ? Color.GREEN : Color.RED));
        });
    }

    @FXML
    public void initialize(){
        instance = this;
        if(!Main.lag.getKey().isEmpty())bindField.setText(Main.lag.getKey());
        Main.lag.setField(bindField);
        Main.lag.setStateLabel(stateLabel);
        setStateText(false);
        volumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            float volume = (float) Math.round(newValue.floatValue() * 10f) / 10f;
            volumeLabel.setText(volume + "%");
            Main.lag.setVolume(volume);
        });
    }

    @FXML
    public void onBind(ActionEvent event){
        Main.ENABLED = false;
        setStateText(false);
        Main.lag.setBinding(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Main.lag.setBinding(false);
            }
        }, 5000L);
    }

    @FXML
    public void onAbout(ActionEvent event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("icon.png")));
            stage.setTitle("About");
            stage.show();
        } catch (IOException e) {
            Main.LOGGER.handleException(e, false);
        }
    }


}