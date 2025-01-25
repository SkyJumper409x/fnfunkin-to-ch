package xyz.skyjumper409.fnftoch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.TreeMap;

import org.json.*;

import xyz.skyjumper409.fnftoch.fnf.FNFNote;
import xyz.skyjumper409.fnftoch.fnf.FNFSection;
import static xyz.skyjumper409.fnftoch.Utils.chPrintln;

public class MyMain {
    private static boolean enableStarPower = true;
    public static void main(String[] args) {
        if(args.length < 1) {
            Utils.die("not enough arguments (missing input file argument)");
        }
        File file = new File(args[0]);
        if(!file.isFile()) {
            System.err.println("file not found");
            System.exit(1);
        }
        String workingDirpath = System.getProperty("user.dir");
        File outputDir = null;
        for(int i = 1; i < args.length; i++) {
            if(args[i].indexOf("--output-dir=") != -1) {
                if(outputDir != null) {
                    Utils.die("--output-dir= was specified multiple times");
                }
                String outDirPath = args[i].substring("--output-dir=".length());
                if(!outDirPath.startsWith("/") && !(
                    (outDirPath.charAt(0)+"").matches("[A-Z]") && outDirPath.charAt(1) == ':' && "/\\".contains(outDirPath.charAt(2)+"")
                )) {
                    outDirPath = workingDirpath + "/" + outDirPath;
                }
                outputDir = new File(outDirPath);
            }
            switch(args[i]) {
                case "--noStarpower":
                    enableStarPower = false;
                    continue;
            }
        }
        if(outputDir == null) {
            outputDir = new File(workingDirpath);
        }
        if(!outputDir.isDirectory()) {
            Utils.die("output directory doesn't exist or isn't a directory");
        }
        new MyMain().go(file, outputDir);
    }
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
            chPrintln(pr, "  Artist = \""+songObj.getString("player1")+","+songObj.getString("player2")+","+songObj.getString("player3") + "\"");
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
            if(enableStarPower) {
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
            return Integer.parseInt(arg0.substring(2,arg0.indexOf(" = "))) - Integer.parseInt(arg1.substring(2,arg1.indexOf(" = ")));
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
    private void generateStarPower(ArrayList<String> noteStrings) {
        System.out.println("TODO: add starpower");
    }
}