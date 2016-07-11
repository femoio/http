package io.femo.http.drivers;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import eu.medsea.mimeutil.detector.MimeDetector;
import io.femo.http.MimeService;
import org.jetbrains.annotations.Contract;
import java.io.File;

/**
 * Created by felix on 6/11/16.
 */
public class DefaultMimeService implements MimeService {

    private ThreadLocal<MimeDetector> tlMagicMimeDetector = new ThreadLocal<MimeDetector>() {
        @Contract(" -> !null")
        @Override
        protected MimeDetector initialValue() {
            return new MagicMimeMimeDetector();
        }
    };

    private ThreadLocal<MimeDetector> tlExtensionMimeDetector = new ThreadLocal<MimeDetector>() {
        @Contract(" -> !null")
        @Override
        protected MimeDetector initialValue() {
            return new ExtensionMimeDetector();
        }
    };

    @Override
    public String contentType(File file) {
        MimeDetector magic = tlMagicMimeDetector.get();
        MimeDetector extension = tlExtensionMimeDetector.get();
        MimeType magicMimeType = MimeUtil.getMostSpecificMimeType(magic.getMimeTypes(file));
        MimeType extensionMimeType = MimeUtil.getMostSpecificMimeType(extension.getMimeTypes(file));
        if(magicMimeType == null || magicMimeType.toString().equals("application/octet-stream")) {
            if(extensionMimeType == null) {
                return "application/octet-stream";
            }
            return extensionMimeType.toString();
        } else {
            return magicMimeType.toString();
        }
    }
}
