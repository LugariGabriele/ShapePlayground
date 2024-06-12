package game.framework.input;

import game.framework.SimulationBody;
import javafx.scene.Node;
import org.dyn4j.world.World;

public class ExportCodeKeyboardInputHandler extends AbstractKeyboardInputHandler {
    private final World<SimulationBody> world;

    public ExportCodeKeyboardInputHandler(Node component, int key, World<SimulationBody> world) {
        super(component, new Key[]{new Key(key)});
        this.world = world;
    }

    protected void onKeyPressed() {
        super.onKeyPressed();
        System.out.println(CodeExporter.export("SampleExport", this.world));
    }

    public boolean isActive() {
        return false;
    }
}
