package game.framework.input;

public class Key {
    public final int key;
    public final boolean isShift;
    public final boolean isCtrl;
    public final boolean isAlt;
    public final boolean isMeta;

    public Key(int key, boolean isShift, boolean isCtrl, boolean isAlt, boolean isMeta) {
        this.key = key;
        this.isShift = isShift;
        this.isCtrl = isCtrl;
        this.isAlt = isAlt;
        this.isMeta = isMeta;
    }

    public Key(int key) {
        this(key, false, false, false, false);
    }
}

