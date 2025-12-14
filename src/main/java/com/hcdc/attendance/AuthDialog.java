package com.hcdc.attendance;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AuthDialog extends JDialog {
    public enum Action { LOGIN, SIGNUP, CANCEL }
    private Action action = Action.CANCEL;

    private final JTextField txtUsername = new JTextField(24);
    private final JPasswordField txtPassword = new JPasswordField(24);

    private final JTextField suUsername = new JTextField(24);
    private final JPasswordField suPassword = new JPasswordField(24);

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public AuthDialog(Frame owner) {
        super(owner, true);
        Theme.applyGlobal();
        initUI(owner);
    }

    private void initUI(Frame owner) {
        setTitle("Authentication");
        // Make this dialog cover the full screen without window decorations
        setUndecorated(true);
        setModal(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screen.width, screen.height);

        // create a full-screen gradient background panel
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth(), h = getHeight();
                Color c1 = Theme.HEADER_BG;
                Color c2 = Theme.ACCENT;
                g2.setPaint(new java.awt.GradientPaint(0,0,c1, w, h, c2));
                g2.fillRect(0,0,w,h);
                g2.dispose();
            }
        };
        bg.setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(18, Color.white);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(480, 380));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(8,8,8,8); c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.CENTER; c.fill = GridBagConstraints.HORIZONTAL;

        // title
        JLabel title = new JLabel("Attendance"); title.setFont(Theme.HEADER_FONT);
        title.setForeground(Theme.HEADER_BG);
        c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER; card.add(title, c);

        // username
        c.gridy = 1; c.gridwidth = 1; c.anchor = GridBagConstraints.WEST;
        JLabel lu = new JLabel("Username"); lu.setFont(Theme.PRIMARY_FONT);
        card.add(lu, c);
        c.gridx = 1; card.add(txtUsername, c);

        // password
        c.gridx = 0; c.gridy = 2; JLabel lp = new JLabel("Password"); lp.setFont(Theme.PRIMARY_FONT); card.add(lp, c);
        c.gridx = 1; card.add(txtPassword, c);

        // buttons
        JButton btnLogin = new JButton("Login"); Theme.styleButton(btnLogin); btnLogin.setPreferredSize(new Dimension(160,36)); btnLogin.setForeground(Color.black);
        JButton btnToSign = new JButton("Create Account"); Theme.styleButton(btnToSign); btnToSign.setPreferredSize(new Dimension(160,36)); btnToSign.setForeground(Color.black);
        JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8)); pBtns.setOpaque(false); pBtns.add(btnLogin); pBtns.add(btnToSign);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; card.add(pBtns, c);

        // small note
        c.gridy = 4; JLabel note = new JLabel("Use your barcode scanner or enter credentials."); note.setFont(Theme.PRIMARY_FONT.deriveFont(12f)); note.setForeground(Color.DARK_GRAY); card.add(note, c);

        // actions
        btnLogin.addActionListener(e -> { action = Action.LOGIN; setVisible(false); });
        btnToSign.addActionListener(e -> cards.show(root, "signup"));

        // signup card (simpler)
        RoundedPanel card2 = new RoundedPanel(18, Color.white);
        card2.setLayout(new GridBagLayout());
        GridBagConstraints s = new GridBagConstraints(); s.insets = new Insets(8,8,8,8); s.gridx = 0; s.gridy = 0; s.fill = GridBagConstraints.HORIZONTAL;
        JLabel st = new JLabel("Create Account"); st.setFont(Theme.HEADER_FONT); st.setForeground(Theme.HEADER_BG); card2.add(st, s);
        s.gridy = 1; card2.add(new JLabel("Username"), s); s.gridx = 1; card2.add(suUsername, s);
        s.gridx = 0; s.gridy = 2; card2.add(new JLabel("Password"), s); s.gridx = 1; card2.add(suPassword, s);
        s.gridx = 0; s.gridy = 3; s.gridwidth = 2; JPanel sp = new JPanel(new FlowLayout(FlowLayout.CENTER,12,8)); sp.setOpaque(false);
        JButton btnCreate = new JButton("Create"); Theme.styleButton(btnCreate); btnCreate.setPreferredSize(new Dimension(160,36)); btnCreate.setForeground(Color.black);
        JButton btnBack = new JButton("Back"); Theme.styleButton(btnBack); btnBack.setPreferredSize(new Dimension(100,36)); btnBack.setForeground(Color.black);
        sp.add(btnCreate); sp.add(btnBack); card2.add(sp, s);
        btnCreate.addActionListener(e -> { action = Action.SIGNUP; setVisible(false); });
        btnBack.addActionListener(e -> cards.show(root, "login"));

        root.add(card, "login");
        root.add(card2, "signup");

        GridBagConstraints rc = new GridBagConstraints(); rc.gridx = 0; rc.gridy = 0; bg.add(root, rc);
        setContentPane(bg);
        cards.show(root, "login");
    }

    // small rounded panel helper
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        public RoundedPanel(int radius, Color bg) { this.radius = radius; this.bg = bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(Theme.ACCENT);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public void showDialog() { setVisible(true); }

    public Action getAction() { return action; }
    public String getUsername() { return action == Action.SIGNUP ? suUsername.getText().trim() : txtUsername.getText().trim(); }
    public String getPassword() { return action == Action.SIGNUP ? new String(suPassword.getPassword()) : new String(txtPassword.getPassword()); }
}
