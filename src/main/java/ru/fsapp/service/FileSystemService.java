package ru.fsapp.service;

import org.apache.log4j.Logger;
import ru.fsapp.model.FileNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for analyzing and managing file system.
 * Provides methods for building file tree,
 * deleting and creating files with full error handling.
 *
 * @author Ahmed
 * @version 1.0
 * @since 2025-12-05
 */
public class FileSystemService {
    private static final Logger logger = Logger.getLogger(FileSystemService.class);
    private static final int MAX_DEPTH = 100;

    /**
     * Builds file tree from starting path.
     *
     * @param rootPath path to root folder
     * @return root node of tree
     * @throws IllegalArgumentException if path does not exist
     */
    public static FileNode buildTree(String rootPath) {
        logger.info("========== START ANALYSIS ==========");
        logger.info("Analysis path: " + rootPath);

        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            logger.error("Path does not exist: " + rootPath);
            throw new IllegalArgumentException("Path does not exist: " + rootPath);
        }

        if (!rootFile.isDirectory()) {
            logger.error("Is not a folder: " + rootPath);
            throw new IllegalArgumentException("Path is not a folder: " + rootPath);
        }

        logger.debug("Creating root node");
        FileNode rootNode = new FileNode(rootFile);
        buildTreeRecursive(rootNode, 0);

        logger.info("Analysis completed successfully");
        logger.info("========== END ANALYSIS ==========");
        return rootNode;
    }

    /**
     * Recursively builds file tree.
     *
     * @param parentNode parent node
     * @param depth current depth
     */
    private static void buildTreeRecursive(FileNode parentNode, int depth) {
        if (depth > MAX_DEPTH) {
            logger.warn("Maximum depth reached: " + MAX_DEPTH);
            return;
        }

        File parentFile = parentNode.getFile();
        logger.debug("Reading folder: " + parentFile.getAbsolutePath());

        File[] files = parentFile.listFiles();

        if (files == null) {
            logger.warn("No access to: " + parentFile.getAbsolutePath());
            return;
        }

        logger.debug("Found elements: " + files.length);

        for (File file : files) {
            try {
                FileNode childNode = new FileNode(file);
                parentNode.addChild(childNode);

                if (file.isDirectory()) {
                    logger.debug("  [D] " + file.getName());
                    if (file.canRead()) {
                        buildTreeRecursive(childNode, depth + 1);
                    }
                } else {
                    logger.debug("  [F] " + file.getName() + " (" + file.length() + " bytes)");
                }
            } catch (Exception exception) {
                logger.warn("Error processing: " + file.getAbsolutePath(), exception);
            }
        }
    }

    /**
     * Deletes file or folder recursively.
     *
     * @param filePath path to file
     * @return true if successful
     */
    public static boolean deleteFile(String filePath) {
        logger.info("Deleting: " + filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            logger.warn("File does not exist: " + filePath);
            return false;
        }

        return deleteRecursive(file);
    }

    /**
     * Recursively deletes files.
     *
     * @param file file to delete
     * @return true if successful
     */
    private static boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursive(child)) {
                        logger.error("Failed to delete: " + child.getAbsolutePath());
                        return false;
                    }
                }
            }
        }

        boolean deleted = file.delete();
        if (deleted) {
            logger.info("Deleted: " + file.getAbsolutePath());
        } else {
            logger.error("Failed to delete: " + file.getAbsolutePath());
        }
        return deleted;
    }

    /**
     * Creates new file.
     *
     * @param parentPath parent folder
     * @param fileName file name
     * @return true if successful
     */
    public static boolean createFile(String parentPath, String fileName) {
        logger.info("Creating: " + fileName + " in " + parentPath);

        try {
            File file = new File(parentPath, fileName);
            boolean created = file.createNewFile();

            if (created) {
                logger.info("File created: " + file.getAbsolutePath());
            } else {
                logger.warn("File already exists: " + file.getAbsolutePath());
            }

            return created;
        } catch (Exception exception) {
            logger.error("Error creating file", exception);
            return false;
        }
    }

    /**
     * Gets list of files in folder.
     *
     * @param folderPath path to folder
     * @return list of files
     */
    public static List<File> listFiles(String folderPath) {
        logger.debug("Listing: " + folderPath);

        List<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.isDirectory()) {
            logger.warn("Is not a folder: " + folderPath);
            return fileList;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                fileList.add(file);
            }
        }

        return fileList;
    }
}
