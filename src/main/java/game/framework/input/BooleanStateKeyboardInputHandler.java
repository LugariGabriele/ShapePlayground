package game.framework.input;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class BooleanStateKeyboardInputHandler extends AbstractKeyboardInputHandler {
    private boolean active = false;
    private boolean hasBeenHandled = false;

    public BooleanStateKeyboardInputHandler(Node component, Key... keys) {
        super(component, keys);
    }

    public BooleanStateKeyboardInputHandler(Node component, int... keys) {
        super(component, keys);
    }

    @Override
    protected void onKeyPressed() {
        super.onKeyPressed();
        boolean active = this.active;
        this.active = true;
        if (!active) {
            this.hasBeenHandled = false;
        }
    }

    @Override
    protected void onKeyReleased() {
        super.onKeyReleased();
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isActiveButNotHandled() {
        return !this.hasBeenHandled && this.active;
    }

    public void setHasBeenHandled(boolean hasBeenHandled) {
        this.hasBeenHandled = hasBeenHandled;
    }
}
