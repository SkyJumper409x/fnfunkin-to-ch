package xyz.skyjumper409.fnftoch;

public class ProgramSettings {
    // not planning on doing any validation sooo public attribute modifiers go weee
    public boolean enableStarPower;
    public boolean createSplitChart;
    // more settings are to be added
    public ProgramSettings() {
        this(true);
    }
    public ProgramSettings(boolean enableStarPower) {
        this(enableStarPower, false);
    }
    public ProgramSettings(boolean enableStarPower, boolean createSplitChart) {
        this.enableStarPower = enableStarPower;
        this.createSplitChart = createSplitChart;
    }
}
