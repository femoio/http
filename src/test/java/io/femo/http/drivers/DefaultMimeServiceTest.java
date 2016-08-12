package io.femo.http.drivers;

import io.femo.http.Http;
import io.femo.http.MimeService;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Created by felix on 6/28/16.
 */
public class DefaultMimeServiceTest {

    @BeforeClass
    public static void setUp() throws Exception {
        File test = new File("test");
        if(!test.exists()) {
            test.mkdir();
        }
        PrintStream printStream = new PrintStream("test/test.html");
        printStream.println("<!DOCTYPE html><html><head><title>Hello</title></head><body>Hello</body></html>");
        printStream.close();
        Http.get("http://www.schaik.com/pngsuite/basn6a08.png").pipe(new FileOutputStream("test/test.png")).execute();
        Http.get("http://www.schaik.com/pngsuite/basn6a08.gif").pipe(new FileOutputStream("test/test.gif")).execute();
        FileUtils.copyFile(new File("test/test.png"), new File("test/test_wrong.gif"));
    }

    @Test
    public void testMimes() throws Exception {
        File html = new File("test/test.html");
        File png = new File("test/test.png");
        File gif = new File("test/test.gif");
        File gifWrong = new File("test/test_wrong.gif");
        MimeService mimeService = new DefaultMimeService();
        assertEquals("text/html", mimeService.contentType(html));
        assertEquals("image/png", mimeService.contentType(png));
        assertEquals("image/png", mimeService.contentType(gifWrong));
        assertEquals("image/gif", mimeService.contentType(gif));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("test"));
    }

}