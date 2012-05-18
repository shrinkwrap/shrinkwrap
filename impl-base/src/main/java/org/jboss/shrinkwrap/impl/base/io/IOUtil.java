/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.impl.base.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.asset.IOUtilDelegator;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * Generic input/output utilities
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public final class IOUtil {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(IOUtil.class.getName());

    /**
     * Default Error Handler
     */
    private static final StreamErrorHandler DEFAULT_ERROR_HANDLER = new StreamErrorHandler() {

        @Override
        public void handle(Throwable t) {
            throw new RuntimeException(t);
        }

    };

    /**
     * Name of UTF-8 Charset
     */
    private static final String CHARSET_UTF8 = "UTF-8";

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Internal constructor; should not be called
     */
    private IOUtil() {
        throw new UnsupportedOperationException("No instances should be created; stateless class");
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains the contents of the specified stream as a byte array
     *
     * @param in
     * @throws IllegalArgumentException
     *             If the stream was not specified
     * @return the byte[] for the given InputStream
     */
    public static byte[] asByteArray(final InputStream in) throws IllegalArgumentException {
        return IOUtilDelegator.asByteArray(in);
    }

    /**
     * Obtains the contents of the specified stream as a String in UTF-8 charset.
     *
     * @param in
     * @throws IllegalArgumentException
     *             If the stream was not specified
     */
    public static String asUTF8String(InputStream in) {
        // Precondition check
        Validate.notNull(in, "Stream must be specified");

        StringBuilder buffer = new StringBuilder();
        String line;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, CHARSET_UTF8));
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append(Character.LINE_SEPARATOR);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error in obtaining string from " + in, ioe);
        } finally {
            try {
                in.close();
            } catch (IOException ignore) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Could not close stream due to: " + ignore.getMessage() + "; ignoring");
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Copies the contents from an InputStream to an OutputStream. It is the responsibility of the caller to close the
     * streams passed in when done, though the {@link OutputStream} will be fully flushed.
     *
     * @param input
     * @param output
     * @throws IOException
     *             If a problem occurred during any I/O operations
     */
    public static void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }

        output.flush();
    }

    /**
     * Writing the specified contents to the specified OutputStream using an internal buffer. Flushing the stream when
     * completed. Caller is responsible for opening and closing the specified stream.
     *
     * @param output
     *            The OutputStream
     * @param content
     *            The content to write to the specified stream
     * @throws IOException
     *             If a problem occured during any I/O operations
     */
    public static void bufferedWriteWithFlush(final OutputStream output, final byte[] content) throws IOException {
        final int size = 4096;
        int offset = 0;
        while (content.length - offset > size) {
            output.write(content, offset, size);
            offset += size;
        }
        output.write(content, offset, content.length - offset);
        output.flush();
    }

    /**
     * Copies the contents from an InputStream to an OutputStream and closes both streams.
     *
     * @param input
     * @param output
     * @throws IOException
     *             If a problem occurred during any I/O operations during the copy, but on closing the streams these
     *             will be ignored and logged at {@link Level#FINER}
     */
    public static void copyWithClose(InputStream input, OutputStream output) throws IOException {
        try {
            copy(input, output);
        } finally {
            try {
                input.close();
            } catch (final IOException ignore) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Could not close stream due to: " + ignore.getMessage() + "; ignoring");
                }
            }
            try {
                output.close();
            } catch (final IOException ignore) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Could not close stream due to: " + ignore.getMessage() + "; ignoring");
                }
            }
        }
    }

    /**
     * Helper method to run a specified task and automatically handle the closing of the stream.
     *
     * @param stream
     * @param task
     */
    public static <S extends Closeable> void closeOnComplete(S stream, StreamTask<S> task) {
        closeOnComplete(stream, task, DEFAULT_ERROR_HANDLER);
    }

    /**
     * Helper method to run a specified task and automatically handle the closing of the stream.
     *
     * @param <S>
     * @param task
     * @param errorHandler
     */
    public static <S extends Closeable> void closeOnComplete(S stream, StreamTask<S> task,
        StreamErrorHandler errorHandler) {
        try {
            task.execute(stream);
        } catch (Throwable t) {
            errorHandler.handle(t);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (final IOException ignore) {
                    if (log.isLoggable(Level.FINER)) {
                        log.finer("Could not close stream due to: " + ignore.getMessage() + "; ignoring");
                    }
                }
            }
        }
    }
}
