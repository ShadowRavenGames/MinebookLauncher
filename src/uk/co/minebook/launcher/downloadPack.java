/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.minebook.launcher;

import java.awt.Color;
import java.awt.Container;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Spring;
import javax.swing.SpringLayout;



/**
 *
 * @author MuddyFudger
 */
class downloadPack extends JDialog {
    
    public static double percentDled;
    
    public static String packID, mcVer;
    
    public static downloadPack frame;
    
    public static JLabel passwordLbl;
    public static JProgressBar progress;

    public downloadPack(String id, String mcv, boolean b) throws Exception {
        super(MinebookLauncher.frame, id, true);
        packID=id;
        mcVer = mcv;
        frame = this;
        setupGui(b);
    }
    
    public void doDispose() {
        frame.dispose();
    }
    
    private void setupGui(boolean b) throws Exception {
        setTitle("Downloading Pack!");
        setModal(true);
        setSize(319, 65);
        setResizable(false);
        setUndecorated(true);
        
        System.out.println(mcVer);
                     
        Container panel = getContentPane();
        
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        if( b == true ) {
            deleteFolder(new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + packID));
            passwordLbl = new JLabel("Updating " + packID.replace("_", " "));
        }else{
            passwordLbl = new JLabel("Downloading " + packID.replace("_", " "));
        }
        passwordLbl.setForeground(Color.WHITE);
        progress = new JProgressBar(0, 100);

        panel.add(passwordLbl);
        panel.add(progress);

        Spring hSpring;

        hSpring = Spring.constant(0);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, passwordLbl, hSpring, SpringLayout.HORIZONTAL_CENTER, panel);

        hSpring = Spring.sum(hSpring, Spring.width(passwordLbl));
        hSpring = Spring.sum(hSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.EAST, panel, hSpring, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, progress, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        
        hSpring = Spring.sum(hSpring, Spring.width(progress));
        hSpring = Spring.sum(hSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.EAST, panel, hSpring, SpringLayout.WEST, panel);

        Spring vSpring;
        Spring rowHeight;

        vSpring = Spring.constant(20);

        layout.putConstraint(SpringLayout.BASELINE, passwordLbl,       vSpring, SpringLayout.BASELINE, panel);

        rowHeight = Spring.height(passwordLbl);

        vSpring = Spring.sum(vSpring, rowHeight);
        vSpring = Spring.sum(vSpring, Spring.constant(5));

        layout.putConstraint(SpringLayout.NORTH, progress, vSpring, SpringLayout.NORTH, panel);

        vSpring = Spring.sum(vSpring, Spring.height(progress));
        vSpring = Spring.sum(vSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.SOUTH, panel, vSpring, SpringLayout.NORTH, panel);
        
        pack();
        
        getRootPane().setOpaque(false);
        getContentPane().setBackground (new Color (0, 0, 0));
        setBackground (new Color (0, 0, 0));
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(128, 128, 128)));

        new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + packID).mkdir();

        String version = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + packID));
        String url = "http://modpacks.minebook.co.uk/packs/" + packID + "/" + version + ".zip";
        new Thread(new downloadFiles(new URL(url), packID, mcVer)).start();

        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
    
    public void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}

class downloadFiles implements Runnable {
    
   String packID, mcv;
   URL url;
    
   public downloadFiles(URL u, String id, String mcVer) {
       packID = id;
       mcv = mcVer;
       url=u;
   }
    
    @Override
   public void run() {
    try {
        
      String version = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + packID));
      File inputFile = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + downloadPack.packID + "\\" + version + ".zip");

      URL copyurl = url;

      HttpURLConnection conn = null;
      conn = (HttpURLConnection)copyurl.openConnection();
      conn.setRequestMethod("HEAD");
      conn.getInputStream();
      int fileSize = conn.getContentLength();

      InputStream outputFile = copyurl.openStream();
      FileOutputStream out = new FileOutputStream(inputFile);

      byte[] data = new byte[512];
      long total = 0L;
      int c;
      while ((c = outputFile.read(data)) != -1) {
        total += c;
        downloadPack.progress.setValue((int)(total * 100L / fileSize));
        Console.log("Downloading " + packID.replace("_", " ") + ": " + (int)(total * 100L / fileSize) + "%");
        out.write(data, 0, c);
      }

      outputFile.close();
      out.close();

      String fName = System.getenv("APPDATA") + "/.MinebookLauncher/packs/" + packID + "/" + version + ".zip";
      unzip(fName, packID);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void unzip(String zipFile, String id) throws Exception {
      
        downloadPack.passwordLbl.setText("Extracting Files");
        Console.log("Extracting " + id.replace("_", " ") );
        downloadPack.progress.setValue(0);
      
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 4);

        Enumeration zipFileEntries = zip.entries();

        while (zipFileEntries.hasMoreElements())
        {
          ZipEntry entry = (ZipEntry)zipFileEntries.nextElement();
          String currentEntry = entry.getName();
          File destFile = new File(file.getParentFile(), currentEntry);

          File destinationParent = destFile.getParentFile();
          int fileSize = zip.size();

          destinationParent.mkdirs();

          if (!entry.isDirectory())
          {
            BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry)); Throwable localThrowable3 = null;
            try
            {
              byte[] data = new byte[BUFFER];

              FileOutputStream fos = new FileOutputStream(destFile);
              BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER); Throwable localThrowable4 = null;
              try
              {
                int currentByte;
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                  downloadPack.progress.setValue( (int)(currentByte * 100L / fileSize) );
                  dest.write(data, 0, currentByte);
                }
                dest.flush();
              }
              catch (Throwable localThrowable1)
              {
                localThrowable4 = localThrowable1; throw localThrowable1;
              }
              finally
              {
                  is.close();
                  dest.close();
                  fos.close();
              }
            }
            catch (Throwable localThrowable2)
            {
              localThrowable3 = localThrowable2; throw localThrowable2;
            }
          }
        }
        zip.close();
        file.delete();

        downloadPack.passwordLbl.setText("Finnished!");

        downloadMC();
    }
  
    public void downloadMC() throws IOException {
      try {
          
        String url = "http://assets.minecraft.net/" + mcv.replace(".", "_") + "/minecraft.jar";

        File inputFile = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + downloadPack.packID + "\\minecraft\\bin\\minecraft.jar");

        downloadPack.passwordLbl.setText("Downloading Minecraft " + mcv);

        URL copyurl = new URL(url);

        HttpURLConnection conn = null;
        conn = (HttpURLConnection)copyurl.openConnection();
        conn.setRequestMethod("HEAD");
        conn.getInputStream();
        int fileSize = conn.getContentLength();

        InputStream outputFile = copyurl.openStream();
        FileOutputStream out = new FileOutputStream(inputFile);

        byte[] data = new byte[512];
        long total = 0L;
        int c;
        while ((c = outputFile.read(data)) != -1) {
          total += c;
          downloadPack.progress.setValue((int)(total * 100L / fileSize));
          Console.log("Downloading Minecraft " + mcv + ": " + (int)(total * 100L / fileSize) + "%");
          out.write(data, 0, c);
        }

        outputFile.close();
        out.close();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
        
        File minecraft = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + downloadPack.packID + "\\minecraft\\bin\\minecraft.jar");
        if (minecraft.exists()) {
            String version = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + downloadPack.packID));
            File versionFile = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + downloadPack.packID + "\\version");
            FileWriter fw = new FileWriter(versionFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(version);
            bw.close();
            fw.close();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(downloadFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/addStat.php?type=download&pack=" + downloadPack.packID));
            
            killMetaInf();
        }
    }
    
    public static void killMetaInf() {
        File inputFile = new File(System.getenv("APPDATA") + "/.MinebookLauncher/packs/" + downloadPack.packID + "/minecraft/bin", "minecraft.jar");
        File outputTmpFile = new File(System.getenv("APPDATA") + "/.MinebookLauncher/packs/" + downloadPack.packID + "/minecraft/bin", "minecraft.jar.tmp");
        try {
            downloadPack.passwordLbl.setText("Removing META-INF");
            Console.log("Removing META-INF");
            JarInputStream input = new JarInputStream(new FileInputStream(inputFile));
            JarOutputStream output = new JarOutputStream(new FileOutputStream(outputTmpFile));
            JarEntry entry;

            while ((entry = input.getNextJarEntry()) != null) {
                if (entry.getName().contains("META-INF")) {
                        continue;
                }
                output.putNextEntry(entry);
                byte buffer[] = new byte[1024];
                int amo;
                while ((amo = input.read(buffer, 0, 1024)) != -1) {
                        output.write(buffer, 0, amo);
                }
                output.closeEntry();
            }

            input.close();
            output.close();

            if(!inputFile.delete()) {
                System.err.println("Failed to delete Minecraft.jar.");
                return;
            }
            outputTmpFile.renameTo(inputFile);

            downloadPack.frame.dispose();

            new launchModPack(downloadPack.packID);
        } catch (Exception e) { 
            System.err.println(e.getMessage());
        }
    }
}