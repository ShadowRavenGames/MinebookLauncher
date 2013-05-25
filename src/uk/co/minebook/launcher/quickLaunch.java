/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.minebook.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MuddyFudger
 */
class quickLaunch {

    public quickLaunch(String string, String mcver) {
        try {
            if( !MinebookLauncher.loggedInUser.equals("Login") ) {
                String sessionContent = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/isPackPrivate.php?id=" + string));
                if( sessionContent.equals("yes") ) {
                    File code = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\accessCodes\\" + string);
                    if( code.exists() ) {
                        StringBuffer fileData = new StringBuffer(1000);
                        BufferedReader reader = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\.MinebookLauncher\\accessCodes\\" + string));
                        char[] buf = new char[1024];
                        int numRead=0;
                        while((numRead=reader.read(buf)) != -1){
                            String readData = String.valueOf(buf, 0, numRead);
                            fileData.append(readData);
                            buf = new char[1024];
                        }
                        reader.close();
                        String loginData = ProtectedLoginFile.main(fileData.toString(), "decrypt");
                        String packCodeCorrect = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/isPackCodeCorrect.php?id=" + string + "&code=" + loginData + "&user=" + MinebookLauncher.loggedInUser));
                        if( packCodeCorrect.equals("yes") ) {
                            File verExists = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + string + "\\version");
                            if( verExists.exists() ) {
                                BufferedReader br = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + string + "\\version"));
                                String v = br.readLine();

                                String vn = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + string));
                                if( v.equals(vn) ) {
                                    //launchPackHere
                                    new launchModPack(string);
                                }else{
                                    new updateAvailable(MinebookLauncher.frame, string, mcver, true);
                                }
                            }else{
                                new downloadPack(string, mcver, false);
                            }
                        }else if( packCodeCorrect.equals("not-allowed") ) {
                            Console.log("You are not on the " + string + " whitelist!");
                        }
                    }else{
                        new enterCode(MinebookLauncher.frame, string, mcver, true);
                    }
                }else{
                    File f = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + string);
                    if(f.exists()) {
                        BufferedReader br = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + string + "\\version"));
                        String v = br.readLine();

                        String vn = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + string));
                        if( v.equals(vn) ) {
                            //launchPackHere
                            new launchModPack(string);
                        }else{
                            new updateAvailable(MinebookLauncher.frame, string, mcver, true);
                        }
                    }else{
                        //downloadPackHere
                        new downloadPack(string, mcver, false);
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
