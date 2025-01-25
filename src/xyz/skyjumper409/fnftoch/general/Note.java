package xyz.skyjumper409.fnftoch.general;

import xyz.skyjumper409.fnftoch.Constants;

public abstract class Note implements Comparable<Note> {
    private static byte maxLaneBitCount = 1;
    protected static void setMaxLaneBitCount(byte bitCount) {
        if(bitCount > maxLaneBitCount && bitCount < 8) {
            maxLaneBitCount = bitCount;
        }
    }

    protected byte lane;
    protected Note() { }
    protected Note(byte lane) { this.lane = lane; }
    public byte getLane() {
        return this.lane;
    }
    public abstract double getTime();
    @Override
    public int compareTo(Note note) {
        int result = ((int)((this.getTime()-note.getTime())*Constants.inverseMarginOfError)) << maxLaneBitCount;
        if(result == 0) {
            result = this.getLane()-note.getLane();
        }
        // System.out.printf("compared notes: [%f,%d,%f] and [%f%d%f], result is: %d",
        //     startTime, lane, sustainLength, note.startTime, note.lane, note.sustainLength, result);
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Note) {
            Note note = (Note)o;
            return this.compareTo(note) == 0;
        }
        return false;
    }
}
