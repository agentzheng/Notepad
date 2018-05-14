package elliott;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MainClass {
    public static void main(String args[]) {
        JFrame frame = new JFrame("Mnemonic/Accelerator Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Action printAction = new PrintHelloAction();

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("Print");
        KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P,
                InputEvent.CTRL_MASK);
        menuItem.setAccelerator(ctrlP);
        menuItem.addActionListener(printAction);
        menu.add(menuItem);

        JButton fileButton = new JButton("About");
        fileButton.setMnemonic(KeyEvent.VK_A);
        fileButton.addActionListener(printAction);

        frame.setJMenuBar(menuBar);

        frame.add(fileButton, BorderLayout.SOUTH);
        frame.setSize(300, 100);
        frame.setVisible(true);
    }
}

class PrintHelloAction extends AbstractAction {
    private static final Icon printIcon = new MyIcon();

    PrintHelloAction() {
        super("Print", printIcon);
        putValue(Action.SHORT_DESCRIPTION, "Hello, World");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Hello, World");
    }
}

class MyIcon implements Icon {
    public int getIconWidth() {
        return 32;
    }

    public int getIconHeight() {
        return 32;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawString("java2s.com", 0, 20);
    }
}

