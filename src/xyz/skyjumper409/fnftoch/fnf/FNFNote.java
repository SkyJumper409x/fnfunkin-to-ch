package xyz.skyjumper409.fnftoch.fnf;

import xyz.skyjumper409.fnftoch.general.*;

public class FNFNote extends TimeNote {
    static {
        Note.setMaxLaneBitCount((byte)3); // left section 0-3, right section 4-7
    }
    public FNFNote(double time) { super(time); }
    public FNFNote(double time, double length) { super(time, length); }
    public FNFNote(byte lane, double time) { super(lane, time); }
    public FNFNote(byte lane, double time, double length) { super(lane, time, length); }
    // public int compareTo(FNFNote note) {
    //     int result = ((((int)((startTime-note.startTime)*Constants.inverseMarginOfError)) << 3) & 0x7FFFFFFF);
    //     if(result == 0) {
    //         result = lane-note.lane;
    //     }
    //     // System.out.printf("compared notes: [%f,%d,%f] and [%f%d%f], result is: %d",
    //     //     startTime, lane, sustainLength, note.startTime, note.lane, note.sustainLength, result);
    //     return result;
    // }
}
