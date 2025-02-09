package xyz.skyjumper409.fnftoch;

import static xyz.skyjumper409.fnftoch.Utils.chPrintln;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import xyz.skyjumper409.fnftoch.fnf.FNFNote;
import xyz.skyjumper409.fnftoch.fnf.FNFSection;

public class MyMain {
    public static void main(String[] args) {
        parseArgsAndGo(args);
    }
    public static void parseArgsAndGo(String[] args) {
        if(args.length < 1) {
            Utils.die("not enough arguments (missing input file argument)");
        }
        File file = new File(args[0]);
        if(!file.isFile()) {
            System.err.println("file not found");
            System.exit(1);
        }
        MyMain myMain = new MyMain();
        String workingDirpath = System.getProperty("user.dir");
        File outputDir = null;
        for(int i = 1; i < args.length; i++) {
            String arg = args[i];
            if(arg.indexOf("--output-dir=") != -1) {
                if(outputDir != null) {
                    Utils.die("--output-dir= was specified multiple times");
                }
                String outDirPath = arg.substring("--output-dir=".length());
                if(!outDirPath.startsWith("/") && !(
                    (outDirPath.charAt(0)+"").matches("[A-Z]") && outDirPath.charAt(1) == ':' && "/\\".contains(outDirPath.charAt(2)+"") // wtf is this line who wrote this (me)
                )) {
                    outDirPath = workingDirpath + "/" + outDirPath;
                }
                outputDir = new File(outDirPath);
            }
            switch(arg) {
                case "--enableStarPower":
                    myMain.settings.enableStarPower = true;
                    continue;
                case "--noStarPower":
                    myMain.settings.enableStarPower = false;
                    continue;
                case "--enableSplitChart":
                    myMain.settings.createSplitChart = true;
                    continue;
                case "--noSplitChart":
                    myMain.settings.createSplitChart = false;
                    continue;
                default:
                    /*
                    * The equalsSignIndex stuff could be hardcoded for each option, which
                    * would probably save like a nanosecond, but I'm not bothering with that,
                    * because it has negative impact on readability.
                    */
                    int equalsSignIndex = arg.indexOf('=');
                    if(equalsSignIndex < 0) {
                        continue;
                    } else if(equalsSignIndex+1 == arg.length()) {
                        Utils.die("Missing argument value for \"" + arg + "\"");
                    }
                    String argValueString = arg.substring(equalsSignIndex+1);
                    boolean argValue = Utils.parseHumanBoolean(argValueString, true);
                    if(arg.startsWith("--enableStarPower=")) {
                        myMain.settings.enableStarPower = argValue;
                    } else if(arg.startsWith("--enableSplitChart=")) {
                        myMain.settings.createSplitChart = argValue;
                    }
                    continue;
            }
        }
        if(outputDir == null) {
            outputDir = new File(workingDirpath);
        }
        if(!outputDir.isDirectory()) {
            Utils.die("output directory doesn't exist or isn't a directory");
        }
        myMain.go(file, outputDir);
    }
    private final ProgramSettings settings = new ProgramSettings();
    public void go(File inFile, File outputDir) {
        try {
            JSONObject rootObj = new JSONObject(new JSONTokener(new FileInputStream(inFile)));
            JSONObject songObj = rootObj.getJSONObject("song");
            JSONArray jsonSections = songObj.getJSONArray("notes");
            ArrayList<FNFSection> sections = new /*FNFSection[jsonSections.length()]*/ArrayList<FNFSection>();
            for(int i = 0; i < /*sections.length*/jsonSections.length(); i++) {
                JSONObject jsonS = jsonSections.getJSONObject(i);
                FNFSection s = new FNFSection();
                // System.out.println("meow");
                if(jsonS.has("startTime")) {
                    s.startTime = jsonS.getFloat("startTime");
                } else {
                    System.out.println("No startTime (i: " + i + ")");
                    continue;
                }
                if(jsonS.has("endTime") && !jsonS.isNull("endTime")) {
                    // Object o = jsonS.get("endTime");
                    // if(o != null) {
                    //     if(o instanceof JSONObject && ((JSONObject)o).)
                    //     try {
                            s.endTime = jsonS.getFloat("endTime");
                    //     } catch(NumberFormatException nfex) {
                    //         nfex.printStackTrace();
                    //         System.out.println("(nfex)\no.getClass(): " + o.getClass());
                    //     } catch(JSONException jsonex) {
                    //         jsonex.printStackTrace();
                    //         System.out.println("(jsonex)\no.getClass(): " + o.getClass());
                    //     } catch(Exception ex) {
                    //         ex.printStackTrace();
                    //         System.out.println("(ex)\no.getClass(): " + o.getClass());
                    //     }
                    // }
                } else {
                    System.out.println("no endTime (startTime: " + s.startTime + ")");
                }
                s.lengthInSteps = jsonS.getInt("lengthInSteps");
                s.bpm = jsonS.getInt("bpm");
                s.mustHitSection = jsonS.getBoolean("mustHitSection");
                JSONArray jsonNotes = jsonS.getJSONArray("sectionNotes");
                s.notes = new FNFNote[jsonNotes.length()];
                for(int j = 0; j < s.notes.length; j++) {
                    JSONArray jsonNote = jsonNotes.getJSONArray(j);
                    double time = jsonNote.getFloat(0);
                    byte lane = (byte)jsonNote.getInt(1);
                    double length = jsonNote.getDouble(2);
                    s.notes[j] = new FNFNote(lane, time, length);
                }
                java.util.TreeSet<FNFNote> sortedSet = new java.util.TreeSet<FNFNote>();
                java.util.Collections.addAll(sortedSet,s.notes);
                s.notes = sortedSet.toArray(s.notes);
                // System.out.println("note count: " + s.notes.length);
                //sections[i] = s;
                sections.add(s);
            }
            System.out.println(sections.size());
            String inFileName = inFile.getName();
            String outFileNameNoExt = inFileName + "_out_";
            String outFileName = outFileNameNoExt + "0.chart";
            File outFile = new File(outputDir, outFileName);
            if(outFile.exists()) {
                String outFileNameNoExtLowercase = outFileNameNoExt.toLowerCase();
                String[] thing = outputDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File parent, String filename) {
                        String lowercasedFilename = filename.toLowerCase();
                        return lowercasedFilename.startsWith(outFileNameNoExtLowercase) && lowercasedFilename.endsWith(".chart");
                    }
                });
                java.util.Arrays.sort(thing, new Comparator<String>(){
                    public int compare(String o1, String o2) {
                        return o2.compareTo(o1);
                    }
                });
                outFileName = outFileNameNoExt + (Integer.parseInt(thing[0].substring(thing[0].lastIndexOf("_out_")+5,thing[0].length()-".chart".length()))+1) + ".chart";
                outFile = new File(outputDir, outFileName);
            }
            PrintWriter pr = new PrintWriter(outFile);
            pr.write("\uFEFF"); // UTF-8 BOM
            pr.print("[Song]"); pr.print(Constants.chChartNewline);
            chPrintln(pr, "{");
            chPrintln(pr, "  Name = \""+songObj.getString("song") + "\"");
            chPrintln(pr, "  Artist = \""+songObj.getString("player1")+","+songObj.getString("player2")+(songObj.isNull("player3") ? "" : ","+songObj.getString("player3")) + "\"");
            chPrintln(pr, "  Album = \"unknown\"");
            chPrintln(pr, "  Year = \", 6969\"");
            chPrintln(pr, "  Offset = 0");
            chPrintln(pr, "  Resolution = " + Constants.defaultTicksPerBeat);
            chPrintln(pr, "  Player2 = bass");
            chPrintln(pr, "  Difficulty = 0");
            chPrintln(pr, "  PreviewStart = 0");
            chPrintln(pr, "  PreviewEnd = 0");
            chPrintln(pr, "  Genre = \"FNF\"");
            chPrintln(pr, "  MediaType = \"digital\"");
            String st = inFile.getAbsolutePath();
            st = st.substring(0,st.lastIndexOf("data/")) + "songs" + st.substring(st.lastIndexOf("data/")+4,st.lastIndexOf("/"));
            chPrintln(pr, "  MusicStream = \"" + st + "/Inst.ogg\"");
            if(new File(st + "/Voices.ogg").exists()) {
                chPrintln(pr, "  GuitarStream = \"" + st + "/Voices.ogg\"");
            }
            chPrintln(pr, "}");
            chPrintln(pr, "[SyncTrack]");
            chPrintln(pr, "{");
            chPrintln(pr, "  0 = TS 4");
            int initialBpm = 0;
            JSONArray eventObjs = songObj.getJSONArray("eventObjects");
            for (int i = 0; i < eventObjs.length(); i++) {
                JSONObject eventObj = eventObjs.getJSONObject(i);
                if(eventObj.getString("name").equals("Init BPM")) {
                    initialBpm = eventObj.getInt("value");   
                }
            }
            chPrintln(pr, "  0 = B " + initialBpm + "000");
            chPrintln(pr, "}");
            chPrintln(pr, "[Events]");
            chPrintln(pr, "{");
            chPrintln(pr, "}");
            System.out.println("TODO: add support for bpm changes and non-4/4-sections"); // TODO: ditto
            ArrayList<String>
                noteStringsLead = new ArrayList<String>(),  // both bf and opponent
                noteStringsCoop = new ArrayList<String>(),  // bf
                noteStringsRythm = new ArrayList<String>(); // opponent
            for(int i = 0; i < sections./*length*/size(); i++) {
                // FNFNote[] notes = sections[i].notes;
                FNFSection section = sections.get(i);
                FNFNote[] notes = section.notes;
                float bpm = /*sections[i].bpm*/ initialBpm; // TODO: ditto
                float bps = bpm/60;
                float tps = Constants.defaultTicksPerBeat*bps;
                float tpms=tps/1000;
                for(int j = 0; j < notes.length; j++) {
                    FNFNote note = notes[j];
                    int startTick = (int)Math.round(tpms*note.getTimeInMs());
                    int lane = (note.getLane() & 0b0011) + 1; // left-right gets mapped to red-orange
                    int sustainTickLength = (int)(tpms*note.getLengthInMs());
                    if(sustainTickLength < (tpms*75)) {
                        sustainTickLength=0;
                    }
                    String noteString = "  " + startTick + " = N " + lane + " " + sustainTickLength;
                    noteStringsLead.add(noteString);
                    // mustHitSection makes the left half bf's part, and the right half the opponent's part.
                    if(((note.getLane() & 0b0100) == 0b0100) == section.mustHitSection) {
                        noteStringsCoop.add(noteString);
                    } else {
                        noteStringsRythm.add(noteString);
                    }
                }
            }
            if(settings.enableStarPower) {
                generateStarPower(noteStringsLead);
                generateStarPower(noteStringsCoop);
                generateStarPower(noteStringsRythm);
            }
            
            // java.util.Collections.sort(noteStringsLead, noteStringComparator);
            // java.util.Collections.sort(noteStringsCoop, noteStringComparator);
            // java.util.Collections.sort(noteStringsRythm, noteStringComparator);
            printChSection(pr, 3, "Single", noteStringsLead);
            printChSection(pr, 3, "DoubleGuitar", noteStringsCoop);
            printChSection(pr, 3, "DoubleRhythm", noteStringsRythm);
            // chPrintln(pr, "[ExpertSingle]");
            // chPrintln(pr, "{");
            // for (String noteString : noteStrings) {
            //     chPrintln(pr, noteString);
            // }
            // chPrintln(pr, "}");
            pr.close();
            System.out.println("successfully wrote to " + outFile.getAbsolutePath());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    public static final Comparator<String> noteStringComparator = new Comparator<String>() {
        @Override
        public int compare(String arg0, String arg1) {
            int eqIdx0 = arg0.indexOf(" = "), eqIdx1 = arg1.indexOf(" = ");
            int typeDiff = ((arg0.charAt(eqIdx1+3) == 'S' ? 2 : 1) - (arg1.charAt(eqIdx1+3) == 'S' ? 2 : 1) + 1);
            assert(typeDiff >= 0);
            return ((Integer.parseInt(arg0.substring(2,eqIdx0)) - Integer.parseInt(arg1.substring(2,eqIdx1)))
            << 2) | ((arg0.charAt(eqIdx1+3) == 'S' ? 2 : 1) - (arg1.charAt(eqIdx1+3) == 'S' ? 2 : 1) + 1);
        }  
    };
    private static final String[] chDiffs = new String[]{"Easy", "Medium", "Hard", "Expert"};
    private void printChSection(PrintWriter pr, int diff, String fileSectionName, ArrayList<String> noteStrings) {
        java.util.Collections.sort(noteStrings, noteStringComparator);
        pr.print("[");
        pr.print(chDiffs[diff]);
        pr.print(fileSectionName);
        chPrintln(pr,"]");
        chPrintln(pr, "{");
        for (String noteString : noteStrings) {
            chPrintln(pr, noteString);
        }
        chPrintln(pr, "}");
    }
    public static void generateStarPower(ArrayList<String> noteStrings) {
        ArrayList<String> phraseStrings = new ArrayList<String>();
        java.util.Collections.sort(noteStrings, noteStringComparator);

        String firstNoteString = noteStrings.get(0);
        int previousNoteTick = Integer.parseInt(firstNoteString.substring(2,firstNoteString.indexOf(" = ")));
        String lastNoteString = noteStrings.get(noteStrings.size()-1);
        final int lastNoteTick = Integer.parseInt(lastNoteString.substring(2,lastNoteString.indexOf(" = ")));

        final int ticksPerBar = Constants.defaultTicksPerBeat*4;

        final int lastBarIdx = Utils.roundToCurrentBarIndex(lastNoteTick);
        final int lastBarStartTick = lastBarIdx*ticksPerBar;

        final int firstBarIdx = Utils.roundToCurrentBarIndex(previousNoteTick);
        final int firstBarStartTick = firstBarIdx*ticksPerBar;
        
        final int firstPhraseBarIdx = 6 + firstBarIdx;
        final int firstPhraseBarStartTick = firstPhraseBarIdx*ticksPerBar;

        // // all of this is for stuff like skipping over breaks (long empty sections) 
        // // which i'm going to add later
        // int nextPhraseBarIdx = firstBarIdx+10; // a phrase which is 2 bars long, every 10 bars, starting from the 6th bar
        // int currentBarIdx = (int)(Math.floor(((double)previousNoteTick)/ticksPerBar));
        // int currentBarStartTick = currentBarIdx*ticksPerBar;
        // int currentBarEndTick = currentBarStartTick+ticksPerBar;
        // for(int i = 1; i < noteStrings.size(); i++) {
        //     String noteString = noteStrings.get(i);
        //     int currentNoteTick = Integer.parseInt(noteString.substring(2,noteString.indexOf(" = ")));
        //     if(currentNoteTick >= currentBarEndTick) {
        //         currentBarIdx = (int)(Math.floor(((double)currentNoteTick)/ticksPerBar));
        //         currentBarStartTick = currentBarIdx*ticksPerBar;
        //         currentBarEndTick = currentBarStartTick+ticksPerBar;
        //     }
        //     previousNoteTick = currentNoteTick;
        // }
        String ticksPer2BarsString = ""+(ticksPerBar*2);
        for(int phraseBarIdx = firstPhraseBarIdx; phraseBarIdx < (lastBarIdx-4); phraseBarIdx += 10) {
            phraseStrings.add("  " + phraseBarIdx*ticksPerBar + " = S 2 " + ticksPer2BarsString);
        }
        noteStrings.addAll(phraseStrings);
        System.out.println("TODO: fix messy star power code");
    }

}