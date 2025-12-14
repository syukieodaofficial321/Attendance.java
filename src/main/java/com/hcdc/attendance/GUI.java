package com.hcdc.attendance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Minimal GUI: left sidebar for input and center table.
 */
public class GUI {
    private final JFrame frame;
    private final JTextField txtStudentId = new JTextField(12);
    private final JTextField txtFullName = new JTextField(16);
    private final JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Present","Absent","Late","Excused"});
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnDelete = new JButton("Delete");
    private final JButton btnImport = new JButton("Import");
    private final JButton btnExport = new JButton("Export CSV");
    // Columns: Number (display), _dbid (hidden), Student ID, Full Name, Status, Timestamp
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Number","_dbid","Student ID","Full Name","Status","Timestamp"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JLabel lblUser = new JLabel();
    private JPanel welcomePanel;
    private JLabel greetingLabel;
    private JLabel quoteLabel;
    private final JButton btnSettings = new JButton("Settings");
    private final JTextArea noteArea = new JTextArea(10, 24);
    private String loggedInUsername = null;

    public GUI() {
        Theme.applyGlobal();
        frame = new JFrame("Attendance");
        initUI();
        enforceNumericStudentId();
    }

    private void enforceNumericStudentId() {
        AbstractDocument doc = (AbstractDocument) txtStudentId.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("[^0-9]", "");
                // Limit to 8 digits
                if (fb.getDocument().getLength() + filtered.length() <= 8) {
                    super.insertString(fb, offset, filtered, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                String filtered = text.replaceAll("[^0-9]", "");
                if (fb.getDocument().getLength() - length + filtered.length() <= 8) {
                    super.replace(fb, offset, length, filtered, attrs);
                }
            }
        });
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 12) return "Good morning";
        if (hour >= 12 && hour < 18) return "Good afternoon";
        return "Good evening";
    }

    private void initUI() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        // Use Theme for colors and fonts
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("  Attendance Dashboard");
        Theme.styleHeader(header, title, null, lblUser);
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(lblUser);
        Theme.styleButton(btnSettings);
        headerRight.add(btnSettings);

        // Welcome panel in dashboard (welcome note + greeting)
        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(Theme.HEADER_FONT.deriveFont(20f));
        welcome.setForeground(Color.WHITE);
        greetingLabel = new JLabel(getGreeting());
        greetingLabel.setFont(Theme.PRIMARY_FONT.deriveFont(14f));
        greetingLabel.setForeground(Color.WHITE);
        quoteLabel = new JLabel(QuoteOfTheDay.getQuote());
        quoteLabel.setFont(Theme.PRIMARY_FONT.deriveFont(12f));
        quoteLabel.setForeground(Color.WHITE);
        welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new javax.swing.BoxLayout(welcomePanel, javax.swing.BoxLayout.Y_AXIS));
        welcomePanel.add(welcome);
        welcomePanel.add(greetingLabel);
        welcomePanel.add(quoteLabel);

        header.add(title, BorderLayout.WEST);
        header.add(welcomePanel, BorderLayout.CENTER);
        header.add(headerRight, BorderLayout.EAST);

        JPanel left = new JPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder("New Attendance"));
        left.setBackground(Theme.PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        // logo at top of left panel
        JLabel logoLabel = new JLabel(getLogoIcon());
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0; left.add(logoLabel, gbc);

        // form fields below logo
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        // Scan input removed; ID will be auto-populated when scanned/entered

        gbc.gridy = 2; gbc.gridx = 0; left.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1; left.add(txtStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 3; left.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; left.add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = 4; left.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; left.add(cbStatus, gbc);

        // spacer consumes extra vertical space and pushes controls below to the bottom
        JPanel spacer = new JPanel(); spacer.setOpaque(false);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL;
        left.add(spacer, gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE;

        // Scan controls removed

        // main action buttons anchored at bottom
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,8,6)); btns.setBackground(Theme.PANEL_BG);
        btns.add(btnAdd); btns.add(btnDelete); btns.add(btnImport); btns.add(btnExport);
        // style buttons via Theme
        for (JButton b : new JButton[]{btnAdd, btnDelete, btnImport, btnExport}) Theme.styleButton(b);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.SOUTH; left.add(btns, gbc);

        left.setPreferredSize(new Dimension(380,0));

        // Notes panel on the right
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setText("Notes:\n- Add instructions here\n- Reminders for teachers");
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setBorder(BorderFactory.createTitledBorder("Notes"));
        noteScroll.setPreferredSize(new Dimension(360, 200));
        Theme.stylePanel((JComponent)noteScroll.getViewport().getView());

        table.setFillsViewportHeight(true);
        // table header styling
        Theme.styleTableHeader(table);
        // alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            private final Color evenColor = Theme.TABLE_EVEN;
            private final Color oddColor = Theme.TABLE_ODD;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground((row % 2 == 0) ? evenColor : oddColor);
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(table);

        // Hide DB id internal column
        try {
            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setWidth(0);
        } catch (Exception ignored) {}

        frame.setLayout(new BorderLayout());
        frame.add(header, BorderLayout.NORTH);
        frame.add(left, BorderLayout.WEST);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(noteScroll, BorderLayout.EAST);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
    }

    public void addSettingsListener(ActionListener l) { btnSettings.addActionListener(l); }
    public void setUsername(String username) { 
        loggedInUsername = username;
        SwingUtilities.invokeLater(() -> {
            lblUser.setText("Logged in: " + (username == null ? "" : username));
            // Hide welcome/greeting when user is logged in
            if (welcomePanel != null) {
                welcomePanel.setVisible(username == null || username.isEmpty());
            }
        });
    }
    public String getLoggedInUsername() { return loggedInUsername; }

    public void addAddListener(ActionListener l) { btnAdd.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addImportListener(ActionListener l) { btnImport.addActionListener(l); }
    public void addExportListener(ActionListener l) { btnExport.addActionListener(l); }
    public void addClearListener(ActionListener l) { /* Clear is handled by clearInputs */ }
    public JFrame getFrame() { return frame; }
    public void showError(String title, String msg) { JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.ERROR_MESSAGE); }
    public void showError(String msg) { showError("Error", msg); }
    public void showInfo(String title, String msg) { JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.INFORMATION_MESSAGE); }
    public void showInfo(String msg) { showInfo("Information", msg); }
    public void showWarning(String title, String msg) { JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.WARNING_MESSAGE); }
    // scan text methods removed
    public void setStudentId(String id) { SwingUtilities.invokeLater(() -> txtStudentId.setText(id == null ? "" : id)); }
    public void setFullName(String name) { SwingUtilities.invokeLater(() -> txtFullName.setText(name == null ? "" : name)); }
    public void focusFullName() { SwingUtilities.invokeLater(() -> txtFullName.requestFocusInWindow()); }
    
    // Register a listener invoked when the Student ID field reaches exactly 8 digits.
    public void addStudentIdCompleteListener(ActionListener l) {
        javax.swing.text.Document doc = txtStudentId.getDocument();
        doc.addDocumentListener(new DocumentListener() {
            private void check() {
                String v = txtStudentId.getText().trim();
                if (v.length() == 8) {
                    l.actionPerformed(new ActionEvent(GUI.this, ActionEvent.ACTION_PERFORMED, v));
                }
            }
            public void insertUpdate(DocumentEvent e) { check(); }
            public void removeUpdate(DocumentEvent e) { /* no-op on remove */ }
            public void changedUpdate(DocumentEvent e) { /* no-op */ }
        });
    }
    public void clickAdd() { SwingUtilities.invokeLater(() -> btnAdd.doClick()); }
    public String getStudentId() { return txtStudentId.getText().trim(); }
    public String getFullName() { return txtFullName.getText().trim(); }
    public String getStatus() { return (String) cbStatus.getSelectedItem(); }
    public void clearInputs() { txtStudentId.setText(""); txtFullName.setText(""); cbStatus.setSelectedIndex(0); }
    public void clearTable() { tableModel.setRowCount(0); }
    public void addRow(Object[] row) {
        // row expected: {displayNumber, dbId, studentId, fullName, status, timestamp}
        int cols = tableModel.getColumnCount();
        Object[] newRow = new Object[cols];
        // Set display Number to next sequential
        newRow[0] = tableModel.getRowCount() + 1;
        // Copy DB id and remaining columns from provided row (row[1]..)
        for (int i = 1; i < Math.min(row.length, cols); i++) {
            newRow[i] = row[i];
        }
        tableModel.addRow(newRow);
    }

    public int getSelectedRecordId() {
        int r = table.getSelectedRow();
        if (r == -1) return -1;
        Object v = tableModel.getValueAt(r, 1); // DB id is in hidden column index 1
        return v == null ? -1 : Integer.parseInt(v.toString());
    }
    public void showFullScreen() { SwingUtilities.invokeLater(() -> { frame.setExtendedState(JFrame.MAXIMIZED_BOTH); frame.setVisible(true); }); }
    public void show() { SwingUtilities.invokeLater(() -> frame.setVisible(true)); }
    public void hideWindow() { SwingUtilities.invokeLater(() -> frame.setVisible(false)); }

    private ImageIcon getLogoIcon() {
        int w = 220, h = 80;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // background
            g.setColor(Theme.ACCENT);
            g.fillRoundRect(0, 0, w, h, 16, 16);
            // draw acronym or text
            g.setColor(Color.white);
            // main text: Attendance
            String text = "Attendance";
            // reduce font size to fit longer text
            Font mainFont = Theme.HEADER_FONT.deriveFont(22f);
            g.setFont(mainFont);
            FontMetrics fmMain = g.getFontMetrics();
            int tx = (w - fmMain.stringWidth(text)) / 2;
            int ty = (h - fmMain.getHeight()) / 2 + fmMain.getAscent();
            g.drawString(text, tx, ty);
        } finally {
            g.dispose();
        }
        return new ImageIcon(img);
    }
}
