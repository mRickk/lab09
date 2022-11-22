package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
//import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private final static long COUNTDOWN = 10_000L;
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int)(screenSize.getWidth() * WIDTH_PERC), (int)(screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stop);
        panel.add(up);
        panel.add(down);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();
        stop.addActionListener(e -> agent.stopCounting());
        up.addActionListener(e -> agent.goUp());
        down.addActionListener(e -> agent.goDown());

        Runnable countdown = new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(AnotherConcurrentGUI.COUNTDOWN);
                    agent.stopCounting();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        };
        new Thread(countdown).start();
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean isUp = true;
        private int counter = 0;

        @Override
        public void run() {
            while(!stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (this.isUp) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InterruptedException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }    
        }

        public void stopCounting() {
            AnotherConcurrentGUI.this.up.setEnabled(false);
            AnotherConcurrentGUI.this.down.setEnabled(false);
            this.stop = true;
        }

        public void goUp() {
            this.isUp = true;
        }

        public void goDown() {
            this.isUp = false;
        }

    }
}
