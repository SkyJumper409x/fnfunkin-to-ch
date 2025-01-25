package xyz.skyjumper409.fnftoch.general;

public abstract class TimeNote extends Note {
    protected double time, length;
    protected TimeNote(double time) { this.time = time; }
    protected TimeNote(double time, double length) { this(time); this.length = length; }
    protected TimeNote(byte lane, double time) { super(lane); this.time = time; }
    protected TimeNote(byte lane, double time, double length) { this(lane, time); this.length = length; }

    public double getTimeInMs() {
        return time;
    }
    public double getLengthInMs() {
        return length;
    }

    @Override
    public double getTime() {
        return this.getTimeInMs();
    }
}
