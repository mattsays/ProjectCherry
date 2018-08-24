package it.mattsay.cherry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.Timer;
import java.util.TimerTask;

public class CherryScene {

    @FXML
    private Button bindBtn;

    @FXML
    private TextField bindField;

    @FXML
    public void initialize(){
        if(!Main.lag.getKey().isEmpty())bindField.setText(Main.lag.getKey());
        Main.lag.setField(bindField);
    }

    @FXML
    public void onBind(ActionEvent event){
        Main.ENABLED = false;
        Main.lag.setBinding(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Main.lag.setBinding(false);
            }
        }, 5000L);
    }

}