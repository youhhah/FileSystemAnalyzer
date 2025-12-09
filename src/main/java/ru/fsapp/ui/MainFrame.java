package ru.fsapp.ui;

import org.apache.log4j.Logger;
import ru.fsapp.model.FileNode;
import ru.fsapp.service.FileSystemService;
import ru.fsapp.util.FormatUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Main window of file system analysis application.
 * Contains file tree, properties panel and action buttons.
 * Supports deletion, renaming and file creation.
 *
 * @author Ahmed
 * @version 1.0
 * @since 2025-12-05
 */
public class MainFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(MainFrame.class);

    // Top panel
    private JTextField pathField;
    private JButton browseBtn;
    private JButton analyzeBtn;

    // Tree
    private JTree tree;

    // Properties panel
    private JLabel nameLabel;
    private JLabel pathLabel;
    private JLabel parentLabel;
    private JLabel typeLabel;
    private JLabel sizeLabel;
    private JLabel diskSizeLabel;
    private JLabel createdLabel;
    private JLabel modifiedLabel;
    private JLabel ownerLabel;
    private JLabel attributesLabel;
    private JLabel statsLabel;
    private JLabel readableLabel;
    private JLabel writableLabel;
    private JLabel executableLabel;
    private JLabel hiddenLabel;
    private JLabel canonicalPathLabel;
    private JLabel absolutePathLabel;
    // Action buttons
    private JButton copyBtn;
    private JButton openBtn;
    private JButton deleteBtn;
    private JButton createFileBtn;

    // Status
    private JLabel statusLabel;

    public MainFrame() {
        logger.info("Initializing main application window");

        setTitle("File System Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setBackground(FlatUITheme.BG_PRIMARY);

        buildUI();

        logger.info("Main window initialized successfully");
    }

    // ---------------------- INTERFACE BUILDING ----------------------

    private void buildUI() {
        logger.debug("Starting interface construction");

        // Top panel for path input
        JPanel topPanel = new JPanel(new BorderLayout(8, 0));
        topPanel.setBackground(FlatUITheme.BG_PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel pathLabelTitle = new JLabel("Path:");
        pathLabelTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        pathLabelTitle.setForeground(FlatUITheme.TEXT_PRIMARY);

        pathField = FlatUITheme.createTextField();

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsPanel.setBackground(FlatUITheme.BG_PRIMARY);

        browseBtn = FlatUITheme.createButton("Browse");
        browseBtn.addActionListener(e -> browse());
        analyzeBtn = FlatUITheme.createButton("Analyze");
        analyzeBtn.addActionListener(e -> analyze());

        buttonsPanel.add(browseBtn);
        buttonsPanel.add(analyzeBtn);

        topPanel.add(pathLabelTitle, BorderLayout.WEST);
        topPanel.add(pathField, BorderLayout.CENTER);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Main split panel (tree + properties)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.35);

        // Left part: tree
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(FlatUITheme.BG_PRIMARY);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 5));

        JLabel treeTitle = new JLabel("Folder Structure");
        treeTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        treeTitle.setForeground(FlatUITheme.TEXT_PRIMARY);
        treeTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        tree = new JTree(new DefaultMutableTreeNode("No data"));
        tree.setBackground(FlatUITheme.BG_SECONDARY);
        tree.setForeground(FlatUITheme.TEXT_PRIMARY);
        tree.setCellRenderer(new SimpleTreeCellRenderer());
        tree.addTreeSelectionListener(e -> updateDetails());

        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setBorder(BorderFactory.createLineBorder(FlatUITheme.BORDER));

        leftPanel.add(treeTitle, BorderLayout.NORTH);
        leftPanel.add(treeScroll, BorderLayout.CENTER);

        // Right part: properties + buttons
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(FlatUITheme.BG_PRIMARY);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 10));

        // Properties header
        JLabel propsTitle = new JLabel("File Properties");
        propsTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        propsTitle.setForeground(FlatUITheme.TEXT_PRIMARY);
        propsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        // Properties panel
        JPanel propsPanel = new JPanel();
        propsPanel.setLayout(new BoxLayout(propsPanel, BoxLayout.Y_AXIS));
        propsPanel.setBackground(FlatUITheme.BG_SECONDARY);
        propsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FlatUITheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        nameLabel        = createPropLabel("Name: (not selected)");
        pathLabel        = createPropLabel("Path: (not selected)");
        parentLabel      = createPropLabel("Parent folder: (not selected)");
        typeLabel        = createPropLabel("Type: (not selected)");
        sizeLabel        = createPropLabel("Size: (not selected)");
        diskSizeLabel    = createPropLabel("Disk size: (not selected)");
        createdLabel     = createPropLabel("Created: (not selected)");
        modifiedLabel    = createPropLabel("Modified: (not selected)");
        ownerLabel       = createPropLabel("Owner: (not selected)");
        attributesLabel  = createPropLabel("Attributes: (not selected)");

        readableLabel    = createPropLabel("Readable: (not selected)");
        writableLabel    = createPropLabel("Writable: (not selected)");
        executableLabel  = createPropLabel("Executable: (not selected)");
        hiddenLabel      = createPropLabel("Hidden: (not selected)");
        absolutePathLabel = createPropLabel("Absolute path: (not selected)");
        canonicalPathLabel = createPropLabel("Canonical path: (not selected)");


        statsLabel       = createPropLabel("Statistics: (not selected)");

        addProp(propsPanel, nameLabel);
        addProp(propsPanel, pathLabel);
        addProp(propsPanel, parentLabel);
        addProp(propsPanel, typeLabel);
        addProp(propsPanel, sizeLabel);
        addProp(propsPanel, diskSizeLabel);
        addProp(propsPanel, createdLabel);
        addProp(propsPanel, modifiedLabel);
        addProp(propsPanel, ownerLabel);
        addProp(propsPanel, attributesLabel);
        addProp(propsPanel, readableLabel);      // New
        addProp(propsPanel, writableLabel);      // New
        addProp(propsPanel, executableLabel);    // New
        addProp(propsPanel, hiddenLabel);        // New
        addProp(propsPanel, absolutePathLabel);  // New
        addProp(propsPanel, canonicalPathLabel); // New
        addProp(propsPanel, statsLabel);


        JScrollPane propsScroll = new JScrollPane(propsPanel);
        propsScroll.setBorder(null);

        // Bottom action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        actionsPanel.setBackground(FlatUITheme.BG_PRIMARY);

        copyBtn = FlatUITheme.createButton("Copy Path");
        copyBtn.addActionListener(e -> copyPath());

        openBtn = FlatUITheme.createButton("Open");
        openBtn.addActionListener(e -> openInExplorer());

        JButton renameBtn = FlatUITheme.createButton("Rename");
        renameBtn.addActionListener(e -> renameFile());

        deleteBtn = FlatUITheme.createButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelected());

        createFileBtn = FlatUITheme.createButton("Create File");
        createFileBtn.addActionListener(e -> createNewFile());

        actionsPanel.add(copyBtn);
        actionsPanel.add(openBtn);
        actionsPanel.add(renameBtn);
        actionsPanel.add(deleteBtn);
        actionsPanel.add(createFileBtn);

        JPanel rightContent = new JPanel(new BorderLayout());
        rightContent.setBackground(FlatUITheme.BG_PRIMARY);
        rightContent.add(propsTitle, BorderLayout.NORTH);
        rightContent.add(propsScroll, BorderLayout.CENTER);
        rightContent.add(actionsPanel, BorderLayout.SOUTH);

        rightPanel.add(rightContent, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(FlatUITheme.SUCCESS);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(FlatUITheme.BG_SECONDARY);
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, FlatUITheme.BORDER));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Assemble window
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        logger.debug("Interface built successfully");
    }

    private JLabel createPropLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(FlatUITheme.TEXT_PRIMARY);
        return l;
    }

    private void addProp(JPanel panel, JComponent comp) {
        panel.add(comp);
        panel.add(Box.createVerticalStrut(5));
    }

    // ---------------------- LOGIC ----------------------

    private void browse() {
        logger.debug("Opening folder selection dialog");

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            pathField.setText(dir.getAbsolutePath());
            logger.info("Selected folder: " + dir.getAbsolutePath());
        }
    }

    private void analyze() {
        logger.info("Starting file system analysis");

        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            logger.warn("Attempted analysis without selected folder");
            JOptionPane.showMessageDialog(this, "Select a folder for analysis.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        statusLabel.setText("Analyzing...");
        statusLabel.setForeground(FlatUITheme.INFO);
        browseBtn.setEnabled(false);
        analyzeBtn.setEnabled(false);

        new SwingWorker<FileNode, Void>() {
            @Override
            protected FileNode doInBackground() throws Exception {
                return FileSystemService.buildTree(path);
            }

            @Override
            protected void done() {
                try {
                    FileNode root = get();
                    DefaultMutableTreeNode treeRoot = toTreeNode(root);
                    tree.setModel(new DefaultTreeModel(treeRoot));
                    tree.expandRow(0);
                    statusLabel.setText("Analysis completed");
                    statusLabel.setForeground(FlatUITheme.SUCCESS);
                    clearProps();
                    logger.info("Analysis completed successfully");
                } catch (Exception ex) {
                    statusLabel.setText("Analysis error");
                    statusLabel.setForeground(FlatUITheme.ERROR);
                    logger.error("Error during analysis", ex);
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Analysis error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    browseBtn.setEnabled(true);
                    analyzeBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    private DefaultMutableTreeNode toTreeNode(FileNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        if (node.isDirectory()) {
            for (FileNode child : node.getChildren()) {
                treeNode.add(toTreeNode(child));
            }
        }
        return treeNode;
    }

    private FileNode getSelectedFileNode() {
        Object sel = tree.getLastSelectedPathComponent();
        if (sel == null) return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) sel;
        Object obj = node.getUserObject();
        if (obj instanceof FileNode) {
            return (FileNode) obj;
        }
        return null;
    }

    private void updateDetails() {
        FileNode fn = getSelectedFileNode();
        if (fn == null) {
            clearProps();
            return;
        }

        logger.debug("Updating file properties: " + fn.getName());

        Path p = fn.getPath();
        nameLabel.setText("Name: " + fn.getName());
        pathLabel.setText("Path: " + p.toAbsolutePath());
        Path parent = p.getParent();
        parentLabel.setText("Parent folder: " + (parent != null ? parent.toAbsolutePath() : "(none)"));
        typeLabel.setText("Type: " + (fn.isDirectory() ? "Folder" : "File"));

        if (fn.isDirectory()) {
            sizeLabel.setText("Size: " + fn.getChildren().size() + " elements");
        } else {
            sizeLabel.setText("Size: " + FormatUtils.formatSize(fn.getSize()));
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
            createdLabel.setText("Created: " + FormatUtils.formatDate(attrs.creationTime()));
            modifiedLabel.setText("Modified: " + FormatUtils.formatDate(attrs.lastModifiedTime()));

            long logicalSize = attrs.size();
            long block = 4096;
            long diskSize = ((logicalSize + block - 1) / block) * block;
            diskSizeLabel.setText("Disk size: " + FormatUtils.formatSize(diskSize));
        } catch (Exception ex) {
            logger.warn("Error reading file attributes", ex);
            createdLabel.setText("Created: (read error)");
            modifiedLabel.setText("Modified: (read error)");
            diskSizeLabel.setText("Disk size: (read error)");
        }

        ownerLabel.setText("Owner: " + fn.getOwner());

        try {
            File f = p.toFile();
            StringBuilder sb = new StringBuilder();
            sb.append(f.canWrite() ? "writable" : "read-only");
            sb.append("; ");
            sb.append(f.isHidden() ? "hidden" : "visible");
            attributesLabel.setText("Attributes: " + sb);

            // New fields
            readableLabel.setText("Readable: " + (f.canRead() ? "Yes" : "No"));
            writableLabel.setText("Writable: " + (f.canWrite() ? "Yes" : "No"));
            executableLabel.setText("Executable: " + (f.canExecute() ? "Yes" : "No"));
            hiddenLabel.setText("Hidden: " + (f.isHidden() ? "Yes" : "No"));
            absolutePathLabel.setText("Absolute path: " + f.getAbsolutePath());
            try {
                canonicalPathLabel.setText("Canonical path: " + f.getCanonicalPath());
            } catch (Exception ex2) {
                canonicalPathLabel.setText("Canonical path: (error)");
            }
        } catch (Exception ex) {
            logger.debug("Error reading attributes", ex);
            attributesLabel.setText("Attributes: (read error)");
            readableLabel.setText("Readable: (error)");
            writableLabel.setText("Writable: (error)");
            executableLabel.setText("Executable: (error)");
            hiddenLabel.setText("Hidden: (error)");
            absolutePathLabel.setText("Absolute path: (error)");
            canonicalPathLabel.setText("Canonical path: (error)");
        }

        if (fn.isDirectory()) {
            long files = FormatUtils.countFiles(p);
            long dirs = FormatUtils.countDirectories(p);
            long totalSize = FormatUtils.calculateTotalSize(p);
            statsLabel.setText("Statistics: " + files + " files, " + dirs +
                    " folders, " + FormatUtils.formatSize(totalSize));
        } else {
            statsLabel.setText("Statistics: single file");
        }
    }


    private void clearProps() {
        nameLabel.setText("Name: (not selected)");
        pathLabel.setText("Path: (not selected)");
        parentLabel.setText("Parent folder: (not selected)");
        typeLabel.setText("Type: (not selected)");
        sizeLabel.setText("Size: (not selected)");
        diskSizeLabel.setText("Disk size: (not selected)");
        createdLabel.setText("Created: (not selected)");
        modifiedLabel.setText("Modified: (not selected)");
        ownerLabel.setText("Owner: (not selected)");
        attributesLabel.setText("Attributes: (not selected)");
        readableLabel.setText("Readable: (not selected)");
        writableLabel.setText("Writable: (not selected)");
        executableLabel.setText("Executable: (not selected)");
        hiddenLabel.setText("Hidden: (not selected)");
        absolutePathLabel.setText("Absolute path: (not selected)");
        canonicalPathLabel.setText("Canonical path: (not selected)");
        statsLabel.setText("Statistics: (not selected)");
    }


    private void copyPath() {
        logger.debug("Copying path to clipboard");

        FileNode fn = getSelectedFileNode();
        if (fn == null) return;
        StringSelection sel = new StringSelection(fn.getPath().toAbsolutePath().toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
        statusLabel.setText("Path copied");
        statusLabel.setForeground(FlatUITheme.SUCCESS);
        logger.info("Path copied: " + fn.getPath().toAbsolutePath());
    }

    private void openInExplorer() {
        logger.debug("Opening in file manager");

        FileNode fn = getSelectedFileNode();
        if (fn == null) return;
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String target = fn.getPath().toAbsolutePath().toString();
            if (os.contains("win")) {
                Runtime.getRuntime().exec("explorer /select,\"" + target + "\"");
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", "-R", target});
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", target});
            }
            statusLabel.setText("Folder opened");
            statusLabel.setForeground(FlatUITheme.SUCCESS);
            logger.info("Opened in file manager: " + target);
        } catch (Exception ex) {
            statusLabel.setText("Open error");
            statusLabel.setForeground(FlatUITheme.ERROR);
            logger.error("Error opening file", ex);
        }
    }

    private void deleteSelected() {
        logger.debug("Deleting selected file/folder");

        FileNode fn = getSelectedFileNode();
        if (fn == null) {
            logger.warn("Attempted deletion without selected element");
            JOptionPane.showMessageDialog(this, "Select a file or folder to delete.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        logger.info("Delete confirmation requested: " + fn.getName());
        int res = JOptionPane.showConfirmDialog(this,
                "Delete \"" + fn.getName() + "\"?\nThis action cannot be undone.",
                "Delete Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.YES_OPTION) {
            logger.debug("Delete cancelled by user");
            return;
        }

        try {
            Path p = fn.getPath();
            if (fn.isDirectory()) {
                try (var stream = Files.walk(p)) {
                    stream.sorted((a, b) -> b.compareTo(a))
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (Exception ignored) {}
                            });
                }
            } else {
                Files.delete(p);
            }
            analyze();
            statusLabel.setText("Delete completed");
            statusLabel.setForeground(FlatUITheme.SUCCESS);
            logger.info("Deleted: " + p);
        } catch (Exception ex) {
            statusLabel.setText("Delete error");
            statusLabel.setForeground(FlatUITheme.ERROR);
            logger.error("Error deleting file", ex);
            JOptionPane.showMessageDialog(this,
                    "Delete error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renameFile() {
        logger.debug("Renaming file");

        FileNode fn = getSelectedFileNode();
        if (fn == null) {
            logger.warn("Attempted rename without selected file");
            JOptionPane.showMessageDialog(this, "Select a file to rename.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fn.isDirectory()) {
            logger.warn("Attempted rename on folder instead of file");
            JOptionPane.showMessageDialog(this, "You can only rename files, not folders.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Path p = fn.getPath();
        String currentName = p.getFileName().toString();
        logger.info("Renaming file: " + currentName);

        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

        JLabel nameLabel = new JLabel("File name:");
        JTextField nameField = new JTextField(currentName, 20);

        JLabel extLabel = new JLabel("Extension:");
        JComboBox<String> extCombo = new JComboBox<>(new String[]{
                "txt", "pdf", "doc", "docx", "xls", "xlsx", "jpg", "png", "mp3", "mp4", "zip"
        });

        int dotIndex = currentName.lastIndexOf('.');
        if (dotIndex > 0) {
            String currentExt = currentName.substring(dotIndex + 1);
            extCombo.setSelectedItem(currentExt);
            nameField.setText(currentName.substring(0, dotIndex));
        }

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(extLabel);
        panel.add(extCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Rename File",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            logger.debug("Rename cancelled");
            return;
        }

        String newName = nameField.getText().trim();
        String newExt = (String) extCombo.getSelectedItem();

        if (newName.isEmpty()) {
            logger.warn("Attempted rename with empty name");
            JOptionPane.showMessageDialog(this, "File name cannot be empty.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newFileName = newName + "." + newExt;
        Path newPath = p.getParent().resolve(newFileName);

        try {
            if (Files.exists(newPath)) {
                logger.warn("File already exists: " + newPath);
                JOptionPane.showMessageDialog(this, "File with this name already exists.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Files.move(p, newPath);
            analyze();
            statusLabel.setText("File renamed");
            statusLabel.setForeground(FlatUITheme.SUCCESS);
            logger.info("File renamed: " + p + " -> " + newPath);
        } catch (Exception ex) {
            statusLabel.setText("Rename error");
            statusLabel.setForeground(FlatUITheme.ERROR);
            logger.error("Error renaming file", ex);
            JOptionPane.showMessageDialog(this,
                    "Rename error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewFile() {
        logger.debug("Creating new file");

        FileNode fn = getSelectedFileNode();
        if (fn == null) {
            logger.warn("Attempted file creation without selected folder");
            JOptionPane.showMessageDialog(this,
                    "Select a folder or file (file - will create in parent folder).",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Path baseDir = fn.isDirectory() ? fn.getPath() : fn.getPath().getParent();
        logger.info("Creating file in: " + baseDir);

        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

        JLabel nameLabel = new JLabel("File name:");
        JTextField nameField = new JTextField(20);
        nameField.setText("new_file");

        JLabel extLabel = new JLabel("Extension:");
        JComboBox<String> extCombo = new JComboBox<>(new String[]{
                "txt", "pdf", "doc", "docx", "xls", "xlsx", "jpg", "png", "mp3", "mp4", "zip"
        });
        extCombo.setSelectedItem("txt");

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(extLabel);
        panel.add(extCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create New File",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            logger.debug("File creation cancelled");
            return;
        }

        String fileName = nameField.getText().trim();
        String ext = (String) extCombo.getSelectedItem();

        if (fileName.isEmpty()) {
            logger.warn("Attempted file creation with empty name");
            JOptionPane.showMessageDialog(this, "File name cannot be empty.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fullName = fileName + "." + ext;
        Path newFile = baseDir.resolve(fullName);

        try {
            if (Files.exists(newFile)) {
                logger.warn("File already exists: " + newFile);
                JOptionPane.showMessageDialog(this,
                        "File already exists.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Files.createFile(newFile);
            analyze();
            statusLabel.setText("File created");
            statusLabel.setForeground(FlatUITheme.SUCCESS);
            logger.info("File created: " + newFile);
        } catch (Exception ex) {
            statusLabel.setText("Create error");
            statusLabel.setForeground(FlatUITheme.ERROR);
            logger.error("Error creating file", ex);
            JOptionPane.showMessageDialog(this,
                    "Create error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------- TREE CELL RENDERER ----------------------

    private static class SimpleTreeCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                Object obj = ((DefaultMutableTreeNode) value).getUserObject();
                if (obj instanceof FileNode) {
                    FileNode fn = (FileNode) obj;
                    String prefix = fn.isDirectory() ? "[D] " : "[F] ";
                    setText(prefix + fn.getName());
                }
            }
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setBackgroundNonSelectionColor(FlatUITheme.BG_SECONDARY);
            setBackgroundSelectionColor(new Color(220, 235, 252));
            setTextSelectionColor(FlatUITheme.ACCENT);
            return this;
        }
    }
}
