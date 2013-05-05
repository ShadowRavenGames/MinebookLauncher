/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.minebook.launcher;

import java.awt.Color;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 *
 * @author MuddyFudger
 */

@SuppressWarnings("serial")
public class enterCode extends JDialog {
    
    String packID;
    
    private JLabel passwordLbl;
    private JPasswordField password;
    private JButton login;
    public static JLabel close = new JLabel();
    
    public enterCode(JFrame instance, String id, boolean modal) throws Exception {
        super(instance, id, modal);
        packID=id;
        setupGui();
    }
    
    private void setupGui() {
        setTitle("Enter Password!");
        setModal(true);
        setSize(324, 91);
        setResizable(false);
        setUndecorated(true);
               
        close.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
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
        });
        try {
            close.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/close.png")));
        } catch (MalformedURLException ex) {
            Logger.getLogger(enterCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        close.setBounds(new Rectangle(20, 20));
        
        Container panel = getContentPane();
        
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        passwordLbl = new JLabel("Enter Access Code: ");
        passwordLbl.setForeground(Color.WHITE);
        password = new JPasswordField(16);
        login = new JButton("Submit");
        login.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String sessionContent = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/isPackCodeCorrect.php?id=" + packID.toLowerCase() + "&code=" + new String(password.getPassword())));
                    if( sessionContent.equals("yes") ) {
                        
                        String data = ProtectedLoginFile.main(new String(password.getPassword()), "encrypt");
                        FileWriter fstream = new FileWriter(System.getenv("APPDATA") + "\\.MinebookLauncher\\accessCodes\\" + packID);
                        BufferedWriter out = new BufferedWriter(fstream);
                        out.write(data);
                        out.close();
                        
                        dispose();
                        Console.log("Code for pack " + packID + " is Correct!");
                        
                        File f = new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + packID + "\\version");
                        if(f.exists()) {
                            Scanner sc = new Scanner(new File(System.getenv("APPDATA") + "\\.MinebookLauncher\\packs\\" + packID + "\\version"));
                            String v = sc.toString();
                            String vn = LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/getCurrentPackVersion.php?id=" + packID));
                            if( v.equals(vn) ) {
                                //launchPackHere
                                new launchModPack(packID);
                            }else{
                                new updateAvailable(MinebookLauncher.frame, packID, true);
                            }
                        }else{
                            //downloadPackHere
                            new downloadPack(packID, false);
                        }
                    }else{
                        Console.log("Code for pack " + packID + " is Incorrect!");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MinebookLauncher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        panel.add(close);
        panel.add(passwordLbl);
        panel.add(password);
        panel.add(login);

        Spring hSpring;

        hSpring = Spring.constant(0);

        layout.putConstraint(SpringLayout.EAST, close, hSpring, SpringLayout.EAST, panel);

        hSpring = Spring.constant(10);

        layout.putConstraint(SpringLayout.WEST, passwordLbl, hSpring, SpringLayout.WEST, panel);

        hSpring = Spring.sum(hSpring, Spring.width(passwordLbl));
        hSpring = Spring.sum(hSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.WEST, password, hSpring, SpringLayout.WEST, panel);

        hSpring = Spring.sum(hSpring, Spring.width(password));
        hSpring = Spring.sum(hSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.EAST, panel, hSpring, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, login, 0, SpringLayout.HORIZONTAL_CENTER, panel);

        Spring vSpring;
        Spring rowHeight;

        vSpring = Spring.constant(-5);

        layout.putConstraint(SpringLayout.BASELINE, close, vSpring, SpringLayout.BASELINE, panel);

        vSpring = Spring.constant(25);

        layout.putConstraint(SpringLayout.BASELINE, passwordLbl,       0, SpringLayout.BASELINE, password);
        layout.putConstraint(SpringLayout.NORTH,    password,    vSpring, SpringLayout.NORTH,    panel);

        rowHeight = Spring.height(passwordLbl);
        rowHeight = Spring.max(rowHeight, Spring.height(password));

        vSpring = Spring.sum(vSpring, rowHeight);
        vSpring = Spring.sum(vSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.NORTH, login, vSpring, SpringLayout.NORTH, panel);

        vSpring = Spring.sum(vSpring, Spring.height(login));
        vSpring = Spring.sum(vSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.SOUTH, panel, vSpring, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.SOUTH, panel, vSpring, SpringLayout.NORTH, panel);
        
        pack();
        
        getRootPane().setOpaque(false);
        getContentPane().setBackground (new Color (0, 0, 0));
        setBackground (new Color (0, 0, 0));
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(128, 128, 128)));

        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
    
}
