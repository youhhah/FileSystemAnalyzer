package ru.fsapp.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities for formatting file system data.
 *
 * Provides methods to transform file information
 * into readable format (sizes, dates, statistics).
 * Uses Stream API for filtering, searching and collecting files.
 *
 * @author Student
 * @version 1.1
 * @since 2025-12-05
 */
public class FormatUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /**
     * Formats file size in readable view (B, KB, MB, GB, TB).
     *
     * Converts number of bytes to human-readable format with units.
     * Uses decimal logarithmic scale to determine the unit.
     *
     * @param bytes number of bytes to format
     * @return formatted string like "123.45 MB"
     */
    public static String formatSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * Formats date to string by given format.
     *
     * Supports different types of input data:
     * <ul>
     *   <li>FileTime - creation/modification time of file</li>
     *   <li>Long - number of milliseconds since 1970</li>
     * </ul>
     * Output format: dd.MM.yyyy HH:mm:ss
     *
     * @param time time object (FileTime or Long)
     * @return formatted date string or "—" if type is not supported
     *
     * @see java.nio.file.attribute.FileTime
     */
    public static String formatDate(Object time) {
        if (time instanceof java.nio.file.attribute.FileTime) {
            java.nio.file.attribute.FileTime fileTime = (java.nio.file.attribute.FileTime) time;
            return DATE_FORMAT.format(new Date(fileTime.toMillis()));
        } else if (time instanceof Long) {
            return DATE_FORMAT.format(new Date((Long) time));
        }
        return "—";
    }

    /**
     * Counts number of files in directory recursively using Stream API.
     *
     * Walks through entire folder tree and counts only regular files,
     * excluding folders. Uses Stream API for filtering.
     * Ignores access errors.
     *
     * @param dir path to directory for counting
     * @return number of found files, 0 if error or folder is empty
     *
     * @see #countDirectories(Path)
     * @see #calculateTotalSize(Path)
     */
    public static long countFiles(Path dir) {
        try {
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Counts number of directories in directory recursively using Stream API.
     *
     * Walks through entire folder tree and counts only subdirectories,
     * excluding the root folder itself (hence "-1" at the end).
     * Uses Stream API for filtering.
     * Ignores access errors.
     *
     * @param dir path to directory for counting
     * @return number of found subdirectories (minus the folder itself), 0 if error
     *
     * @see #countFiles(Path)
     * @see #calculateTotalSize(Path)
     */
    public static long countDirectories(Path dir) {
        try {
            return Files.walk(dir)
                    .filter(Files::isDirectory)
                    .count() - 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculates total size of all files in directory recursively using Stream API.
     *
     * Sums sizes of all files in all subdirectories using mapToLong and sum operations.
     * Files that cannot be accessed are ignored (do not cause error).
     * Folder sizes are not counted.
     *
     * @param dir path to directory for counting
     * @return total size of all files in bytes, 0 if error
     *
     * @see #formatSize(long) for converting to readable format
     * @see #countFiles(Path)
     */
    public static long calculateTotalSize(Path dir) {
        try {
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .mapToLong(path -> {
                        try {
                            return Files.size(path);
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .sum();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Filters files by extension using Stream API.
     *
     * Walks through directory tree and collects all files with specified extension.
     * Extension comparison is case-insensitive.
     * Uses Stream API with filter and collect operations.
     *
     * @param dir path to directory for search
     * @param extension file extension without dot (e.g., "txt", "java", "log")
     * @return list of File objects matching the extension, empty list if none found
     *
     * @see #countFilesByExtension(Path, String)
     * @see #filterFilesBySize(Path, long)
     */
    public static List<File> filterByExtension(Path dir, String extension) {
        try {
            String ext = "." + extension.toLowerCase();
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(ext))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Counts files with specific extension using Stream API.
     *
     * Walks through directory tree and counts files matching extension.
     * Uses Stream API with filter and count operations.
     *
     * @param dir path to directory for search
     * @param extension file extension without dot (e.g., "txt", "java")
     * @return number of files with specified extension, 0 if error or none found
     *
     * @see #filterByExtension(Path, String)
     * @see #filterFilesBySize(Path, long)
     */
    public static long countFilesByExtension(Path dir, String extension) {
        try {
            String ext = "." + extension.toLowerCase();
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(ext))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Filters files by minimum size using Stream API.
     *
     * Walks through directory tree and collects files larger than specified size.
     * Uses Stream API with filter and collect operations.
     *
     * @param dir path to directory for search
     * @param minSize minimum file size in bytes
     * @return list of File objects larger than minSize, empty list if none found
     *
     * @see #countFilesByExtension(Path, String)
     * @see #filterByExtension(Path, String)
     */
    public static List<File> filterFilesBySize(Path dir, long minSize) {
        try {
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        try {
                            return Files.size(p) >= minSize;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Gets all files sorted by size using Stream API.
     *
     * Walks through directory tree and returns files sorted by size in descending order.
     * Uses Stream API with sorted operation.
     *
     * @param dir path to directory for search
     * @return sorted list of File objects by size (largest first), empty list if error
     *
     * @see #filterFilesBySize(Path, long)
     * @see #filterByExtension(Path, String)
     */
    public static List<File> getAllFilesSortedBySize(Path dir) {
        try {
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .sorted((p1, p2) -> {
                        try {
                            return Long.compare(Files.size(p2), Files.size(p1));
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Calculates total size of files with specific extension using Stream API.
     *
     * Walks through directory tree and sums sizes of files matching extension.
     * Uses Stream API with filter and mapToLong operations.
     *
     * @param dir path to directory for search
     * @param extension file extension without dot (e.g., "txt", "java")
     * @return total size of files with specified extension in bytes, 0 if error
     *
     * @see #countFilesByExtension(Path, String)
     * @see #filterByExtension(Path, String)
     */
    public static long calculateSizeByExtension(Path dir, String extension) {
        try {
            String ext = "." + extension.toLowerCase();
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(ext))
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .sum();
        } catch (Exception e) {
            return 0;
        }
    }
}
