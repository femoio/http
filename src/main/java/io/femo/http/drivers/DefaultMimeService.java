package io.femo.http.drivers;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.MimeUtil2;
import eu.medsea.mimeutil.TextMimeDetector;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import eu.medsea.mimeutil.detector.MimeDetector;
import io.femo.http.MimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Created by felix on 6/11/16.
 */
public class DefaultMimeService implements MimeService {

    static {
        MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
    }

    @Override
    public String contentType(File file) {
        @SuppressWarnings("unchecked")
        Collection<MimeType> mimeTypes = MimeUtil.getMimeTypes(file);
        if(mimeTypes.size() == 1) {
            return mimeTypes.stream().findFirst().get().toString();
        } else {
            return mimeTypes.stream().sorted((mimeType, t1) -> mimeType.getSpecificity() - t1.getSpecificity())
                    .findFirst().get().toString();
        }
    }
}
