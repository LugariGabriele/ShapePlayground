package game.framework.input;

import javafx.scene.Node;


public class ToggleStateKeyboardInputHandler extends AbstractKeyboardInputHandler {
    private boolean active = false;

    public ToggleStateKeyboardInputHandler(Node component, Key... keys) {
        super(component, keys);
    }

    public ToggleStateKeyboardInputHandler(Node component, int... keys) {
        super(component, keys);
    }

    protected void onKeyPressed() {
        super.onKeyPressed();
        this.active = !this.active;
    }

    public void setActive(boolean flag) {
        this.active = flag;
    }

    public boolean isActive() {
        return this.active;
    }
}
