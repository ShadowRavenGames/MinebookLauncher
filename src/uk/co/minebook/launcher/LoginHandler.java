/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.minebook.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author MuddyFudger
 */
public class LoginHandler extends Thread {
    
    public static String getContentResult(URL url)  throws IOException {

        InputStream in = url.openStream();
        StringBuffer sb = new StringBuffer();

        byte [] buffer = new byte[256];

        while(true){
            int byteRead = in.read(buffer);
            if(byteRead == -1)
                break;
            for(int i = 0; i < byteRead; i++){
                sb.append((char)buffer[i]);
            }
        }
        return sb.toString();
    }
    
    LoginHandler(String username, char[] passwordChars, boolean remember, boolean autoLogin) throws MalformedURLException, IOException, Exception {
        
        if( !remember ) {
            File file = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\login");
            if( file.exists() ) {
                file.delete();
            }
        }
        
        String password = new String(passwordChars);

        String sessionContent = getContentResult(new URL("https://login.minecraft.net/?user=" + username + "&password=" + password + "&version=13"));
        if( sessionContent.equals("Bad login") ) {
            MinebookLauncher.loginMessage.setText("Bad Login!");
        }else if( sessionContent.equals("Mojang account, use e-mail as username.") ) {
            MinebookLauncher.loginMessage.setText("Account Migrated, Use email address!");
        }else if( !sessionContent.equals("Bad login") || !sessionContent.equals("Mojang account, use e-mail as username.") ) {
            String[] parts = sessionContent.split(":");

            String hasPaid = getContentResult(new URL("https://minecraft.net/haspaid.jsp?user=" + parts[2]));
                      
            if( hasPaid.equals("true") ) {

                MinebookLauncher.sessionID = parts[3].toString();
                MinebookLauncher.loggedInUser = parts[2].toString();

                // REGISTER USER
                String register = getContentResult(new URL("http://modpacks.minebook.co.uk/registerUser.php?u=" + parts[2].toString() + "&p=" + password));
                Console.log(register);

                MinebookLauncher.frame.setTitle("Minebook Launcher | " + parts[2].toString());

                ImageIcon userIMG = new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/user.php?user=" + parts[2].toString()));
                int imgWidth = userIMG.getIconWidth();
                MinebookLauncher.userImage.setIcon(new ImageIcon(new URL("http://minebook.co.uk/images/player.php?u=" + parts[2].toString() + "&s=128")));
                MinebookLauncher.user.setIcon(userIMG);
                MinebookLauncher.user.setBounds(774-imgWidth, 1, imgWidth, 29);
                MinebookLauncher.loginBox.setVisible(false);
                MinebookLauncher.arrow.setVisible(false);
                MinebookLauncher.arrow.setBounds(774-imgWidth+(imgWidth/2)-5, 26, 11, 6);

                if( remember ) {
                    String data = null;
                    if( autoLogin ) {
                        data = ProtectedLoginFile.main(username + ":" + password, "encrypt");
                    }else{
                        data = ProtectedLoginFile.main(username, "encrypt");
                    }
                    FileWriter fstream = new FileWriter(System.getenv("APPDATA") + "\\.MinebookLauncher\\login");
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(data);
                    out.close();

                    MinebookLauncher.loginUser.setText("");
                    MinebookLauncher.loginPassword.setText("");
                    MinebookLauncher.loginRemember.setSelected(false);
                    MinebookLauncher.loginAuto.setSelected(false);
                }
            }else{
                MinebookLauncher.loginMessage.setText("User Not Premium!");
            }
        }
        
    }
}
