package game.framework.input;
import java.util.List;

public interface InputHandler {
    void install();

    void uninstall();

    boolean isEnabled();

    void setEnabled(boolean var1);

    boolean isActive();

    List<InputHandler> getDependentBehaviors();

    boolean isDependentBehaviorActive();

    boolean isDependentBehaviorsAdditive();

    void setDependentBehaviorsAdditive(boolean var1);
}

