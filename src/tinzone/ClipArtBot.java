package tinzone;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.pontus.gifexport.GifWriter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import static java.awt.Image.SCALE_SMOOTH;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;
//import org.imgscalr.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class ClipArtBot extends JFrame {
    
    private static Settings settings;
    
    public static JLayeredPane layeredPane;
    public static JLabel clipartLabel;
    public static JLabel clipartLabel2;
    public static JLabel clipartLabel3;
    public static JLabel clipartLabel4;
    public static JLabel clipartLabel5;
    public static JLabel clipartLabel6;
    public static JLabel textLabel;
    public static JLabel textLabel2;
    public static JLabel textLabel3;
    public static JLabel textLabel4;
    public static JLabel textLabel5;
    public static JLabel textLabel6;
    public static int counter = 0;
    
    public static String font;
    public static int fontSize;
    
    public static ArrayList giveArray = new ArrayList();
    public static int imageNum = 0;    

    public static File loadFolder;
    public static File[] listOfFiles;
    public static File loadFolderStart;
    public static File[] listOfFilesStart;
    
    public static String tokenDeviant;
    public static String tokenStock;
    public static int curHourDeviant = -1;
    public static int curHourStock = -1;
    
    public static String lastPic;

    public ClipArtBot(Settings settings) throws Exception {
        this.settings = settings;
        
        if (settings.rtrEnabled() == 1) {
            loadFolder = new File(settings.dumpFilepath());
            listOfFiles = loadFolder.listFiles();
        }
        
        if (settings.loadFromFolder() == 1) {
            loadFolder = new File(settings.dumpFilepath());
            listOfFiles = loadFolder.listFiles();
            loadFolderStart = new File(settings.folderFilepath());
            listOfFilesStart = loadFolderStart.listFiles();
        }
               
        this.setSize(283, 295); 
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); 
        this.setVisible(true); 
        this.setTitle("ClipArtBot");
        this.setLayout(null);
        this.getContentPane().setBackground(Color.white);
        ClipArtBot.layeredPane = new JLayeredPane();
        layeredPane.setSize(700, 700);
        layeredPane.setLocation(0, 0);
        layeredPane.setVisible(true);
        this.add(layeredPane);
        ImageIcon background = new ImageIcon("background.png");
        JLabel backgroundLabel = new JLabel(background);
        layeredPane.add(backgroundLabel, new Integer(0));
        
        font = settings.font();
        fontSize = settings.fontSize();
    }
    
    public static void getBooru(String word) throws Exception {
        reset();
        displayMessage("Loading new image...");
        
        String siteURL = "http://safebooru.org/index.php?page=post&s=list&tags=";
        siteURL = siteURL.concat(word);
        
        Document doc = Jsoup.connect(siteURL).get();
        Elements link = doc.getElementsByTag("img");
        //System.out.println(link);
        
        if(link.size() > 2) {
            Random r = new Random();
            int random = r.nextInt(link.size()-2)+1;
            String pictureURL = link.get(random).attr("src");
            pictureURL = "http://" + pictureURL.substring(2, pictureURL.indexOf("?"));
            lastPic = pictureURL;
            
            //gets picture from URL, resizes it if necessary, and displays it
            URL url = new URL(pictureURL);
            BufferedImage bi = ImageIO.read(url);
            ImageIcon booru = new ImageIcon(bi);
            Image booruImage = booru.getImage();
            
            int width = bi.getWidth();
            int height = bi.getHeight();
            if (width - height > 49) {
                    booruImage = booruImage.getScaledInstance(width*267/150, height*267/150, SCALE_SMOOTH);
                } else {
                    booruImage = booruImage.getScaledInstance(width*200/150, height*200/150, SCALE_SMOOTH);
                }
            ImageIcon clipartFinal = new ImageIcon(booruImage);
            display(clipartFinal);
            displayMessage("\"" + word + "\"");

            //saves picture
            save(bi);

            //repaints picture
            realTimeRepainter(bi);
        } else {
            //no results, displays no clip art message
            reset();
            clipartLabel = new JLabel("<html>No booru images found. :(</html>");
            displayText();
            displayMessage("\"" + word + "\"");
        }
    }
    
    public static void getClipart(String word) throws Exception {     
        reset();
        displayMessage("Loading new image...");
        
        String jsonURL = "https://openclipart.org/search/json/?query=";
        jsonURL = jsonURL.concat(word);

        //gets JSON
        try {
            JSONObject json = readJsonFromUrl(jsonURL);

            //if query is successful, picture-getting begins
            if (json.get("msg").equals("success")) {
        
                //gets number of available pictures
                JSONObject info = new JSONObject(json.getString("info"));
                int results = (Integer) info.get("results");
                
                //if there is a result, display a clip art image
                if (results > 0) {
                    
                    //picks random clip art image from list
                    Random r = new Random();
                    int random = r.nextInt(10);
                    if (results <= 10) {
                        random = r.nextInt(results);
                    } else {
                        int pages = (Integer) info.get("pages");
                        int randomPage = 1 + r.nextInt(pages);
                        jsonURL = jsonURL.concat("&page=" + randomPage);
                        json = readJsonFromUrl(jsonURL);
                    }

                    //gets URL of picture
                    JSONArray arr = json.getJSONArray("payload");
                    JSONObject svg = new JSONObject(arr.getJSONObject(random).getString("svg"));
                    JSONObject dimensions = new JSONObject(arr.getJSONObject(random).getString("dimensions"));
                    JSONObject png_thumb = new JSONObject(dimensions.getString("png_thumb"));
                    int width = (int) png_thumb.get("width");
                    int height = (int) png_thumb.get("height");
                    String pictureURL = (String) svg.get("png_thumb");
                    lastPic = pictureURL;

                    //gets picture from URL, resizes it if necessary, and displays it
                    URL url = new URL(pictureURL);
                    BufferedImage bi = ImageIO.read(url);
                    ImageIcon clipart = new ImageIcon(bi);
                    Image clipartImage = clipart.getImage();
                    if (height > 200) {
                        clipartImage = clipartImage.getScaledInstance(width*200/250, height*200/250, SCALE_SMOOTH);
                    }
                    ImageIcon clipartFinal = new ImageIcon(clipartImage);
                    display(clipartFinal);
                    displayMessage("\"" + word + "\"");
                    
                    //saves picture
                    save(bi);
                    
                    //repaints picture
                    realTimeRepainter(bi);
                } else {
                    //no results, displays no clip art message
                    reset();
                    clipartLabel = new JLabel("<html>No clip art found. :(</html>");
                    displayText();
                    displayMessage("\"" + word + "\"");
                }
            } else {
                reset();
                wrong();
                displayMessage("\"" + word + "\"");
            }
        } catch (IOException | JSONException e) {
            reset();
            wrong();
            displayMessage("\"" + word + "\"");
        }
    }
    
    public static void getDeviant(String word) throws Exception {
        reset();
        displayMessage("Loading new image...");
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); 
        int hour = cal.get(Calendar.HOUR);
        if (hour != curHourDeviant) {
            curHourDeviant = hour;
        
            String grant = "https://www.deviantart.com/oauth2/token?grant_type=client_credentials&client_id=4304&client_secret=9337cfc66430235aa50953f1807c05dd";
            try {
                JSONObject json = readJsonFromUrl(grant);
                System.out.println("GRANTED!");

                if (json.get("status").equals("success")) {
                    tokenDeviant = (String) json.get("access_token");
                }
            } catch (IOException | JSONException e) {
                System.out.println(e);
                reset();
                wrong();
                displayMessage("\"" + word + "\"");
            }
        }
 
        String jsonURL = "https://www.deviantart.com/api/v1/oauth2/browse/newest?q=".concat(word).concat("&limit=50&access_token=").concat(tokenDeviant);
        System.out.println(jsonURL);
//        URL url2 = new URL(jsonURL);
//        HttpURLConnection con = (HttpURLConnection)url2.openConnection();
//        con.setRequestMethod("GET");
//        con.setConnectTimeout(9000);
//        con.setReadTimeout(9000);
//        con.connect();
//        //int code = con.getResponseCode();
//        System.out.println("RESPONSE");
//        
//        System.out.println("CODE: " + code);
//        InputStream in = con.getInputStream();
//        String encoding = con.getContentEncoding();
//        encoding = encoding == null ? "UTF-8" : encoding;
//        String body = IOUtils.toString(in, encoding);
//        System.out.println("body: " + body);
//        System.out.println("jsonURL: " + jsonURL);

        JSONObject json2 = readJsonFromUrl(jsonURL);
        System.out.println("JSON GET!");
        //System.out.println("json: " + json2);
        //System.out.println("******************");
        String estTotalStr = json2.getString("estimated_total");
        int estTotal = (Integer.parseInt(estTotalStr));  

        if (estTotal > 0) {                   
            //picks random clip art image from list
            Random r = new Random();
            int offset;
            int random = 0;

            if (settings.deviantQuickMode() == 0) {
                if (estTotal >= 40000) {
                    offset = r.nextInt(40000);
                } else {
                    offset = r.nextInt(estTotal);
                }
                jsonURL = jsonURL.concat("&offset=" + offset);
                json2 = readJsonFromUrl(jsonURL);      
            } else {
                if (estTotal >= 24) {
                    random = r.nextInt(24);
                } else {
                    random = r.nextInt(estTotal);
                }
            }
            //System.out.println(jsonURL);

            //gets URL of picture
            JSONArray arr = json2.getJSONArray("results");
            JSONArray thumbs = arr.getJSONObject(random).getJSONArray("thumbs");
            String mature = arr.getJSONObject(random).getString("is_mature");

            if(mature.equals("false")) {
                if(thumbs.length()>0) {
                    String pictureURL = thumbs.getJSONObject(1).getString("src");
                    lastPic = pictureURL;

                    //gets picture from URL, resizes it if necessary, and displays it
                    URL url = new URL(pictureURL);
                    System.out.println(url);
                    BufferedImage bi = ImageIO.read(url);
                    System.out.println("PICTURE GET!");                  
                    ImageIcon clipart = new ImageIcon(bi);
                    Image clipartImage = clipart.getImage();
                    ImageIcon clipartFinal = new ImageIcon(clipartImage);
                    display(clipartFinal);
                    displayMessage("\"" + word + "\"");

                    //saves picture
                    save(bi);

                    //repaints picture
                    realTimeRepainter(bi);

                } else {
                    reset();
                    clipartLabel = new JLabel("<html>Requested art was not<br>an image. :(</html>");
                    displayText();
                    displayMessage("\"" + word + "\"");
                }
            } else {
                reset();
                clipartLabel = new JLabel("<html>Requested art was<br>explicit. :(</html>");
                displayText();
                displayMessage("\"" + word + "\"");
            } 
        } else {
            //no results, displays no clip art message
            reset();
            clipartLabel = new JLabel("<html>No art found. :(</html>");
            displayText();
            displayMessage("\"" + word + "\"");
        }
    }    
    
    public static void getGif(String word) throws Exception {
        reset();
        displayMessage("Loading new image...");
        
        String jsonURL = "https://api.tenor.co/v1/search?tag=";
        jsonURL = jsonURL.concat(word);
        jsonURL = jsonURL.concat("&key=LIVDSRZULELA");
        
        //gets JSON
        try {
            JSONObject json = readJsonFromUrl(jsonURL);
            JSONArray arr = json.getJSONArray("results");       
            
            //if there is a result, display a stock image
            if (arr.length() != 0) {
                              
                //picks random stock image from list
                Random r = new Random();
                int random = r.nextInt(arr.length());

                //gets URL of picture
                JSONObject randObj = new JSONObject(arr.getJSONObject(random).toString());
                JSONArray media = new JSONArray(randObj.getJSONArray("media").toString());
                JSONObject tinygif = new JSONObject(media.getJSONObject(0).get("tinygif").toString());     
                String pictureURL = (String) tinygif.get("url");   
                lastPic = pictureURL;
                
                //gets picture from URL and displays it   
                URL url = new URL(pictureURL);
                System.out.println(url);
                BufferedImage bi = ImageIO.read(url);
                //BufferedImage outputImage = Scalr.resize(bi, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 200);
                ImageIcon gif = new ImageIcon(url);
                display(gif);
                displayMessage("\"" + word + "\""); 

                //saves picture in .png format
                save(bi);

                //saves picture in .gif format
                GifWriter.saveImage(url.toString(), settings.savedImagesFilepath() + imageNum + ".gif");
                imageNum++;

                //repaints picture
                realTimeRepainter(bi);
            }
            else {
                //no results, displays no gif message
                reset();
                clipartLabel = new JLabel("<html>No gifs found. :(</html>");
                displayText();
                displayMessage("\"" + word + "\"");
            }
        } catch (IOException | JSONException e) {
            reset();
            System.out.println(e);
            wrong();
            displayMessage("\"" + word + "\"");
        }
    }
    
    public static void getStock(String word) throws Exception {
        reset();
        displayMessage("Loading new image...");
        
        String jsonURL = "http://api.bigstockphoto.com/2/803111/search/?q=";
        jsonURL = jsonURL.concat(word);
        
        //gets JSON
        try {
            JSONObject json = readJsonFromUrl(jsonURL);
            
            //if query is successful, picture-getting begins
            if ( (Integer) json.get("response_code") == 200) {
                
                //gets number of available pictures
                JSONObject data = new JSONObject(json.getString("data"));
                JSONObject paging = new JSONObject(data.getString("paging"));
                int total_items = (Integer) paging.get("total_items");
                
                //if there is a result, display a stock image
                if (total_items > 0) {
                    
                    //picks random stock image from list
                    Random r = new Random();
                    int random = r.nextInt(50);
                    if (total_items <= 50) {
                        random = r.nextInt(total_items);
                    } else {
                        int total_pages = (Integer) paging.get("total_pages");
                        int randomPage = 1 + r.nextInt(total_pages);
                        jsonURL = jsonURL.concat("&page=" + randomPage);
                        json = readJsonFromUrl(jsonURL);
                        data = new JSONObject(json.getString("data"));
                    }

                    //gets URL of picture
                    JSONArray arr = data.getJSONArray("images");
                    JSONObject small_thumb = new JSONObject(arr.getJSONObject(random).getString("small_thumb"));
                    String pictureURL = (String) small_thumb.get("url");
                    lastPic = pictureURL;
                    int width = (int) small_thumb.get("width");
                    int height = (int) small_thumb.get("height");
                    
                    //gets picture from URL, resizes it if necessary, and displays it
                    URL url = new URL(pictureURL);
                    BufferedImage bi = ImageIO.read(url);
                    ImageIcon stock = new ImageIcon(bi);
                    Image stockImage = stock.getImage();
                    if (width - height > 56) {
                        stockImage = stockImage.getScaledInstance(width*267/170, height*267/170, SCALE_SMOOTH);
                    } else {
                        stockImage = stockImage.getScaledInstance(width*200/170, height*200/170, SCALE_SMOOTH);
                    }
                    ImageIcon stockFinal = new ImageIcon(stockImage);
                    display(stockFinal);
                    displayMessage("\"" + word + "\"");
                    
                    //saves picture
                    save(bi);
                    
                    //repaints picture
                    realTimeRepainter(bi);         
                }                        
            } 
        } catch (IOException | JSONException e) {
            reset();
            wrongStock(word);
            displayMessage("\"" + word + "\"");
        }
    }
    
    public static void getShutterstock (String word) throws Exception {  
        reset();
        displayMessage("Loading new image...");
        
        Webb webb = Webb.create();
        JSONObject json;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); 
        int hour = cal.get(Calendar.HOUR);

        if (hour != curHourStock) {
            curHourStock = hour;
             
            Response<JSONObject> accessToken = webb.post("https://api.shutterstock.com/v2/oauth/access_token")
                .param("grant_type", "client_credentials")
                .param("client_id", "aa8754624ec4633b1a06")
                .param("client_secret", "09902108a99087113a2d699ca37b96dfb07ee75d")
                .ensureSuccess()
                .asJsonObject();
            json = accessToken.getBody();
            tokenStock = (String) json.get("access_token");
        }
        
        String auth = "Basic YWE4NzU0NjI0ZWM0NjMzYjFhMDY6MDk5MDIxMDhhOTkwODcxMTNhMmQ2OTljYTM3Yjk2ZGZiMDdlZTc1ZA==";
        webb.setDefaultHeader(Webb.HDR_AUTHORIZATION, auth);
        Response<JSONObject> response = webb.get("https://api.shutterstock.com/v2/images/search?per_page=1&view=full&query=" + word)
            .ensureSuccess()
            .asJsonObject();
        json = response.getBody();  
        
        String totalStr = json.getString("total_count");
        int total = (Integer.parseInt(totalStr));

        if (total > 0) {                   
            //picks random clip art image from list
            Random r = new Random();
            int random;
            if (total > 2000) {
                random = r.nextInt(2000);
            } else {
                random = r.nextInt(total);
            }
            
            Response<JSONObject> response2 = webb.get("https://api.shutterstock.com/v2/images/search?per_page=1&view=full&query=" + word + "&page=" + random)
                .ensureSuccess()
                .asJsonObject();
            json = response2.getBody();      
        
            //gets URL of picture
            JSONArray data = json.getJSONArray("data");
            String mature = (String) data.getJSONObject(0).getString("is_adult");
            JSONObject assets = new JSONObject(data.getJSONObject(0).getString("assets"));
            JSONObject large_thumb = assets.getJSONObject("large_thumb");
            String pictureURL = (String) large_thumb.get("url");
            lastPic = pictureURL;
            int height = (int) large_thumb.get("height");
            int width = (int) large_thumb.get("width");

            if (mature == "false") {
                //gets picture from URL, resizes it if necessary, and displays it
                URL url = new URL(pictureURL);
                BufferedImage bi = ImageIO.read(url);
                ImageIcon stock = new ImageIcon(bi);
                Image stockImage = stock.getImage();
                if (width - height > 49) {
                    stockImage = stockImage.getScaledInstance(width*267/150, height*267/150, SCALE_SMOOTH);
                } else {
                    stockImage = stockImage.getScaledInstance(width*200/150, height*200/150, SCALE_SMOOTH);
                }
                ImageIcon stockFinal = new ImageIcon(stockImage);
                display(stockFinal);
                displayMessage("\"" + word + "\"");

                //saves picture
                save(bi);

                //repaints picture
                realTimeRepainter(bi);
            } else {
                reset();
                clipartLabel = new JLabel("<html>Requested art was<br>explicit. :(</html>");
                displayText();
                displayMessage("\"" + word + "\"");
            }
        } else {
            //no results, displays no gif message
            reset();
            clipartLabel = new JLabel("<html>No stock images found. :(</html>");
            displayText();
            displayMessage("\"" + word + "\"");
        }      
    }
    
    public static void display(ImageIcon ii) {
        reset();
        clipartLabel = new JLabel(ii);
        clipartLabel.setSize(267, 200);
        clipartLabel.setLocation(0, 0);
        clipartLabel.setVisible(true);
        layeredPane.add(clipartLabel, new Integer (1));
        counter++;
    }
    
    public static void displayText() {
        //reset();
        
//        clipartLabel2 = clipartLabel;
//        clipartLabel3 = clipartLabel;
//        clipartLabel4 = clipartLabel;
//        clipartLabel5 = clipartLabel;
//        clipartLabel6 = clipartLabel;
        
        clipartLabel.setFont(new Font(font, Font.PLAIN, fontSize));
        clipartLabel.setSize(267, 200);
        clipartLabel.setLocation(1, 1);
        clipartLabel.setHorizontalAlignment(JLabel.CENTER);
        clipartLabel.setVerticalAlignment(JLabel.CENTER);
        clipartLabel.setForeground(Color.BLACK);
        clipartLabel.setVisible(true);
        layeredPane.add(clipartLabel, new Integer (2));
        
//        clipartLabel2 = clipartLabel;
//        clipartLabel2.setFont(new Font(font, Font.PLAIN, fontSize));
//        clipartLabel2.setSize(267, 200);
//        clipartLabel2.setLocation(1, -1);
//        clipartLabel2.setHorizontalAlignment(JLabel.CENTER);
//        clipartLabel2.setVerticalAlignment(JLabel.CENTER);
//        clipartLabel2.setForeground(Color.BLACK);
//        clipartLabel2.setVisible(true);
//        layeredPane.add(clipartLabel2, new Integer (3));
//                
//        clipartLabel3.setFont(new Font(font, Font.PLAIN, fontSize));
//        clipartLabel3.setSize(267, 200);
//        clipartLabel3.setLocation(-1, -1);
//        clipartLabel3.setHorizontalAlignment(JLabel.CENTER);
//        clipartLabel3.setVerticalAlignment(JLabel.CENTER);
//        clipartLabel3.setForeground(Color.BLACK);
//        clipartLabel3.setVisible(true);
//        layeredPane.add(clipartLabel3, new Integer (4));
//                
//        clipartLabel4.setFont(new Font(font, Font.PLAIN, fontSize));
//        clipartLabel4.setSize(267, 200);
//        clipartLabel4.setLocation(-1, 1);
//        clipartLabel4.setHorizontalAlignment(JLabel.CENTER);
//        clipartLabel4.setVerticalAlignment(JLabel.CENTER);
//        clipartLabel4.setForeground(Color.BLACK);
//        clipartLabel4.setVisible(true);
//        layeredPane.add(clipartLabel4, new Integer (5));
//                
//        clipartLabel5.setFont(new Font(font, Font.PLAIN, fontSize));
//        clipartLabel5.setSize(267, 200);
//        clipartLabel5.setLocation(2, 2);
//        clipartLabel5.setHorizontalAlignment(JLabel.CENTER);
//        clipartLabel5.setVerticalAlignment(JLabel.CENTER);
//        clipartLabel5.setForeground(Color.BLACK);
//        clipartLabel5.setVisible(true);
//        layeredPane.add(clipartLabel5, new Integer (6));
//                
//        clipartLabel6.setFont(new Font(font, Font.PLAIN, fontSize));
//        clipartLabel6.setSize(267, 200);
//        clipartLabel6.setLocation(0, 0);
//        clipartLabel6.setHorizontalAlignment(JLabel.CENTER);
//        clipartLabel6.setVerticalAlignment(JLabel.CENTER);
//        clipartLabel6.setForeground(Color.WHITE);
//        clipartLabel6.setVisible(true);
//        layeredPane.add(clipartLabel6, new Integer (7));
        
        counter++;
    }
    
    public static void displayMessage(String word) throws UnsupportedEncodingException, FileNotFoundException {
        String decoded = URLDecoder.decode(word, "UTF-8");
        
        textLabel = new JLabel(decoded);
        textLabel.setFont(new Font(font, Font.PLAIN, fontSize));
        textLabel.setSize(267, 56);
        textLabel.setLocation(1, 201);
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setVerticalAlignment(JLabel.CENTER);
        textLabel.setForeground(Color.BLACK);
        textLabel.setVisible(true);
        layeredPane.add(textLabel, new Integer (8));
        
        textLabel2 = new JLabel(decoded);
        textLabel2.setFont(new Font(font, Font.PLAIN, fontSize));
        textLabel2.setSize(267, 56);
        textLabel2.setLocation(1, 199);
        textLabel2.setHorizontalAlignment(JLabel.CENTER);
        textLabel2.setVerticalAlignment(JLabel.CENTER);
        textLabel2.setForeground(Color.BLACK);
        textLabel2.setVisible(true);
        layeredPane.add(textLabel2, new Integer (9));
        
        textLabel3 = new JLabel(decoded);
        textLabel3.setFont(new Font(font, Font.PLAIN, fontSize));
        textLabel3.setSize(267, 56);
        textLabel3.setLocation(-1, 199);
        textLabel3.setHorizontalAlignment(JLabel.CENTER);
        textLabel3.setVerticalAlignment(JLabel.CENTER);
        textLabel3.setForeground(Color.BLACK);
        textLabel3.setVisible(true);
        layeredPane.add(textLabel3, new Integer (10));
        
        textLabel4 = new JLabel(decoded);
        textLabel4.setFont(new Font(font, Font.PLAIN, fontSize));
        textLabel4.setSize(267, 56);
        textLabel4.setLocation(-1, 201);
        textLabel4.setHorizontalAlignment(JLabel.CENTER);
        textLabel4.setVerticalAlignment(JLabel.CENTER);
        textLabel4.setForeground(Color.BLACK);
        textLabel4.setVisible(true);
        layeredPane.add(textLabel4, new Integer (11));
        
        textLabel5 = new JLabel(decoded);
        textLabel5.setFont(new Font(font, Font.PLAIN, fontSize));
        textLabel5.setSize(267, 56);
        textLabel5.setLocation(2, 202);
        textLabel5.setHorizontalAlignment(JLabel.CENTER);
        textLabel5.setVerticalAlignment(JLabel.CENTER);
        textLabel5.setForeground(Color.BLACK);
        textLabel5.setVisible(true);
        layeredPane.add(textLabel5, new Integer (12));
        
        textLabel6 = new JLabel(decoded);
        textLabel6.setFont(new Font(font, Font.PLAIN, fontSize));
        textLabel6.setSize(267, 56);
        textLabel6.setLocation(0, 200);
        textLabel6.setHorizontalAlignment(JLabel.CENTER);
        textLabel6.setVerticalAlignment(JLabel.CENTER);
        textLabel6.setForeground(Color.WHITE);
        textLabel6.setVisible(true);
        layeredPane.add(textLabel6, new Integer (13));
        
        counter++;
                
        File logFile = new File("lastMessage.txt");
        try (PrintWriter log_file_writer = new PrintWriter(logFile)) {
            for(int i = 0; i < word.length()-2; i++){
                if (word.charAt(i) == '%' && word.charAt(i+1) == '2' && word.charAt(i+2) == '0') {
                    String wordTmp = word.substring(i+3);
                    word = word.substring(0, i);
                    word = word.concat(" " + wordTmp);
                }
            }
            log_file_writer.println(word);
        }
    }
    
    public static boolean giveaway(String sender) {
        if(!giveArray.contains(sender)) {
            giveArray.add(sender);
            return true;
        }
        return false;
    }
    
    public static String giveawayEnd() {
        Random r = new Random();
        String winner = "Nobody";
        if(!giveArray.isEmpty()) {
            int num = r.nextInt(giveArray.size());
            winner = (String) giveArray.get(num);
        }
        return winner;
    }
    
    private static void loadFromFolder() throws IOException {   
        int numFiles = listOfFiles.length;
        JProgressBar progressBar = new JProgressBar(0, numFiles);
        progressBar.setValue(0);
        progressBar.setSize(256, 16);
        progressBar.setLocation(5, 179);
        progressBar.setStringPainted(true);
        progressBar.setVisible(true);
        layeredPane.add(progressBar, new Integer (2));
        Random r = new Random();
        
        reset();
        displayMessage("Repainting textures...");
        
        for (int i = 0; i < numFiles; i++) {
            int rand = r.nextInt(listOfFilesStart.length);
            String dumpName = listOfFiles[i].getName();
            String imageFromFolderName = listOfFilesStart[rand].getName();
            File inputfile = new File(settings.folderFilepath() + imageFromFolderName);
            BufferedImage bi = ImageIO.read(inputfile);
            File outputfile = new File(settings.loadFilepath() + dumpName);
            ImageIO.write(bi, "png", outputfile);
            progressBar.setValue(i+1);
        }
        
        progressBar.setVisible(false);
        reset();
        displayMessage("Finished!");
    }

    private static String readAll(Reader rd) throws Exception {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws Exception {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        }
    }
    
    private static void reset() {
        //removes old image
        if (counter > 0) {
            layeredPane.remove(clipartLabel);
            layeredPane.remove(textLabel);
            layeredPane.remove(textLabel2);
            layeredPane.remove(textLabel3);
            layeredPane.remove(textLabel4);
            layeredPane.remove(textLabel5);
            layeredPane.remove(textLabel6);
        } 
    }
    
    public static void realTimeRepainter(BufferedImage bi) throws IOException {
        if (settings.rtrEnabled() == 1) {
            Random r = new Random();
            int numFiles = settings.numTexturesToReplace();
            
            if (settings.flipTextures() == 1) {
                AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                tx.translate(0, -bi.getHeight(null));
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                bi = op.filter(bi, null);
            }

            JProgressBar progressBar = new JProgressBar(0, numFiles);
            progressBar.setValue(0);
            progressBar.setSize(256, 16);
            progressBar.setLocation(5, 179);
            progressBar.setStringPainted(true);
            progressBar.setVisible(true);
            layeredPane.add(progressBar, new Integer (2));
            
            if(settings.recheckDumpFolder() == 1) {
                listOfFiles = loadFolder.listFiles();
            }

            for(int i = 0; i < numFiles; i++) {
                int rand = r.nextInt(listOfFiles.length);
                String name = listOfFiles[rand].getName();
                File outputfile = new File(settings.loadFilepath() + name);
                ImageIO.write(bi, "png", outputfile);
                progressBar.setValue(i+1);
            }
            progressBar.setVisible(false);
        }
    }
    
    public static void save(BufferedImage bi) throws IOException {
        if (settings.savedImagesEnabled() == 1) {
            File outputfile = new File(settings.savedImagesFilepath() + "saved" + imageNum + ".png");
            ImageIO.write(bi, "png", outputfile);
            imageNum++;
        }
    }
    
    private static void wrong() {
        clipartLabel = new JLabel("Something went wrong :(");
        displayText();
    }

    private static void wrongStock(String word) {
        clipartLabel = new JLabel("<html>Either no stock images found<br>or something went wrong. :(</html>");
        displayText();
    }
    
    public static void main(String[] args) throws Exception {
        Settings settings = new Settings();
        ClipArtBot cab = new ClipArtBot(settings);
        TwitchBot tb = new TwitchBot(settings); 
        Midis mid = new Midis(settings);
        tb.setVerbose(true);
        System.out.println("aaa");
        tb.connect(settings.server(), settings.port(), settings.oauthCode());
        System.out.println("bbb");
        System.out.println(settings.oauthCode());
        tb.joinChannel("#" + settings.twitchChannel());
                
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File("logo.png")); // eventually C:\\ImageTest\\pic2.jpg
        } catch (IOException e) {
        }
        ImageIcon logo = new ImageIcon(bi);
        reset();
        display(logo);
        displayMessage("ClipArtBot v0.9.4");
        
        if (settings.loadFromFolder() == 1) {
            loadFromFolder();
        }
        
        while (true) {
            try {
                if(!tb.isConnected()) {
                    tb.reconnect();
                    if(tb.isConnected()) {
                        tb.joinChannel("#" + settings.twitchChannel());
                    }
                }
            } catch(Exception e) {
            }
            Thread.sleep(5000);
        }  
    }
}