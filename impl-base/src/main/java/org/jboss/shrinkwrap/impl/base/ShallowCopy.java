package org.jboss.shrinkwrap.impl.base;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;

public class ShallowCopy {

    /**
     * Copies pointers to assets from an archive to another archive. Paths are recreated in the target archive.
     *
     * @param from origin archive
     * @param to target archive
     */
    public static <T extends Archive<T>> void shallowCopyContentTo(Archive<T> from, Archive<T> to) {
        for (ArchivePath path : from.getContent().keySet()) {
            to.add(from.get(path).getAsset(), path);
        }
    }

}
