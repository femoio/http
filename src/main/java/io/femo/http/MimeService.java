package io.femo.http;

import java.io.File;

/**
 * Created by felix on 6/11/16.
 */
public interface MimeService extends Driver {

    String contentType(File file);
}
