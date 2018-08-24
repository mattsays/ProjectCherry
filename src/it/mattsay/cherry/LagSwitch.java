package it.mattsay.cherry;


import it.mattsay.cherry.utils.Commands;
import javafx.scene.control.TextField;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class LagSwitch implements NativeKeyListener, NativeMouseListener {

    private enum MouseButtons{
        MOUSE_1(1), MOUSE_2(2),MOUSE_3(3), MOUSE_4(4), MOUSE_5(5);

        private int i;

        MouseButtons(int i){
            this.i = i;
        }

        public int getCode() {
            return i;
        }
    }

    private boolean state, pressed, binding;
    private String c;
    private TextField field;

    public void setKey(String c) {
        this.c = c;
    }

    public String getKey() {
        return c;
    }

    public void setField(TextField field) {
        this.field = field;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
        if(binding)
            field.setText("Press any key...");
        else {
            field.setText(c);
            Main.ENABLED = true;
        }
    }

    public void setState(boolean state){
        Commands.execute("netsh advfirewall set allprofiles firewallpolicy blockinbound," + (state ? "blockoutbound" : "allowoutbound"));
        Main.LOGGER.info("You have " + (state ? "enabled" : "disabled") + " the lag switch!");
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase(c) && !pressed){
            pressed = true;
            if(Main.ENABLED) {
                state = !state;
                this.setState(state);
            }
        }
        if(this.binding){
            this.setKey(NativeKeyEvent.getKeyText(e.getKeyCode()));
            setBinding(false);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if(NativeKeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase(c) && pressed){
            pressed = false;
        }
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        if(c.equalsIgnoreCase(MouseButtons.MOUSE_1.name()) || c.equalsIgnoreCase(MouseButtons.MOUSE_2.name()) || c.equalsIgnoreCase(MouseButtons.MOUSE_3.name()) || c.equalsIgnoreCase(MouseButtons.MOUSE_4.name()) || c.equalsIgnoreCase(MouseButtons.MOUSE_5.name())) {
            if (MouseButtons.valueOf(c).getCode() == e.getButton() && !pressed) {
                pressed = true;
                if (Main.ENABLED) {
                    state = !state;
                    this.setState(state);
                }
            }
        }
            if (this.binding) {
                for (MouseButtons btn : MouseButtons.values()) {
                    if (btn.getCode() == e.getButton()) {
                        setKey(btn.name());
                        break;
                    }
                }
                setBinding(false);
            }


    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        if(pressed){
            pressed = false;
        }
    }
}
