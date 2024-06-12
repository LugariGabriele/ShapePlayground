package game.framework.input;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

    public abstract class AbstractKeyboardInputHandler extends AbstractInputHandler implements InputHandler {
        protected final Node node;
        protected final Key[] keys;
    private final CustomKeyListener keyListener;

    public AbstractKeyboardInputHandler(Node node, Key... keys) {
        this.node = node;
        this.keys = keys;
        this.keyListener = new CustomKeyListener();
    }

    public AbstractKeyboardInputHandler(Node node, int... keys) {
        this.node = node;
        this.keys = new Key[keys.length];

        for (int i = 0; i < keys.length; ++i) {
            this.keys[i] = new Key(keys[i]);
        }

        this.keyListener = new CustomKeyListener();
    }

    private boolean isKeyMatch(KeyCode key, boolean shift, boolean ctrl, boolean alt, boolean meta) {
        for (Key k : this.keys) {
            if (k.key == key.getCode() &&
                    k.isShift == shift &&
                    k.isCtrl == ctrl &&
                    k.isAlt == alt &&
                    k.isMeta == meta) {
                return true;
            }
        }
        return false;
    }

    public void install() {
        this.node.addEventHandler(KeyEvent.KEY_PRESSED, keyListener::handleKeyPressed);
        this.node.addEventHandler(KeyEvent.KEY_RELEASED, keyListener::handleKeyReleased);
    }

    public void uninstall() {
        this.node.removeEventHandler(KeyEvent.KEY_PRESSED, keyListener::handleKeyPressed);
        this.node.removeEventHandler(KeyEvent.KEY_RELEASED, keyListener::handleKeyReleased);
    }

    protected void onKeyPressed() {
    }

    protected void onKeyReleased() {
    }

    private class CustomKeyListener {
        private CustomKeyListener() {
        }

        public void handleKeyPressed(KeyEvent e) {
            if (!e.isConsumed()) {
                if (AbstractKeyboardInputHandler.this.isKeyMatch(
                        e.getCode(),
                        e.isShiftDown(),
                        e.isControlDown(),
                        e.isAltDown(),
                        e.isMetaDown())) {
                    if (AbstractKeyboardInputHandler.this.isEnabled() && !AbstractKeyboardInputHandler.this.isDependentBehaviorActive()) {
                        AbstractKeyboardInputHandler.this.onKeyPressed();
                    }
                }
            }
        }

        public void handleKeyReleased(KeyEvent e) {
            if (!e.isConsumed()) {
                if (AbstractKeyboardInputHandler.this.isKeyMatch(
                        e.getCode(),
                        e.isShiftDown(),
                        e.isControlDown(),
                        e.isAltDown(),
                        e.isMetaDown())) {
                    if (AbstractKeyboardInputHandler.this.isEnabled() && !AbstractKeyboardInputHandler.this.isDependentBehaviorActive()) {
                        AbstractKeyboardInputHandler.this.onKeyReleased();
                    }
                }
            }
        }
    }
}
