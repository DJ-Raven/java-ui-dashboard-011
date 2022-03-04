package javaswingdev.swing.titlebar;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;

public class TitleBar extends JComponent {

    private JFrame fram;
    private ComponentResizer resizer;
    private JPanel panel;
    private boolean register = true;
    private int x;
    private int y;

    public TitleBar() {
        init();
    }

    public void initJFram(JFrame fram) {
        this.fram = fram;
        resizer = new ComponentResizer();
        resizer.setSnapSize(new Dimension(10, 10));
        resizer.setMinimumSize(new Dimension(800, 600));
        resizer.registerComponent(fram);
        fram.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if (e.getNewState() == JFrame.MAXIMIZED_BOTH) {
                    resizer.deregisterComponent(fram);
                    register = false;
                } else if (e.getNewState() == JFrame.NORMAL) {
                    if (register == false) {
                        resizer.registerComponent(fram);
                        register = true;
                    }
                }
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (fram.getExtendedState() == JFrame.NORMAL && SwingUtilities.isLeftMouseButton(e)) {
                    x = e.getX() + 3;
                    y = e.getY() + 3;
                }
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (fram.getExtendedState() == JFrame.NORMAL) {
                        fram.setLocation(e.getXOnScreen() - x, e.getYOnScreen() - y);
                    }
                }
            }
        });
    }

    private void init() {
        setLayout(new MigLayout("inset 3, fill", "[fill]", "[fill]"));
        panel = new JPanel();
        panel.setOpaque(false);
        add(panel);
        panel.setLayout(new MigLayout("inset 3"));
        Item close = new Item();
        Item minimize = new Item();
        Item resize = new Item();
        close.setBackground(new Color(235, 47, 47));
        minimize.setBackground(new Color(220, 213, 53));
        resize.setBackground(new Color(44, 203, 87));
        panel.add(close);
        panel.add(minimize);
        panel.add(resize);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        minimize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fram.setState(JFrame.ICONIFIED);
            }
        });
        resize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fram.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    fram.setExtendedState(JFrame.NORMAL);
                } else {
                    fram.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            }
        });
    }

    private class Item extends JButton {

        public Item() {
            init();
        }

        private void init() {
            setContentAreaFilled(false);
            setBorder(null);
            setPreferredSize(new Dimension(11, 11));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int with = getWidth();
            int height = getHeight();
            g2.setColor(getBackground());
            g2.fillOval(0, 0, with, height);
            g2.dispose();
        }
    }
}
