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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 *
 * @author MuddyFudger
 */

@SuppressWarnings("serial")
public class updateAvailable extends JDialog {
    
    String packID, mcver;
    
    private JLabel passwordLbl;
    private JButton cancel;
    private JButton update;
    public static JLabel close = new JLabel();
    
    public updateAvailable(JFrame instance, String id, String mcv, boolean modal) throws Exception {
        super(instance, id, modal);
        packID=id;
        mcver = mcv;
        setupGui();
    }
    
    private void setupGui() {
        setTitle("Update Available!");
        setModal(true);
        setSize(451, 87);
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

        passwordLbl = new JLabel("A new version is available for " + packID +", do you want to update it?");
        passwordLbl.setForeground(Color.WHITE);
        cancel = new JButton("NO");
        cancel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    dispose();
                    new launchModPack(packID);
                } catch (Exception ex) {
                    Logger.getLogger(updateAvailable.class.getName()).log(Level.SEVERE, null, ex);
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
        update = new JButton("YES");
        update.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    dispose();
                    new downloadPack(packID, mcver, true);
                } catch (Exception ex) {
                    Logger.getLogger(updateAvailable.class.getName()).log(Level.SEVERE, null, ex);
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
        panel.add(cancel);
        panel.add(update);

        Spring hSpring;

        hSpring = Spring.constant(0);

        layout.putConstraint(SpringLayout.EAST, close, hSpring, SpringLayout.EAST, panel);

        hSpring = Spring.constant(0);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, passwordLbl, hSpring, SpringLayout.HORIZONTAL_CENTER, panel);

        hSpring = Spring.sum(hSpring, Spring.width(passwordLbl));
        hSpring = Spring.sum(hSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.WEST, cancel, 150, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, update, -150, SpringLayout.EAST, panel);

        hSpring = Spring.sum(hSpring, Spring.width(cancel));
        hSpring = Spring.sum(hSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.EAST, panel, hSpring, SpringLayout.WEST, panel);

        Spring vSpring;

        vSpring = Spring.constant(-5);

        layout.putConstraint(SpringLayout.BASELINE, close, vSpring, SpringLayout.BASELINE, panel);

        vSpring = Spring.constant(25);

        layout.putConstraint(SpringLayout.BASELINE, passwordLbl, vSpring, SpringLayout.BASELINE, panel);

        vSpring = Spring.sum(vSpring, Spring.height(passwordLbl));
        vSpring = Spring.sum(vSpring, Spring.constant(10));

        layout.putConstraint(SpringLayout.NORTH,    cancel,    vSpring, SpringLayout.NORTH,    panel);
        layout.putConstraint(SpringLayout.NORTH, update, vSpring, SpringLayout.NORTH, panel);

        vSpring = Spring.sum(vSpring, Spring.height(cancel));
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
