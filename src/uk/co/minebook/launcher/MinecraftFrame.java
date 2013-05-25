package uk.co.minebook.launcher;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.minecraft.Launcher;

@SuppressWarnings("serial")
public class MinecraftFrame extends JFrame {
	private Launcher appletWrap = null;
	private String animationname;

        public static JLabel screenshot = new JLabel();
        
	public MinecraftFrame(String title, String imagePath, String animationname) {
		super(title.replace("_", " "));
		this.animationname = animationname;

		setIconImage(Toolkit.getDefaultToolkit().createImage(imagePath));
		super.setVisible(true);
		setResizable(true);
                fixSize(new Dimension(900, 560));
		addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(30000L);
                                } catch (InterruptedException localInterruptedException) { }
                                System.out.println("FORCING EXIT!");
                                System.exit(0);
                            }
                        }.start();
                        if (appletWrap != null) {
                            appletWrap.stop();
                            appletWrap.destroy();
                        }
                        System.exit(0);
                    }
		});
		final MinecraftFrame thisFrame = this;
	}

	public void start(Applet mcApplet, String user, String session) throws MalformedURLException {
		JLabel label = new JLabel();
		Thread animation = new Thread();
		Dimension size = new Dimension(900, 560);
                
                setLayout(new BorderLayout());
                setMinimumSize(new Dimension(900, 30));
                
                if(!animationname.equalsIgnoreCase("empty")) {
                    try {
                        animation.start();
                        label = new JLabel(new ImageIcon(animationname));
                        label.setBounds(0, 0, 900, 560);
                        fixSize(size);
                        getContentPane().setBackground(Color.BLACK);
                        add(label, BorderLayout.CENTER);
                        animation.sleep(5000);
                        animation.stop();
                    } catch (Exception e) {
        		label.add(label);
                    } finally {
                        remove(label);
                    }
                }
                
                // ADD MINEBOOK MENU
                final JLabel menu = new JLabel();
                menu.setBackground(Color.BLACK);
                menu.setForeground(Color.WHITE);
                menu.setBounds(0, 0, 900, 30);
                menu.setIcon(new ImageIcon(new URL("http://modpacks.minebook.co.uk/images/icon.png")));
                
                //add(menu, BorderLayout.NORTH);

                try {
                    appletWrap = new Launcher(mcApplet, new URL("http://www.minecraft.net/game"));
		} catch (MalformedURLException ignored) { }
		
                appletWrap.setParameter("username", user);
		appletWrap.setParameter("sessionid", session);
		appletWrap.setParameter("stand-alone", "true");
		mcApplet.setStub(appletWrap);

                add(appletWrap, BorderLayout.CENTER);

                appletWrap.setPreferredSize(size);
                
                pack();
                validate();
		appletWrap.init();
		appletWrap.start();
		fixSize(size);
		setVisible(true);
                try {
                    LoginHandler.getContentResult(new URL("http://modpacks.minebook.co.uk/addStat.php?type=launch&pack=" + downloadPack.packID));
                } catch (IOException ex) {
                    Logger.getLogger(MinecraftFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
	}
        
        public void centerWindow(Dimension size) {
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (int) ((dimension.getWidth() - size.getWidth()) / 2);
            int y = (int) ((dimension.getHeight() - size.getHeight()) / 2);
            setLocation(x, y);
        }

        public void centerWindow(JLabel label) {
            int x = (int) ((getWidth() - label.getWidth()) / 2);
            int y = (int) ((getHeight() - label.getHeight()) / 2);
            label.setLocation(x, y);
        }

        
        private void fixSize(Dimension size) {
		setSize(size);
		centerWindow(size);
	}
}