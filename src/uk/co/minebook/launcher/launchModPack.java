/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.minebook.launcher;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author MuddyFudger
 */
public class launchModPack {
    public launchModPack(String selectedPack) throws IOException {
        Process p = Runtime.getRuntime().exec("javaw -classpath \"bin\\minecraft.jar;bin\\jinput.jar;bin\\lwjgl.jar;bin\\lwjgl_util.jar\" -Djava.library.path=\"bin\\natives\" net.minecraft.client.Minecraft " + MinebookLauncher.loggedInUser + " " + MinebookLauncher.sessionID + "", null, new File(System.getenv("APPDATA") + "/.MinebookLauncher/packs/" + selectedPack + "/minecraft" ));
    }
}
