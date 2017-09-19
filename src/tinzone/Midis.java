package tinzone;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

public class Midis {   
    
    private static Settings settings;
    
    public static File midiLoadFolder;
    public static File[] midiListOfFiles;   
    
    public static Sequencer sequencer;
    
    public Midis(Settings settings) throws Exception {   
        Midis.settings = settings;

        if (settings.playMidis() == 1) {
            midiLoadFolder = new File(settings.midiFolderFilepath());
            midiListOfFiles = midiLoadFolder.listFiles();
        }
        
        sequencer = MidiSystem.getSequencer(false);
        
        if (settings.playMidis() == 1 && settings.autoplay() == 1) {
            playMidi("random", 1);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!sequencer.isRunning()) {
                        try {
                            playMidi("random", 1);
                        } catch (MidiUnavailableException | IOException | InvalidMidiDataException ex) {
                            Logger.getLogger(Midis.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }, 0, 1000);
        }     
    }
    
    public static String playMidi(String word, int timeLimit) throws MidiUnavailableException, FileNotFoundException, IOException, InvalidMidiDataException, EOFException {
        
        int numFiles = midiListOfFiles.length;
        int[] acceptableMidis = new int[numFiles];
        int j = 0;
        for (int i = 0; i < numFiles; i++) {
            if (midiListOfFiles[i].getName().toLowerCase().contains(word.toLowerCase())) {
                acceptableMidis[j] = i;
                j++;
            }
        }
        
        Random r = new Random();
        String filename;
        String pathName;
        if (word.equals("random")) {
            int rand = r.nextInt(numFiles);
            filename = midiListOfFiles[rand].getName();
            pathName = settings.midiFolderFilepath() + filename;
        } else if (j > 0) {
            int rand = r.nextInt(j);
            filename = midiListOfFiles[acceptableMidis[rand]].getName();
            pathName = settings.midiFolderFilepath() + filename;
        } else {
            return "0";
        }
        //System.out.println(j + " - " + filename);
        
        if (timeLimit == 1) {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            if (settings.customSoundfont() == 1) {     
                synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
                synthesizer.loadAllInstruments(MidiSystem.getSoundbank(new File(settings.soundfontFilepath())));
            }

            sequencer.close();
            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
            sequencer.open();
            Sequence sequence = MidiSystem.getSequence(new File(pathName));
            sequencer.setSequence(sequence);
            sequencer.start();

            int lengthInSeconds = (int)(sequencer.getMicrosecondLength()/1000000);
            int minutes = lengthInSeconds/60;
            int seconds = lengthInSeconds%60;
            String time = minutes + ":" + String.format("%02d", seconds);

            File logFile = new File("lastMidi.txt");
            try (PrintWriter log_file_writer = new PrintWriter(logFile)) {
                log_file_writer.println(filename + " (" + time + ")        ");
            }
        }

        return(filename);
    }
}