package ru.fsapp.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents node of file system (file or folder).
 * Contains information about file and its child elements.
 *
 * @author Ahmed
 * @version 1.0
 * @since 2025-12-05
 */
public class FileNode {

    private final File file;
    private final Path path;
    private final List<FileNode> children;
    private long size;
    private String owner;
    private boolean isDirectory;
    private String name;

    /**
     * Creates file node.
     *
     * @param file file represented by this node
     */
    public FileNode(File file) {
        this.file = file;
        this.path = file.toPath();
        this.children = new ArrayList<>();
        this.name = file.getName();
        this.isDirectory = file.isDirectory();
        this.size = file.length();

        // Get file owner
        try {
            this.owner = Files.getOwner(this.path).getName();
        } catch (Exception e) {
            this.owner = "unknown";
        }
    }

    /**
     * Adds child node.
     *
     * @param child child node
     */
    public void addChild(FileNode child) {
        if (child != null) {
            this.children.add(child);
        }
    }

    /**
     * Returns file object.
     *
     * @return File object
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns Path object.
     *
     * @return Path object
     */
    public Path getPath() {
        return path;
    }

    /**
     * Returns list of child nodes.
     *
     * @return list of child nodes
     */
    public List<FileNode> getChildren() {
        return children;
    }

    /**
     * Checks if node is folder.
     *
     * @return true if folder
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Returns file size in bytes.
     *
     * @return size in bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns file name.
     *
     * @return file name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns file owner.
     *
     * @return file owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns string representation of node.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "FileNode{" +
                "name='" + name + '\'' +
                ", path=" + path +
                ", isDirectory=" + isDirectory +
                ", size=" + size +
                ", childrenCount=" + children.size() +
                '}';
    }
}
