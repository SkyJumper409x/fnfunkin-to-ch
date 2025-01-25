package xyz.skyjumper409.fnftoch.general;

public abstract class TickNote extends Note {
    protected int tick;
    protected int length;
    protected TickNote(int tick) { this.tick = tick; }
    protected TickNote(int tick, int length) { this(tick); this.length = length; }
    protected TickNote(byte lane, int tick) { super(lane); this.tick = tick; }
    protected TickNote(byte lane, int tick, int length) { this(lane, tick); this.length = length; }
    public int getTick() {
        return tick;
    }
    public int getLength() {
        return length;
    }

    @Override
    public double getTime() {
        return this.getTick();
    }
}
