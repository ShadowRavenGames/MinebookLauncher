package uk.co.minebook.launcher;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;

/**
 *
 * @author MuddyFudger
 */
class Console extends Thread {
    
    public static void log(String msg) throws InterruptedException {
        MinebookLauncher.frame.setVisible(true);
        MinebookLauncher.footer.setForeground(Color.WHITE);
        MinebookLauncher.footer.setText(msg);
    }

    public static void error(String msg) throws InterruptedException {
        MinebookLauncher.frame.setVisible(true);
        MinebookLauncher.footer.setForeground(Color.PINK);
        MinebookLauncher.footer.setText(msg);
    }

    
}
