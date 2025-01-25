package xyz.skyjumper409.fnftoch.ch;

import xyz.skyjumper409.fnftoch.general.*;

public class CHNote extends TickNote {
    static {
        Note.setMaxLaneBitCount((byte)3); // frets 0-4, force and tap 5 and 6, open note 7
    }
    public CHNote(int tick) { super(tick); }
    public CHNote(int tick, int length) { super(tick, length); }
    public CHNote(byte lane, int tick) { super(lane, tick); }
    public CHNote(byte lane, int tick, int length) { super(lane, tick, length); }
}
