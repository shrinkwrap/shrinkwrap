/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.base.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamConstants;
import java.io.ObjectStreamField;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.serialization.SerializableView;
import org.jboss.shrinkwrap.api.serialization.ZipSerializableView;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Ensures that serialization of Archives is possible via the {@link SerializableView}s.
 * <p>
 * SHRINKWRAP-178
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class SerializationTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members -----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(SerializationTestCase.class.getName());

    /**
     * Name of the payload archive used in testing serialization
     */
    private static final String NAME_PAYLOAD_ARCHIVE = "serializedArchive.jar";

    // -------------------------------------------------------------------------------------||
    // Instance Members --------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * A populated archive to be used in testing serialization
     */
    private JavaArchive payload;

    // -------------------------------------------------------------------------------------||
    // Lifecycle ---------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a payload archive to be used in serialization tests
     */
    @BeforeEach
    public void createPayload() {
        payload = ShrinkWrap.create(JavaArchive.class, NAME_PAYLOAD_ARCHIVE).addClasses(SerializationTestCase.class,
            JavaArchive.class);
    }

    // -------------------------------------------------------------------------------------||
    // Tests -------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures we may serialize an {@link Archive} as {@link ZipSerializableView} and preserve contents as expected
     */
    @Test
    public void zipSerializableView() throws Exception {
        this.testSerializableView(ZipSerializableView.class);
    }

    /**
     * Ensures we may serialize an {@link Archive} as {@link SerializableView} and preserve contents as expected
     */
    @Test
    public void serializableView() throws Exception {
        this.testSerializableView(SerializableView.class);
    }

    /**
     * Tests that the payload archive may be serialized as the specified {@link SerializableView} type and contents of
     * the roundtrip are as expected.
     *
     * @param <S>
     *           The type of {@link SerializableView}
     * @param serializableView
     *           The class of the serializable view to test
     * @throws Exception
     *           If an error occurs during serialization or deserialization
     */
    private <S extends SerializableView> void testSerializableView(final Class<S> serializableView) throws Exception {
        // Define the initial archive
        log.info("Before: " + payload.toString(true));

        // Serialize
        final JavaArchive roundtrip = serializeAndDeserialize(payload.as(serializableView)).as(JavaArchive.class);
        log.info("After: " + roundtrip.toString(true));

        // Ensure contents are as expected
        this.testCurrentFields(payload, roundtrip);
    }

    /**
     * Ensures that the current serialization protocol is compatible with the version initially released. We accomplish
     * this by mocking {@link ZipSerializableOriginalImpl} and redefining its class name via
     * {@link SerializationTestCase#serializeAndDeserialize(SerializableView, Class)}, which uses the
     * {@link SpoofingObjectOutputStream}.
     *
     * @throws Exception
     *             If an error occurs during the test
     */
    @Test
    public void zipWireProtocolCurrentToOriginal() throws Exception {
        final SerializableView currentWireFormat = this.payload.as(SerializableView.class);
        final SerializableView roundtrip = this.testWireProtocol(currentWireFormat, ZipSerializableOriginalImpl.class);
        this.testOriginalFields(payload, roundtrip.as(JavaArchive.class));
    }

    /**
     * Ensures that the original serialization protocol is compatible with the current version. We accomplish this by
     * mocking {@link ZipSerializableOriginalImpl} and redefining its class name via
     * {@link SerializationTestCase#serializeAndDeserialize(SerializableView, Class)}, which uses the
     * {@link SpoofingObjectOutputStream}.
     *
     * @throws Exception
     *             If an error occurs during the test
     */
    @Test
    public void zipWireProtocolOriginalToCurrent() throws Exception {
        final SerializableView originalWireFormat = new ZipSerializableOriginalImpl(payload);
        final SerializableView roundtrip = this.testWireProtocol(originalWireFormat, ZipSerializableViewImpl.class);
        this.testOriginalFields(payload, roundtrip.as(JavaArchive.class));
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods -------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that the specified client object may be serialized to the specified type
     *
     * @param clientObject
     *            The object to be serialized
     * @param targetType
     *            The type we should be represented as
     * @return The roundtrip view
     * @throws IOException
     *            If an I/O error occurs during serialization or deserialization
     */
    private SerializableView testWireProtocol(final SerializableView clientObject,
        final Class<? extends SerializableView> targetType) throws IOException {
        // Roundtrip the object, now representing as the target type
        final SerializableView roundtrip = serializeAndDeserialize(clientObject, targetType);

        // The type of the object put through roundtrip serialization must be of the type specified
        Assertions.assertEquals(targetType, roundtrip.getClass());

        // Return
        return roundtrip;
    }

    private void testOriginalFields(final Archive<?> payload, final Archive<?> roundtrip) {
        final Map<ArchivePath, Node> originalContents = payload.getContent();
        final Map<ArchivePath, Node> roundtripContents = roundtrip.getContent();
        Assertions.assertEquals(originalContents, roundtripContents, "Contents after serialization were not as expected");
        Assertions.assertEquals(NAME_PAYLOAD_ARCHIVE, payload.getName(), "Name of original archive was not as expected");
        Assertions.assertEquals(payload.getName(), roundtrip.getName(), "Name not as expected after serialization");
    }

    private void testCurrentFields(final Archive<?> payload, final Archive<?> roundtrip) {
        this.testOriginalFields(payload, roundtrip);
        Assertions.assertEquals(payload.getId(), roundtrip.getId(), "ID not as expected after serialization");
    }

    /**
     * Roundtrip serializes/deserializes the specified {@link Archive}
     *
     * @param archive
     *            The original {@link SerializableView} instance
     * @return The reconstituted {@link SerializableView} instance after serialization and deserialization
     * @throws IOException
     *             If an I/O error occurs during serialization or deserialization
     * @throws ClassNotFoundException
     *             If the class of a serialized object cannot be found during deserialization
     */
    private static SerializableView serializeAndDeserialize(final SerializableView archive) throws IOException, ClassNotFoundException {
        assert archive != null : "Archive must be specified";
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(archive);
        out.flush();
        out.close();
        final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        final SerializableView roundtrip = (SerializableView) in.readObject();
        in.close();
        return roundtrip;
    }

    /**
     * Roundtrip serializes/deserializes the specified {@link SerializableView} and reconsitutes/redefines as the
     * specified target type
     *
     * @param archive
     *            The original {@link SerializableView} instance
     * @param targetType
     *            new type we should cast to after deserialization
     * @see <a href="http://crazybob.org/2006/01/unit-testing-serialization-evolution.html">Unit Testing Serialization Evolution</a>
     * @see <a href="http://crazybob.org/2006/01/unit-testing-serialization-evolution_13.html">Unit Testing Serialization Evolution Part 2</a>
     * @see <a href="http://www.theserverside.com/news/thread.tss?thread_id=38398">TheServerSide Discussion</a>
     * @author Bob Lee
     */
    private static <S extends SerializableView> S serializeAndDeserialize(final SerializableView archive,
        final Class<S> targetType) throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream oout = new SpoofingObjectOutputStream(bout, archive.getClass(), targetType);
        oout.writeObject(archive);
        oout.flush();
        oout.close();
        final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        final ObjectInputStream oin = new ObjectInputStream(bin);
        try {
            final Object obj = oin.readObject();
            oin.close();
            log.info("Original type " + archive.getClass().getName() + " now represented as "
                + obj.getClass().getName());
            return targetType.cast(obj);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Classes -------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * SpoofingObjectOutputStream
     * <p>
     * ObjectOutputStream which will replace a class name with one explicitly given
     *
     * @see <a href="http://crazybob.org/2006/01/unit-testing-serialization-evolution_13.html">Unit Testing Serialization Evolution</a>
     * @author Bob Lee
     * @version $Revision: $
     */
    static class SpoofingObjectOutputStream extends ObjectOutputStream {

        String oldName;

        String newName;

        public SpoofingObjectOutputStream(OutputStream out, Class<?> oldClass, Class<?> newClass) throws IOException {
            super(out);
            this.oldName = oldClass.getName();
            this.newName = newClass.getName();
        }

        @Override
        protected void writeClassDescriptor(ObjectStreamClass descriptor) throws IOException {
            Class<?> clazz = descriptor.forClass();

            boolean externalizable = Externalizable.class.isAssignableFrom(clazz);
            boolean serializable = Serializable.class.isAssignableFrom(clazz);
            boolean hasWriteObjectData = hasWriteObjectMethod(clazz);
            boolean isEnum = Enum.class.isAssignableFrom(clazz);

            writeUTF(replace(descriptor.getName()));
            writeLong(descriptor.getSerialVersionUID());
            byte flags = 0;
            if (externalizable) {
                flags |= ObjectStreamConstants.SC_EXTERNALIZABLE;
                flags |= ObjectStreamConstants.SC_BLOCK_DATA;
            } else if (serializable) {
                flags |= ObjectStreamConstants.SC_SERIALIZABLE;
            }
            if (hasWriteObjectData) {
                flags |= ObjectStreamConstants.SC_WRITE_METHOD;
            }
            if (isEnum) {
                flags |= ObjectStreamConstants.SC_ENUM;
            }
            writeByte(flags);

            ObjectStreamField[] fields = descriptor.getFields();
            writeShort(fields.length);
            for (ObjectStreamField field : fields) {
                writeByte(field.getTypeCode());
                writeUTF(field.getName());
                if (!field.isPrimitive()) {
                    writeObject(replace(field.getTypeString()));
                }
            }
        }

        String replace(String className) {
            if (className.equals(newName)) {
                throw new RuntimeException("Found instance of " + className + "." + " Expected instance of " + oldName
                    + ".");
            }
            return className == oldName ? newName : className;
        }

        boolean hasWriteObjectMethod(Class<?> clazz) {
            try {
                Method method = clazz.getDeclaredMethod("writeObject", ObjectOutputStream.class);
                int modifiers = method.getModifiers();
                return method.getReturnType() == Void.TYPE && !Modifier.isStatic(modifiers)
                    && Modifier.isPrivate(modifiers);
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
    }
}
