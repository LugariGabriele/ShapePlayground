package game.framework.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractInputHandler implements InputHandler {
    private boolean enabled = true;
    private boolean additive = false;
    private final List<InputHandler> dependentBehaviors = new ArrayList();

    public AbstractInputHandler() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean flag) {
        this.enabled = flag;
    }

    public List<InputHandler> getDependentBehaviors() {
        return this.dependentBehaviors;
    }

    public boolean isDependentBehaviorActive() {
        boolean result = false;
        Iterator var2 = this.dependentBehaviors.iterator();

        while(var2.hasNext()) {
            InputHandler behavior = (InputHandler)var2.next();
            if (behavior.isActive()) {
                result = true;
            }
        }

        if (this.additive) {
            return !result;
        } else {
            return result;
        }
    }

    public boolean isDependentBehaviorsAdditive() {
        return this.additive;
    }

    public void setDependentBehaviorsAdditive(boolean flag) {
        this.additive = flag;
    }
}
