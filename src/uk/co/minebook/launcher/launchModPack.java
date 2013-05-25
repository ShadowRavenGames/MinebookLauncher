/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.minebook.launcher;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author MuddyFudger
 */
public class launchModPack {
    public static String pack, packDir, username, password, session;
        
    public launchModPack(String selectedPack) throws Exception {
        pack = selectedPack;
        packDir = System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + pack + "\\minecraft";
        username = MinebookLauncher.loggedInUser;
        session = MinebookLauncher.sessionID;
        Process minecraftProcess = MinecraftLauncher.launchMinecraft(packDir, username, session, "MinecraftForge.zip", "2048");
        
        ProcessMonitor.create(minecraftProcess, new Runnable() {
            @Override
            public void run() {
                JFrame launchFrame = MinebookLauncher.frame;
                launchFrame.setVisible(true);
            }
        });
    }
    
    public static void launchMinecraft(String workingDir, String username, String password, String forgename, String rmax) throws IOException {
            try {
                    Console.log("Launching " + pack.replace("_", " "));
                    System.out.println("Loading jars...");
                    String[] jarFiles = new String[] {"minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
                    ArrayList<File> classPathFiles = new ArrayList<File>();
                    File tempDir = new File(new File(workingDir).getParentFile(), "instMods/");
                    if(tempDir.isDirectory()) {
                            for(String name : tempDir.list()) {
                                    if(!name.equalsIgnoreCase(forgename)) {
                                            if(name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar")) {
                                                    classPathFiles.add(new File(tempDir, name));
                                            }
                                    }
                            }
                    }

                    classPathFiles.add(new File(tempDir, forgename));
                    for(String jarFile : jarFiles) {
                            classPathFiles.add(new File(new File(workingDir, "bin"), jarFile));
                    }	

                    URL[] urls = new URL[classPathFiles.size()];
                    for(int i = 0; i < classPathFiles.size(); i++) {
                            try {
                                    urls[i] = classPathFiles.get(i).toURI().toURL();
                            } catch (MalformedURLException e) {
                                    e.printStackTrace();
                            }
                            System.out.println("Added URL to classpath: " + urls[i].toString());
                    }

                    System.out.println("Loading natives...");
                    String nativesDir = new File(new File(workingDir, "bin"), "natives").toString();
                    System.out.println("Natives loaded...");

                    System.setProperty("org.lwjgl.librarypath", nativesDir);
                    System.setProperty("net.java.games.input.librarypath", nativesDir);

                    System.setProperty("user.home", new File(workingDir).getParent());

                    URLClassLoader cl = new URLClassLoader(urls, launchModPack.class.getClassLoader());

                    System.out.println("Loading minecraft class");
                    Class<?> mc = cl.loadClass("net.minecraft.client.Minecraft");
                    System.out.println("mc = " + mc);
                    Field[] fields = mc.getDeclaredFields();
                    System.out.println("field amount: " + fields.length);

                    for (Field f : fields) {
                            if (f.getType() != File.class) {
                                    continue;
                            }
                            if (0 == (f.getModifiers() & (Modifier.PRIVATE | Modifier.STATIC))) {
                                    continue;
                            }
                            f.setAccessible(true);
                            f.set(null, new File(workingDir));
                            System.out.println("Fixed Minecraft Path: Field was " + f.toString());
                            break;
                    }

                    String mcDir = mc.getMethod("a", String.class).invoke(null, (Object) "minecraft").toString();

                    String iconLoc = new File(workingDir).getParent() + "\\icon.png";
                    String animLoc = new File(workingDir).getParent() + "\\animation.gif";
                    
                    System.out.println("Pack Directory: " + workingDir);
                    System.out.println("MCDIR: " + mcDir);

                    System.out.println("Launching with applet wrapper...");

                    try {
                            Class<?> MCAppletClass = cl.loadClass("net.minecraft.client.MinecraftApplet");
                            Applet mcappl = (Applet) MCAppletClass.newInstance();
                            MinecraftFrame mcWindow = new MinecraftFrame(pack, iconLoc, animLoc);
                            mcWindow.start(mcappl, username, password);
                            LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/addStat.php?type=launch&pack=" + downloadPack.packID));
                    } catch (InstantiationException e) {
                            Console.log("Applet wrapper failed! Falling back to compatibility mode.");
                            mc.getMethod("main", String[].class).invoke(null, (Object) new String[] {username, password});
                    }
            } catch (Throwable t) {
                try {
                    System.err.println(t);
                    Console.log("Unhandled error launching minecraft");
                } catch (InterruptedException ex) {
                    Logger.getLogger(launchModPack.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
	}
    
        public static enum OS {
		WINDOWS,
		UNIX,
		MACOSX,
		OTHER,
	}
    
        public static OS getCurrentOS() {
		String osString = System.getProperty("os.name").toLowerCase();
		if (osString.contains("win")) {
			return OS.WINDOWS;
		} else if (osString.contains("nix") || osString.contains("nux")) {
			return OS.UNIX;
		} else if (osString.contains("mac")) {
			return OS.MACOSX;
		} else {
			return OS.OTHER;
		}
	}

    
        public static String getJavaDelimiter() {
		switch(getCurrentOS()) {
		case WINDOWS:
			return ";";
		case UNIX:
			return ":";
		case MACOSX:
			return ":";
		default:
			return ";";
		}
	}
}
