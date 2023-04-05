import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class TextEditor extends JFrame {
    private final Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    private int currentMatchIndex = 0;
    private DocumentSearcher documentSearcher;
    private JTextField searchField;
    private final JCheckBox regexCheckBox = new JCheckBox();
    private JScrollPane scrollPane;

    private final ImageIcon openIcon = new ImageIcon("src/icons/open.png");
    private final ImageIcon saveIcon = new ImageIcon("src/icons/save.png");
    private final ImageIcon searchIcon = new ImageIcon("src/icons/search.png");
    private final ImageIcon nextIcon = new ImageIcon("src/icons/right.png");
    private final ImageIcon previousIcon = new ImageIcon("src/icons/left.png");


    public TextEditor() {
        // Creating the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setTitle("NotePad Deluxe");

        // Creating the writable area
        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");
        textArea.setColumns(10);
        textArea.setRows(1);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        add(scrollPane);

        // Creating JFileChooser
        JFileChooser fileChooser = initFileChooser();

        // Creating JMenuBar and associates functionality
        JMenuBar menuBar = createMenuBar(textArea, fileChooser);
        add(menuBar, BorderLayout.NORTH);

        // Panel that holds all file loading and saving components.
        JPanel FileSystem = createFileSystemPanel(textArea, fileChooser);
        FileSystem.setBackground(new Color(181, 111, 222));
        add(FileSystem, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JFileChooser initFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setName("FileChooser");
        this.add(fileChooser, BorderLayout.EAST);
        fileChooser.setVisible(false);
        return fileChooser;
    }

    private JMenuBar createMenuBar(JTextComponent TextArea, JFileChooser fileChooser) {
        JMenuBar menuBar = new JMenuBar();
        // File menu
        JMenu file = createFileMenu(TextArea, fileChooser);
        menuBar.add(file);

        // Search menu
        JMenu search = createSearchMenu(TextArea);
        menuBar.add(search);

        return menuBar;
    }

    private JMenu createFileMenu(JTextComponent TextArea, JFileChooser fileChooser) {
        JMenu file = new JMenu("File");
        file.setName("MenuFile");

        // Open menu item
        JMenuItem load = new JMenuItem("open");
        load.setName("MenuOpen");
        load.addActionListener(e -> loadFile(TextArea, fileChooser));
        file.add(load);

        // Save menu item
        JMenuItem save = new JMenuItem("save");
        save.setName("MenuSave");
        save.addActionListener(e -> saveFile(TextArea, fileChooser));
        file.add(save);

        // Exit menu item
        JMenuItem exit = new JMenuItem("exit");
        exit.setName("MenuExit");
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);

        return file;
    }

    private JMenu createSearchMenu(JTextComponent TextArea) {
        JMenu search = new JMenu("Search");
        search.setName("MenuSearch");

        // Start search menu item
        JMenuItem startSearch = new JMenuItem("Start Search");
        startSearch.setName("MenuStartSearch");
        startSearch.addActionListener(e -> {
            String searchText = searchField.getText();
            boolean useRegex = regexCheckBox.isSelected();
            documentSearcher = new DocumentSearcher(TextArea, searchText, useRegex);
            documentSearcher.highlightMatches(highlightPainter);
            currentMatchIndex = documentSearcher.selectNextMatch(-1);
        });
        search.add(startSearch);

        // Previous match menu item
        JMenuItem previousMatch = new JMenuItem("Previous Match");
        previousMatch.setName("MenuPreviousMatch");
        previousMatch.addActionListener(e -> {
            if (documentSearcher != null) {
                currentMatchIndex = documentSearcher.selectPreviousMatch(currentMatchIndex);
            }
        });
        search.add(previousMatch);
        // Next match menu item
        JMenuItem nextMatch = new JMenuItem("Next Match");
        nextMatch.setName("MenuNextMatch");
        nextMatch.addActionListener(e -> {
            if (documentSearcher != null) {
                currentMatchIndex = documentSearcher.selectNextMatch(currentMatchIndex);
            }
        });
        search.add(nextMatch);

        // Use regex menu item
        JMenuItem regex = new JMenuItem("Use Regex");
        regex.setName("MenuUseRegExp");
        regex.addActionListener(e -> regexCheckBox.setSelected(!regexCheckBox.isSelected()));
        search.add(regex);

        return search;
    }

    private JPanel createFileSystemPanel(JTextComponent TextArea, JFileChooser fileChooser) {
        JPanel FileSystem = new JPanel();

        // Load button
        JButton loadButton = new JButton(openIcon);
        loadButton.setName("OpenButton");
        loadButton.addActionListener(e -> loadFile(TextArea, fileChooser));
        FileSystem.add(loadButton);

        // Save button
        JButton saveButton = new JButton(saveIcon);
        saveButton.setName("SaveButton");
        saveButton.addActionListener(e -> saveFile(TextArea, fileChooser));
        FileSystem.add(saveButton);

        // Search components
        searchField = new JTextField();
        searchField.setName("SearchField");

        forceSize(searchField, 300, 35);
        FileSystem.add(searchField);

        // Start search button
        JButton startSearchButton = new JButton(searchIcon);
        startSearchButton.setName("StartSearchButton");
        startSearchButton.addActionListener(e -> {
            String searchText = searchField.getText();
            boolean useRegex = regexCheckBox.isSelected();
            documentSearcher = new DocumentSearcher(TextArea, searchText, useRegex);
            documentSearcher.highlightMatches(highlightPainter);
            currentMatchIndex = documentSearcher.selectNextMatch(-1);
        });
        FileSystem.add(startSearchButton);

        // Previous match button
        JButton previousMatchButton = new JButton(previousIcon);
        previousMatchButton.setName("PreviousMatchButton");
        previousMatchButton.addActionListener(e -> {
            if (documentSearcher != null) {
                currentMatchIndex = documentSearcher.selectPreviousMatch(currentMatchIndex);
            }
        });
        FileSystem.add(previousMatchButton);

        // Next match button
        JButton nextMatchButton = new JButton(nextIcon);
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(e -> {
            if (documentSearcher != null) {
                currentMatchIndex = documentSearcher.selectNextMatch(currentMatchIndex);
            }
        });
        FileSystem.add(nextMatchButton);

        // Regex checkbox
        regexCheckBox.setName("UseRegExCheckbox");
        regexCheckBox.setText("RegEx");
        FileSystem.add(regexCheckBox);

        return FileSystem;
    }

    private void loadFile(JTextComponent TextArea, JFileChooser fileChooser) {
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setVisible(true);
        int returnValue = fileChooser.showOpenDialog(null);
        fileChooser.setVisible(false);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                TextArea.setText(new String(fis.readAllBytes()));
            } catch (IOException fileNotFoundException) {
                TextArea.setText("");
                fileNotFoundException.printStackTrace();
            }
        }
        scrollPane.setVisible(true);
    }

    private void saveFile(JTextComponent TextArea, JFileChooser fileChooser) {
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setVisible(true);
        int returnValue = fileChooser.showSaveDialog(null);
        fileChooser.setVisible(false);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String text = TextArea.getText();
            try {
                FileOutputStream fos = new FileOutputStream(selectedFile);
                fos.write(text.getBytes());
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
        scrollPane.setVisible(true);
    }

    private static void forceSize(JComponent component, int width, int height) {
        Dimension d = new Dimension(width, height);
        component.setMinimumSize(d);
        component.setMaximumSize(d);
        component.setPreferredSize(d);
    }
}