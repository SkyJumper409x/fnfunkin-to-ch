package xyz.skyjumper409.fnftoch.fnf;

import xyz.skyjumper409.fnftoch.Constants;

public class FNFSection implements Comparable<FNFSection> {
    public double startTime, endTime;
    public int lengthInSteps;
    public int bpm;
    public boolean mustHitSection;
    public FNFNote[] notes;
    public int compareTo(FNFSection section) {
        int result = ((((int)((startTime-section.startTime)*Constants.inverseMarginOfError))));
        return result;
    }
}
