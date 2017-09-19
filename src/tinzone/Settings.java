package tinzone;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.swing.JOptionPane;

public class Settings {
    
    private String oauthCode = "";
    private String server = "";
    private int port = 0;
    private String twitchChannel = "";
    private String font = "";
    private int fontSize = 0;
    private int getEnabled = 0;
    private int clipartEnabled = 0;
    private int stockEnabled = 0;
    private int useShutterstock = 0;
    private int gifEnabled = 0;
    private int deviantEnabled = 0;
    private int deviantQuickMode = 0;
    private int booruEnabled = 0;
    private int bwfEnabled = 0;
    private int savedImagesEnabled = 0;
    private String savedImagesFilepath = "";
    private int rtrEnabled = 0;
    private String dumpFilepath = "";
    private String loadFilepath = "";
    private int numTexturesToReplace = 0;
    private int recheckDumpFolder = 0;
    private int flipTextures = 0;
    private int loadFromFolder = 0;
    private String folderFilepath = "";
    private int playMidis = 0;
    private String midiFolderFilepath = "";
    private int autoplay = 0;
    private int customSoundfont = 0;
    private String soundfontFilepath = "";
    
    public Settings() throws FileNotFoundException, IOException {
        InputStream s1 = new FileInputStream("settings.txt");
        InputStreamReader s2 = new InputStreamReader(s1, Charset.forName("UTF-8"));
        BufferedReader settings = new BufferedReader(s2);

        for (int i = 0; i < 6; i++) {
            settings.readLine(); 
        }
        
        try {
            oauthCode = settings.readLine().substring(12);
            server = settings.readLine().substring(8);
            port = Integer.parseInt(settings.readLine().substring(6));
            twitchChannel = settings.readLine().substring(16);
            settings.readLine();
            settings.readLine();
            font = settings.readLine().substring(6);
            fontSize = Integer.parseInt(settings.readLine().substring(11));
            settings.readLine();
            settings.readLine();
            getEnabled = Integer.parseInt(settings.readLine().substring(14));
            clipartEnabled = Integer.parseInt(settings.readLine().substring(18));
            stockEnabled = Integer.parseInt(settings.readLine().substring(16));
            useShutterstock = Integer.parseInt(settings.readLine().substring(18));
            gifEnabled = Integer.parseInt(settings.readLine().substring(14));
            deviantEnabled = Integer.parseInt(settings.readLine().substring(18));
            deviantQuickMode = Integer.parseInt(settings.readLine().substring(21));
            booruEnabled = Integer.parseInt(settings.readLine().substring(16));
            bwfEnabled = Integer.parseInt(settings.readLine().substring(25));
            savedImagesEnabled = Integer.parseInt(settings.readLine().substring(22));
            savedImagesFilepath = settings.readLine().substring(23);
            settings.readLine();
            settings.readLine();
            rtrEnabled = Integer.parseInt(settings.readLine().substring(29));
            dumpFilepath = settings.readLine().substring(23);
            loadFilepath = settings.readLine().substring(23);            
            numTexturesToReplace = Integer.parseInt(settings.readLine().substring(31));
            recheckDumpFolder = Integer.parseInt(settings.readLine().substring(21));
            flipTextures = Integer.parseInt(settings.readLine().substring(15));
            settings.readLine();
            settings.readLine();
            loadFromFolder = Integer.parseInt(settings.readLine().substring(27));
            folderFilepath = settings.readLine().substring(17);
            settings.readLine();
            settings.readLine();
            playMidis = Integer.parseInt(settings.readLine().substring(12));
            midiFolderFilepath = settings.readLine().substring(22);
            autoplay = Integer.parseInt(settings.readLine().substring(10));
            customSoundfont = Integer.parseInt(settings.readLine().substring(18));
            soundfontFilepath = settings.readLine().substring(20);
            
        } catch(StringIndexOutOfBoundsException siobe) {
            System.out.println("invalid input");
            Component frame = null;
            JOptionPane.showMessageDialog(frame, "ClipArtBot is very picky and wants you to know that you deleted the space after the colon on one of the lines in settings.txt. \n This will prevent things from working properly. The next update should make ClipArtBot less picky.");
        }
    }
 
    public String oauthCode() {return oauthCode;}
    public String server() {return server;}
    public int port() {return port;}
    public String twitchChannel() {return twitchChannel;}
    public String font() {return font;}
    public int fontSize() {return fontSize;}
    public int getEnabled() {return getEnabled;}
    public int clipartEnabled() {return clipartEnabled;}
    public int stockEnabled() {return stockEnabled;}
    public int useShutterstock() {return useShutterstock;}
    public int gifEnabled() {return gifEnabled;}
    public int deviantEnabled() {return deviantEnabled;}
    public int deviantQuickMode() {return deviantQuickMode;}
    public int booruEnabled() {return booruEnabled;}
    public int bwfEnabled() {return bwfEnabled;}
    public int savedImagesEnabled() {return savedImagesEnabled;}
    public String savedImagesFilepath() {return savedImagesFilepath;}
    public int rtrEnabled() {return rtrEnabled;}
    public String dumpFilepath() {return dumpFilepath;}
    public String loadFilepath() {return loadFilepath;}
    public int numTexturesToReplace() {return numTexturesToReplace;}
    public int recheckDumpFolder() {return recheckDumpFolder;}
    public int flipTextures() {return flipTextures;}
    public int loadFromFolder() {return loadFromFolder;}
    public String folderFilepath() {return folderFilepath;}
    public int playMidis() {return playMidis;}
    public String midiFolderFilepath() {return midiFolderFilepath;}
    public int autoplay() {return autoplay;}
    public int customSoundfont() {return customSoundfont;}
    public String soundfontFilepath () {return soundfontFilepath;}
}
