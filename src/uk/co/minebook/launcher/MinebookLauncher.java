package uk.co.minebook.launcher;

import chrriis.dj.nativeswing.NativeSwing;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MinebookLauncher extends JFrame {
  
    public static int height = 490;
    public static int width = 852;
    
    public static JFrame frame = new JFrame();
    public static JLabel logo = new JLabel();
    public static JLabel minimize = new JLabel();
    public static JLabel arrow = new JLabel();
    public static JPanel loginBox = new JPanel();
    public static JPanel profileBox = new JPanel();
    public static JLabel maximize = new JLabel();
    public static JLabel close = new JLabel();
    public static JLabel user = new JLabel();
    public static JLabel footer = new JLabel();
    public static JLabel header = new JLabel();
    public static JLabel loginMessage = new JLabel();
    public static JLabel userImage;
    public static JTextField loginUser = new JTextField();
    public static JPasswordField loginPassword = new JPasswordField();
    public static JToggleButton loginRemember = new JToggleButton("Remember Me");
    public static JToggleButton loginAuto = new JToggleButton("Auto Login");
    public static JWebBrowser webBrowser = new JWebBrowser();
    
    public static downloadPack dlp;
    
    public static Image programImage;
    
    public static Point startDrag = null;
    public static Point curLocation = null;
        
    public static String loggedInUser = "Login";
    public static String sessionID = null;
    
    public static void centerWindow() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
       
    public static void main(String[] args) throws Exception {
        NativeSwing.initialize();
        NativeInterface.open();

        new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\").mkdir();
        new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\").mkdir();
        new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\accessCodes\\").mkdir();
        
        URL serverConnection = new URL("http://modpacks.minebook.co.uk/connected.php");
        HttpURLConnection connection = (HttpURLConnection) serverConnection.openConnection();
        InputStream stream = connection.getInputStream();
        Scanner scanner = new Scanner(stream); // You can read the stream however you want. Scanner was just an easy example
        boolean found = false;
        while(scanner.hasNext()) {
            String next = scanner.next();
            if("true".equals(next)) {
                found = true;
                break;
            }
        }

        if(!found) {
            JOptionPane.showMessageDialog(null, "Sorry the Minebook server is offline at the moment.\nPlease try again later.");
            return;
        }
        
        programImage = ImageIO.read(new URL("http://minebook.co.uk/images/icon.png"));
        frame.setIconImage(programImage);
        frame.setTitle("Minebook Launcher");
        
        File f = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\login");
        if( f.exists() ) {
            StringBuffer fileData = new StringBuffer(1000);
            BufferedReader reader = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\.MinebookLauncher\\login"));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            String loginData = ProtectedLoginFile.main(fileData.toString(), "decrypt");
            if( loginData.contains(":") ) {
                String[] user = loginData.split(":");
                String sessionContent = LoginHandler.getContentResult(new URL("https://login.minecraft.net/?user=" + user[0] + "&password=" + user[1] + "&version=13"));
                String[] parts = sessionContent.split(":");
                
                programImage = ImageIO.read(new URL("http://minebook.co.uk/images/player.php?u=" + parts[2].toString() + "&t=application"));
                frame.setIconImage(programImage);
                frame.setTitle("Minebook Launcher | " + parts[2].toString());

                //socketMessage sMsg = new socketMessage();
                //sMsg.send(parts[2].toString() + "|notification|You have logged in elsewhere\n&bullet; Minebook Launcher");
                        
                sessionID = parts[3].toString();
                loggedInUser = parts[2].toString();
                loginRemember.setSelected(true);
                loginAuto.setSelected(true);
            }else{
                loginUser.setText(loginData);
                loginRemember.setSelected(true);
            }
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                webBrowser.setBounds(311, 30, 531, 430);
//                webBrowser.setDefaultPopupMenuRegistered(false);
                webBrowser.setBarsVisible(false);
                webBrowser.setJavascriptEnabled(true);
                browseTo("http://modpacks.minebook.co.uk/news.php");
            }

        });

        SpringLayout layout = new SpringLayout();
        
        frame.setBackground(Color.BLACK);

        JLabel background = new JLabel(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/frame.png")));
        
        frame.setContentPane(background);

        frame.setBounds(new Rectangle(width, height));
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        centerWindow();

        logo.addMouseListener(new logoAction());
        logo.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/logo.png")));
        logo.setBounds(0, 1, 133, 29);
        logo.setToolTipText("Goto minebook.co.uk!");
        logo.setVisible(true);

        ImageIcon userIMG = new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/user.php?user=" + loggedInUser + ""));
        int imgWidth = userIMG.getIconWidth();
        user.setIcon(userIMG);
        user.addMouseListener(new userAction());
        user.setBounds(774-imgWidth, 1, imgWidth, 29);
        user.setVisible(true);
                
        profileBox.setBackground(Color.WHITE);
        profileBox.setBounds((852-300-11), 31, 300, 150);
        profileBox.setBorder(BorderFactory.createLineBorder(new Color(89, 61, 41)));
        profileBox.setVisible(false);
        profileBox.setLayout(layout);
        
        int imgDim = 128;
        
        userImage = new JLabel(new ImageIcon(new URL("http://minebook.co.uk/images/player.php?u=" + loggedInUser + "&s=" + imgDim)));
        userImage.setPreferredSize(new Dimension(imgDim, imgDim));
        userImage.setBorder(BorderFactory.createLineBorder(new Color(89, 61, 41)));
        
        JButton profileButton = new JButton("View your Profile");
        profileButton.addMouseListener(new userProfileAction());
        profileButton.setPreferredSize(new Dimension(140, 30));

        JButton editButton = new JButton("Edit your Profile");
        editButton.addMouseListener(new userEditAction());
        editButton.setPreferredSize(new Dimension(140, 30));

        JButton friendButton = new JButton("Add A Friend");
        friendButton.setPreferredSize(new Dimension(140, 30));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(140, 29));
        logoutButton.addMouseListener(new logoutAction());
                
        profileBox.add(userImage);
        profileBox.add(profileButton);
        profileBox.add(editButton);
        profileBox.add(friendButton);
        profileBox.add(logoutButton);
        
        layout.putConstraint(SpringLayout.WEST, userImage, 10, SpringLayout.WEST, profileBox);
        layout.putConstraint(SpringLayout.EAST, userImage, imgDim+10, SpringLayout.WEST, profileBox);
        layout.putConstraint(SpringLayout.NORTH, userImage, 10, SpringLayout.NORTH, profileBox);
        layout.putConstraint(SpringLayout.SOUTH, userImage, imgDim+10, SpringLayout.NORTH, profileBox);

        layout.putConstraint(SpringLayout.WEST, profileButton, imgDim + 20, SpringLayout.WEST, profileBox);
        layout.putConstraint(SpringLayout.NORTH, profileButton, 10, SpringLayout.NORTH, profileBox);

        layout.putConstraint(SpringLayout.WEST, editButton, imgDim + 20, SpringLayout.WEST, profileBox);
        layout.putConstraint(SpringLayout.NORTH, editButton, 43, SpringLayout.NORTH, profileBox);

        layout.putConstraint(SpringLayout.WEST, friendButton, imgDim + 20, SpringLayout.WEST, profileBox);
        layout.putConstraint(SpringLayout.NORTH, friendButton, 76, SpringLayout.NORTH, profileBox);

        layout.putConstraint(SpringLayout.WEST, logoutButton, imgDim + 20, SpringLayout.WEST, profileBox);
        layout.putConstraint(SpringLayout.NORTH, logoutButton, 109, SpringLayout.NORTH, profileBox);
        
        loginBox.setBackground(Color.WHITE);
        loginBox.setBounds((852-300-11), 31, 300, 150);
        loginBox.setBorder(BorderFactory.createLineBorder(new Color(89, 61, 41)));
        loginBox.setVisible(false);
        loginBox.setLayout(layout);
        
        JLabel loginUserText = new JLabel("Username: ");
        JLabel loginPasswordText = new JLabel("Password: ");
        loginMessage.setForeground(Color.RED);
        loginMessage.setHorizontalAlignment(SwingConstants.RIGHT);
        JButton loginButton = new JButton("Login");
        loginButton.addMouseListener(new loginAction());

        loginBox.add(loginUserText);
        loginBox.add(loginUser);
        loginBox.add(loginPasswordText);
        loginBox.add(loginPassword);
        loginBox.add(loginRemember);
        loginBox.add(loginAuto);
        loginBox.add(loginMessage);
        loginBox.add(loginButton);
        
        layout.putConstraint(SpringLayout.WEST, loginUserText, 40, SpringLayout.WEST, loginBox);
        layout.putConstraint(SpringLayout.NORTH, loginUserText, 10, SpringLayout.NORTH, loginBox);
        layout.putConstraint(SpringLayout.WEST, loginUser, 70, SpringLayout.WEST, loginUserText);
        layout.putConstraint(SpringLayout.EAST, loginUser, 160, SpringLayout.EAST, loginUserText);
        layout.putConstraint(SpringLayout.NORTH, loginUser, 0, SpringLayout.NORTH, loginUserText);
        layout.putConstraint(SpringLayout.WEST, loginPasswordText, 40, SpringLayout.WEST, loginBox);
        layout.putConstraint(SpringLayout.NORTH, loginPasswordText, 35, SpringLayout.NORTH, loginBox);
        layout.putConstraint(SpringLayout.WEST, loginPassword, 70, SpringLayout.WEST, loginPasswordText);
        layout.putConstraint(SpringLayout.EAST, loginPassword, 161, SpringLayout.EAST, loginPasswordText);
        layout.putConstraint(SpringLayout.NORTH, loginPassword, 0, SpringLayout.NORTH, loginPasswordText);
        layout.putConstraint(SpringLayout.WEST, loginRemember, 40, SpringLayout.WEST, loginBox);
        layout.putConstraint(SpringLayout.NORTH, loginRemember, 60, SpringLayout.NORTH, loginBox);
        layout.putConstraint(SpringLayout.WEST, loginAuto, 170, SpringLayout.WEST, loginBox);
        layout.putConstraint(SpringLayout.NORTH, loginAuto, 60, SpringLayout.NORTH, loginBox);
        layout.putConstraint(SpringLayout.WEST, loginMessage, 0, SpringLayout.WEST, loginUserText);
        layout.putConstraint(SpringLayout.EAST, loginMessage, 159, SpringLayout.EAST, loginUserText);
        layout.putConstraint(SpringLayout.NORTH, loginMessage, 90, SpringLayout.NORTH, loginBox);
        layout.putConstraint(SpringLayout.WEST, loginButton, 199, SpringLayout.WEST, loginBox);
        layout.putConstraint(SpringLayout.NORTH, loginButton, 113, SpringLayout.NORTH, loginBox);
        
        arrow.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/arrow.png")));
        arrow.setBounds(774-imgWidth+(imgWidth/2)-5, 26, 11, 6);
        arrow.setVisible(false);
        
        minimize.addMouseListener(new minimizeAction());
        minimize.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/minimize.png")));
        minimize.setBounds(width-80, 5, 20, 20);
        minimize.setVisible(true);

        maximize.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/maximize.png")));
        maximize.setBounds(width-55, 5, 20, 20);
        maximize.setVisible(true);

        close.addMouseListener(new closeAction());
        close.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/close.png")));
        close.setBounds(width-30, 5, 20, 20);
        close.setVisible(true);
        
        header.addMouseMotionListener(new headerAction());
        header.addMouseListener(new headerMAction());
        header.setBounds(0, 0, width, 30);
        header.setVisible(true);

        footer.setBounds(11, 462, 830, 20);
        footer.setForeground(Color.WHITE);
        footer.setVisible(true);
        
        frame.add(arrow);
        frame.add(profileBox);
        frame.add(loginBox);
        frame.add(logo);
        frame.add(user);
        frame.add(minimize);
        frame.add(maximize);
        frame.add(close);
        frame.add(footer);
        frame.add(header);
        frame.add(webBrowser);
        
        
        JButton[] labels = getModPacks();

        int show = ( labels.length < 8 ) ? 8: labels.length;
        
        JPanel panel = new JPanel(new GridLayout( show, 1));
        panel.setBounds(0, 0, 295, (labels.length*20));
        panel.setPreferredSize(new Dimension(295, (labels.length*40)));

        panel.setBorder(null);
        panel.setBackground(new Color(50, 50, 50));
        panel.setVisible(true);
        JScrollPane scrollFrame = new JScrollPane(panel);
        panel.setAutoscrolls(true);
        scrollFrame.setBounds(10, 30, 301, height-60);
        scrollFrame.setBorder(null);
        scrollFrame.setVisible(true);
        
        URL fontUrl = new URL("http://fonts.mrvdog.in/f/DroidSans.ttf");
        Font rosemary = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream()).deriveFont(Font.PLAIN, 16);
        
        frame.add(scrollFrame);
        
        for (int i=0;i<labels.length;i++) {
            labels[i].setPreferredSize(new Dimension(290, 50));
            labels[i].setSize(290, 50);
            labels[i].setFont(rosemary);
            labels[i].setVisible(true);
            labels[i].addMouseListener(new ModPackListener(labels[i].getToolTipText()));
            labels[i].setHorizontalAlignment(SwingConstants.LEFT);
            labels[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            labels[i].setBorderPainted(false);
            labels[i].setFocusPainted(false);
            labels[i].setBackground(new Color(50, 50, 50));
            labels[i].setForeground(new Color(255, 255, 255));
            panel.add(labels[i]);
        }
      
        frame.setVisible(true);

    }
    
    public static void browseTo(String str) {
        webBrowser.navigate(str);
    }

    public static JButton[] getModPacks() throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        URL url = new URL("http://modpacks.minebook.co.uk/modpacks.php");
        InputStream stream = url.openStream();
        Document doc = docBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        NodeList listOfPacks = doc.getElementsByTagName("pack");
        stream.close();
               
        JButton[] pack = new JButton[ listOfPacks.getLength() ];
        
        for(int s=0; s<listOfPacks.getLength(); s++) {
            Node firstPackNode = listOfPacks.item(s);
            if(firstPackNode.getNodeType() == Node.ELEMENT_NODE){
                Element packNameElement = (Element)firstPackNode;
                NodeList packNameList = packNameElement.getElementsByTagName("name");
                Element packNameElement2 = (Element)packNameList.item(0);
                NodeList packNameFNList = packNameElement2.getChildNodes();

                Element packVersionElement = (Element)firstPackNode;
                NodeList packVersionList = packVersionElement.getElementsByTagName("version");
                Element packVersionElement2 = (Element)packVersionList.item(0);
                NodeList packVersionFNList = packVersionElement2.getChildNodes();

                Element minecraftVersionElement = (Element)firstPackNode;
                NodeList minecraftVersionList = minecraftVersionElement.getElementsByTagName("minecraftVersion");
                Element minecraftVersionElement2 = (Element)minecraftVersionList.item(0);
                NodeList minecraftVersionFNList = minecraftVersionElement2.getChildNodes();

                Element modDescElement = (Element)firstPackNode;
                NodeList modDescList = modDescElement.getElementsByTagName("by");
                Element modDescElement2 = (Element)modDescList.item(0);
                NodeList modDescFNList = modDescElement2.getChildNodes();
                
                pack[s] = new JButton( ((Node)packNameFNList.item(0)).getNodeValue().trim() + " v" + ((Node)packVersionFNList.item(0)).getNodeValue().trim() + " (MC" + ((Node)minecraftVersionFNList.item(0)).getNodeValue().trim() + ")" );
                pack[s].setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/packs/" + ((Node)packNameFNList.item(0)).getNodeValue().trim() + "/icon.png")));
                pack[s].setToolTipText( ((Node)packNameFNList.item(0)).getNodeValue().trim() );
            }
        }
        return pack;
    }
    
    private static class logoAction implements MouseListener {

        public logoAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI( "http://minebook.co.uk" );
                desktop.browse( uri );
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    
    private static class userProfileAction implements MouseListener {

        public userProfileAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI( "http://minebook.co.uk/" + loggedInUser );
                desktop.browse( uri );
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private static class userEditAction implements MouseListener {

        public userEditAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI( "http://minebook.co.uk/editaccount" );
                desktop.browse( uri );
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
    
    private static class headerAction implements MouseMotionListener {

        public headerAction() {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point curDrag = e.getPoint();
            if (startDrag == null) {
                startDrag = curDrag;
            }
            curLocation = curDrag;
            Point location = frame.getLocation();
            int newX = location.x + (curLocation.x - startDrag.x);
            int newY = location.y + (curLocation.y - startDrag.y);
            frame.setLocation(newX, newY);
        }

        @Override
        public void mouseMoved(MouseEvent e) {}
    }

    private static class headerMAction implements MouseListener {

        public headerMAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Point location = frame.getLocation();
            
            int newX = location.x + (curLocation.x - startDrag.x);
            int newY = location.y + (curLocation.y - startDrag.y);
            
            frame.setLocation(newX, newY);
            startDrag = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private static class closeAction implements MouseListener {

        public closeAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            frame.dispose();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            try {
                close.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/closeOver.png")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            try {
                close.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/close.png")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
        private static class minimizeAction implements MouseListener {

        public minimizeAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            frame.setState(JFrame.ICONIFIED);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            try {
                minimize.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/minimizeOver.png")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            try {
                minimize.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/minimize.png")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static class userAction implements MouseListener {

        public userAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if( loggedInUser == "Login" ) {
                if( loginBox.isVisible() ) {
                    loginBox.setVisible(false);
                    arrow.setVisible(false);
                }else{
                    loginBox.setVisible(true);
                    arrow.setVisible(true);
                }
            }else{
                if( profileBox.isVisible() ) {
                    profileBox.setVisible(false);
                    arrow.setVisible(false);
                }else{
                    profileBox.setVisible(true);
                    arrow.setVisible(true);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            try {
                user.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/user.php?user=" + loggedInUser + "&action=over")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            try {
                user.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/user.php?user=" + loggedInUser + "")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static class loginAction implements MouseListener {

        public loginAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
               Thread loginService = new LoginHandler(loginUser.getText(), loginPassword.getPassword(), loginRemember.isSelected(), loginAuto.isSelected());
                loginService.start();
            } catch (Exception ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private static class logoutAction implements MouseListener {

        public logoutAction() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                try {
                    programImage = ImageIO.read(new URL("http://minebook.co.uk/images/icon.png"));
                    frame.setIconImage(programImage);
                    frame.setTitle("Minebook Launcher");
                } catch (IOException ex) {
                    Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
                }
                loggedInUser = "Login";
                sessionID = null;
                ImageIcon userIMG = new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/user.php?user=" + loggedInUser + ""));
                user.setIcon(userIMG);
                int imgWidth = userIMG.getIconWidth();
                user.setBounds(774-imgWidth, 1, imgWidth, 29);
                profileBox.setVisible(false);
                arrow.setVisible(false);
                loginRemember.setSelected(false);
                loginAuto.setSelected(false);
                File file = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\login");
                if( file.exists() ) {
                    file.delete();
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private static class ModPackListener implements MouseListener {

    String buttonText;
    private MinebookLauncher MinebookLauncher;
    
    public ModPackListener(String text) {
        buttonText = text;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            try {
                if( !loggedInUser.equals("Login") ) {
                    String sessionContent = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/isPackPrivate.php?id=" + buttonText));
                    if( sessionContent.equals("yes") ) {
                        File code = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\accessCodes\\" + buttonText);
                        if( code.exists() ) {
                            StringBuffer fileData = new StringBuffer(1000);
                            BufferedReader reader = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\.MinebookLauncher\\accessCodes\\" + buttonText));
                            char[] buf = new char[1024];
                            int numRead=0;
                            while((numRead=reader.read(buf)) != -1){
                                String readData = String.valueOf(buf, 0, numRead);
                                fileData.append(readData);
                                buf = new char[1024];
                            }
                            reader.close();
                            String loginData = ProtectedLoginFile.main(fileData.toString(), "decrypt");
                            String packCodeCorrect = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/isPackCodeCorrect.php?id=" + buttonText + "&code=" + loginData));
                            if( packCodeCorrect.equals("yes") ) {
                                BufferedReader br = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + buttonText + "\\version"));
                                String v = br.readLine();

                                String vn = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + buttonText));
                                if( v.equals(vn) ) {
                                    //launchPackHere
                                    new launchModPack(buttonText);
                                }else{
                                    new updateAvailable(MinebookLauncher.frame, buttonText, true);
                                }
                            }
                        }else{
                            new enterCode(MinebookLauncher.frame, buttonText, true);
                        }
                    }else{
                        File f = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + buttonText);
                        if(f.exists()) {
                            BufferedReader br = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + buttonText + "\\version"));
                            String v = br.readLine();

                            String vn = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + buttonText));
                            if( v.equals(vn) ) {
                                //launchPackHere
                                new launchModPack(buttonText);
                            }else{
                                new updateAvailable(MinebookLauncher.frame, buttonText, true);
                            }
                        }else{
                            //downloadPackHere
                            new downloadPack(buttonText, false);
                        }
                    }
                }else{
                    Console.log("You must be logged in to use any of the Modpacks, Click LOGIN at the top right of the window.");
                }
            } catch (Exception ex) {
                Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
    
}
