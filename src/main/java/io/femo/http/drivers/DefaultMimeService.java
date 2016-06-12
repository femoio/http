package io.femo.http.drivers;

import io.femo.http.MimeService;

import java.io.File;

/**
 * Created by felix on 6/11/16.
 */
public class DefaultMimeService implements MimeService {
    @Override
    public String contentType(File file) {
        String fileEnding = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
        if(fileEnding.endsWith("html") || fileEnding.endsWith(".htm")) {
            return "text/html";
        } else if (fileEnding.endsWith("css")) {
            return "text/css";
        } else if (fileEnding.endsWith("js")) {
            return "text/javascript";
        } else if (fileEnding.endsWith("txt")) {
            return "text/plain";
        }
        return "";
    }
}
