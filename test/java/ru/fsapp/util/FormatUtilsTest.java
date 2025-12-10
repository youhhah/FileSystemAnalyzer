package ru.fsapp.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class FormatUtilsTest {

    @Test
    public void testFormatSize() {
        assertEquals("0 B", FormatUtils.formatSize(0));
        assertEquals("1.00 KB", FormatUtils.formatSize(1024));
    }

    @Test
    public void testFormatDateNotNull() {
        String result = FormatUtils.formatDate(System.currentTimeMillis());
        assertNotNull(result);
    }
}
