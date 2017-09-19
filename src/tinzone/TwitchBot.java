package tinzone;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Random;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import org.jibble.pircbot.*;

public class TwitchBot extends PircBot {
    
    private final Settings settings;
    
    boolean give = false; 
    boolean goodWord = true;
    
    long lastMidiTime = 0;
    
    public TwitchBot(Settings settings) throws IOException {
        this.settings = settings;
        this.setName("ClipArtBot");  
    }
    
    boolean goodWord(String word) throws FileNotFoundException, IOException {
        if (settings.bwfEnabled() == 0) {
            return true;
        }
        
        InputStream sw1 = new FileInputStream("swearWords.txt");
        InputStreamReader sw2 = new InputStreamReader(sw1, Charset.forName("UTF-8"));
        BufferedReader swearWords = new BufferedReader(sw2);
        String line;
        
        while ((line = swearWords.readLine()) != null) {
            if (word.contains(line + " ") || word.contains(" " + line) || word.equalsIgnoreCase(line)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        
        //converts message to all lower case
        String m = message.toLowerCase();
        
        if (m.startsWith("!get ") && settings.getEnabled() == 1) {
            String word = m.substring(5);
            try {
            goodWord = goodWord(word);
            } catch (IOException ex) {
            }
            if (goodWord) {
                try {
                    int randNum = 0;
                    if (settings.booruEnabled() == 1) {randNum++;}
                    if (settings.clipartEnabled() == 1) {randNum++;}
                    if (settings.deviantEnabled() == 1) {randNum++;}
                    if (settings.gifEnabled() == 1) {randNum++;}
                    if (settings.stockEnabled() == 1) {randNum++;}
                    
                    Random r = new Random();
                    int random = r.nextInt(randNum);
                    
                    if (settings.booruEnabled() == 1) {
                        random--;
                        if (random == -1) {
                            ClipArtBot.getBooru(word);
                        }
                    } 
                    
                    if (settings.clipartEnabled() == 1) {
                        random--;
                        if (random == -1) {
                            ClipArtBot.getClipart(word);
                        }
                    } 
                    
                    if (settings.deviantEnabled() == 1) {
                        random--;
                        if (random == -1) {
                            ClipArtBot.getDeviant(word);
                        }
                    } 
                    
                    if (settings.gifEnabled() == 1) {
                        random--;
                        if (random == -1) {
                            ClipArtBot.getGif(word);
                        }
                    } 
                    
                    if (settings.stockEnabled() == 1) {
                        random--;
                        if (random == -1) {
                            if (settings.useShutterstock() == 1) {
                                ClipArtBot.getShutterstock(word);
                            } else {
                                ClipArtBot.getStock(word);
                            }
                        }
                    } 
                } catch (Exception e) {
                }
            }
        }
        
        if (m.startsWith("!booru ") && settings.booruEnabled() == 1) {
            String word = m.substring(7);
            try {
            goodWord = goodWord(word);
            } catch (IOException ex) {
            }
            if (goodWord) {
                try {
                ClipArtBot.getBooru(word);
                } catch (Exception e) {
                }
            }
        }
        
        //displays clip art image with !clipart (word)
        if (m.startsWith("!clipart ") && settings.clipartEnabled() == 1) {
            String word = m.substring(9);
            try {
            goodWord = goodWord(word);
            } catch (IOException ex) {
            }
            if (goodWord) {
                try {
                ClipArtBot.getClipart(word);
                } catch (Exception e) {
                }
            }
        }
        
        if (m.startsWith("!deviant ") && settings.deviantEnabled() == 1) {
            String word = m.substring(9);
            String wordOrig = word;
            for(int i = 0; i < word.length(); i++){
                if (word.charAt(i) == ' ') {
                    String wordTmp = word.substring(i+1);
                    word = word.substring(0, i);
                    word = word.concat("%20" + wordTmp);
                }
            }
            
            try {
            goodWord = goodWord(wordOrig);
            } catch (IOException ex) {
            }
            if (goodWord) {
                try {
                ClipArtBot.getDeviant(word);
                } catch (Exception e) {
                }
            }
        }
        
        //displays clip art image with !clipart (word)
        if (m.startsWith("!gif ") && settings.gifEnabled() == 1) {
            String word = m.substring(5);
            String wordOrig = word;
            for(int i = 0; i < word.length(); i++){
                if (word.charAt(i) == ' ') {
                    String wordTmp = word.substring(i+1);
                    word = word.substring(0, i);
                    word = word.concat("%20" + wordTmp);
                }
            }
            
            try {
            goodWord = goodWord(wordOrig);
            } catch (IOException ex) {
            }
            if (goodWord) {
                try {
                ClipArtBot.getGif(word);
                } catch (Exception e) {
                }
            }
        }
        
        //displays clip art image with !clipart (word)
        if (m.startsWith("!stock ") && settings.stockEnabled() == 1) {
            String word = m.substring(7);
            String wordOrig = word;
            for(int i = 0; i < word.length(); i++){
                if (word.charAt(i) == ' ') {
                    String wordTmp = word.substring(i+1);
                    word = word.substring(0, i);
                    word = word.concat("%20" + wordTmp);
                }
            }
            
            try {
            goodWord = goodWord(wordOrig);
            } catch (IOException ex) {
            }
            if (goodWord) {
                try {
                    if (settings.useShutterstock() == 1) {
                        ClipArtBot.getShutterstock(word);
                    } else {
                        ClipArtBot.getStock(word);
                    }
                } catch (Exception e) {
                }
            }
        }
        
        if (m.startsWith("!butt") && sender.equals("buttsbot") && channel.equals("#tininsteelian") && give == false) {
            System.out.println("a");
            give = true;
            ClipArtBot.giveArray.clear();
            sendMessage("#" + settings.twitchChannel(), "Buttsbot started a giveaway! A free Steam game key is up for grabs! Type '!enter' to enter. The giveaway will automatically end in 60 seconds.");
            
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        give = false;
                        String winner = ClipArtBot.giveawayEnd();
                        sendMessage("#" + settings.twitchChannel(), winner + " won the giveaway, congratulations! You should PM tininsteelian to remind him to give you your Steam key after the stream.");
                    }
                }, 
                60000 
            );
        }
        
        if (m.equalsIgnoreCase("!kill") && sender.equals(settings.twitchChannel())) {
            try {
                ClipArtBot.getClipart("sorry");
            } catch (Exception e) {
            }
        }
        
        if (m.equalsIgnoreCase("!giveaway") && sender.equals(settings.twitchChannel()) && give == false) {
            give = true;
            ClipArtBot.giveArray.clear();
            sendMessage("#" + settings.twitchChannel(), "A giveaway has started! Type '!enter' to enter the giveaway!");
        }
        
        if (m.equalsIgnoreCase("!enter") && give == true) {
            if (ClipArtBot.giveaway(sender)) {
                //sendMessage("#" + settings.twitchChannel(), sender + ": You have been entered into the giveaway."); 
            }
        }
        
        if (m.equalsIgnoreCase("!end") && sender.equals(settings.twitchChannel()) && give == true) {
            give = false;
            String winner = ClipArtBot.giveawayEnd();
            if (channel.equals("#tininsteelian")) {
                sendMessage("#" + settings.twitchChannel(), winner + " won the giveaway, congratulations! TacoHappy");
            } else {
                sendMessage("#" + settings.twitchChannel(), winner + " won the giveaway, congratulations!");
            }
        }
        
        if (m.equalsIgnoreCase("!clipabout")) {
            sendMessage("#" + settings.twitchChannel(), "I'm ClipArtBot! Type '!clipart [word]' in the chat and a clip art image of [word] will appear on the stream.");
        }
        
        if (m.equalsIgnoreCase("!url")) {
            sendMessage("#" + settings.twitchChannel(), ClipArtBot.lastPic);
        }
        
        if (channel.equals("#tininsteelian")) {
            
            if (m.equalsIgnoreCase("clipartbot yes")) {
                sendMessage("#" + settings.twitchChannel(), "TacoHappy");
            }

            if (m.equalsIgnoreCase("clipartbot no")) {
                sendMessage("#" + settings.twitchChannel(), "TacoSad");
            }

            if (m.equalsIgnoreCase("clipartbot why")) {
                sendMessage("#" + settings.twitchChannel(), "TacoKappa");
            }

            if (m.equalsIgnoreCase("clipartbot die")) {
                sendMessage("#" + settings.twitchChannel(), "TacoDead");
                try {
                    Thread.sleep(42069/10);
                } catch (Exception e) {
                }
                sendMessage("#" + settings.twitchChannel(), "I refuse to die! TacoAngry");
            }

            if (m.startsWith("hey, tin") || m.startsWith("hey tin") || m.startsWith("hey, clip") || m.startsWith("hey clip")) {
                sendMessage("#tininsteelian", "Hi " + sender + "! TacoHeart");
            }

            if (m.contains("good night")) {
                sendMessage("#" + settings.twitchChannel(), "TacoSleep");
             }

            if (m.contains("excellent")) {
                sendMessage("#" + settings.twitchChannel(), "AAAAA +");
            }
            
            if (m.contains("just subscribed")) {
                sendMessage("#" + settings.twitchChannel(), "tinSub Thanks for subscribing! tinSub");
            }
            
            if (m.startsWith("!midi ") && settings.playMidis() == 1) {
                    String word = m.substring(6);
                    try {
                    goodWord = goodWord(word);
                    } catch (IOException ex) {
                    }
                    if (goodWord) {
                        try {
                            String result = "this should never be seen";
                            if (System.currentTimeMillis() < lastMidiTime + 60000) {
                                result = Midis.playMidi(word, 0);
                            } else {
                                result = Midis.playMidi(word, 1);
                            }

                            if (System.currentTimeMillis() < lastMidiTime + 60000) {
                                long timeLeft = 60 - ((System.currentTimeMillis() - lastMidiTime)/1000);
                                sendMessage("#" + settings.twitchChannel(), "Please wait " + timeLeft + " more seconds.");
                            } else if (result.equals("0")) {
                                sendMessage("#" + settings.twitchChannel(), "No midis found.");
                            } else {
                                lastMidiTime = System.currentTimeMillis();
                                sendMessage("#" + settings.twitchChannel(), "Now playing " + result);
                            }
                        } catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
                        }
                    }
                }
            }

            if (m.equalsIgnoreCase("bee") || m.equalsIgnoreCase("bee.") || m.equalsIgnoreCase("bee!") || m.equalsIgnoreCase("bee?")){
                sendMessage("#" + settings.twitchChannel(), "BBBBB");
    //            try {
    //                ClipArtBot.beeRepainter();
    //            } catch (IOException ex) {
    //                Logger.getLogger(TwitchBot.class.getName()).log(Level.SEVERE, null, ex);
    //            }
            }
        }
    }
