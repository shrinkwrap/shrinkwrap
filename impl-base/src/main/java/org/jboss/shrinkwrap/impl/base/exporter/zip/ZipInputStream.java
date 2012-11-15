package org.jboss.shrinkwrap.impl.base.exporter.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;

/**
 * TODO
 * 
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class ZipInputStream extends InputStream {

    /**
     * Number of bytes kept in buffer.
     */
    private static final int BUFFER_LENGTH = 4096;

    /**
     * Iterator over nodes contained in base archive.
     */
    private final Iterator<Node> nodesIterator;

    /**
     * Creates stream directly from archive.
     * 
     * @param archive
     */
    public ZipInputStream(final Archive<?> archive) {
        final Collection<Node> nodes = archive.getContent().values();
        this.nodesIterator = nodes.iterator();
    }

    private final ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
    private ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

    private InputStream currentNodeStream;
    private ByteArrayInputStream bufferInputStream;

    private boolean zipOutputStreamClosed = false;

    private ArchivePath currentPath = null;

    @Override
    public int read() throws IOException {

        int value = bufferInputStream != null ? bufferInputStream.read() : -1;
        if (value == -1) {
            if (currentNodeStream != null) {
                try {
                    doCopy();
                    bufferInputStream = new ByteArrayInputStream(bufferedOutputStream.toByteArray());
                    bufferedOutputStream.reset();
                    return this.read();
                } catch (final Throwable t) {
                    throw new ArchiveExportException("Failed to write asset to output: " + currentPath.get(), t);
                }
            }

            if (currentNodeStream == null && nodesIterator.hasNext()) {
                final Node currentNode = nodesIterator.next();

                currentPath = currentNode.getPath();
                final String pathName = PathUtil.optionallyRemovePrecedingSlash(currentPath.get());

                final boolean isDirectory = currentNode.getAsset() == null;
                String resolvedPath = pathName;

                if (isDirectory) {
                    resolvedPath = PathUtil.optionallyAppendSlash(resolvedPath);
                    startAsset(resolvedPath);
                    endAsset();
                } else {
                    startAsset(resolvedPath);

                    try {
                        currentNodeStream = currentNode.getAsset().openStream();
                        doCopy();
                    } catch (final Throwable t) {
                        throw new ArchiveExportException("Failed to write asset to output: " + currentPath.get(), t);
                    }
                    bufferInputStream = new ByteArrayInputStream(bufferedOutputStream.toByteArray());
                    bufferedOutputStream.reset();
                }

                // pathsExported.add(path);

            } else {
                if (!zipOutputStreamClosed) {
                    zipOutputStream.close();
                    zipOutputStreamClosed = true;
                    bufferInputStream = new ByteArrayInputStream(bufferedOutputStream.toByteArray());

                    bufferedOutputStream.close();
                    currentNodeStream = null;
                    zipOutputStream = null;
                    return this.read();
                }
                return -1;
            }
            return this.read();
        }
        return value;
    }

    private void doCopy() throws IOException {
        // IOUtil.copy(currentNodeStream, zipOutputStream);
        int copied = IOUtil.copy(currentNodeStream, zipOutputStream, BUFFER_LENGTH);
        if (copied < BUFFER_LENGTH || copied == -1) {
            currentNodeStream.close();
            currentNodeStream = null;
            endAsset();
        }
    }

    private void startAsset(final String path) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(path));
    }

    private void endAsset() throws IOException {
        zipOutputStream.closeEntry();
    }
}
