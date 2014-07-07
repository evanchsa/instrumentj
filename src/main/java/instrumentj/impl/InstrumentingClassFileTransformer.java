/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package instrumentj.impl;

import instrumentj.ClassLoaderFilter;
import instrumentj.ProfilerController;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class InstrumentingClassFileTransformer implements ClassFileTransformer {

    private static final String[] blackListedPackages = new String[] {
        "com/sun/",
        "groovy",
        "instrumentj",
        "java",
        "jline/",
        "org/codehaus/groovy/",
        "org/fusesource/",
        "org/h2/",
        "org/objectweb/",
        "sun/",
        "$"
    };

    private static final Logger logger = Logger.getLogger(InstrumentingClassFileTransformer.class.getName());

    private static final ClassLoaderFilter SYSTEM_CLASS_LOADER_FILTER = new SystemClassLoaderFilter();

    private final ProfilerController profilerController;

    /**
     *
     * @param profilerController
     */
    public InstrumentingClassFileTransformer(final ProfilerController profilerController) {
        this.profilerController = profilerController;
    }

    @Override
    public byte[] transform(
            final ClassLoader classLoader,
            final String className,
            final Class<?> classBeingRedefined,
            final ProtectionDomain protectionDomain,
            final byte[] classFileBuffer)
        throws IllegalClassFormatException
    {
        if (!profilerController.isActive()) {
            logger.log(Level.FINEST, "Transformer inactive");
            return classFileBuffer;
        }

        logger.log(Level.FINEST, "Transformer active");

        if (!SYSTEM_CLASS_LOADER_FILTER.accept(classLoader)) {
            logger.log(Level.FINEST, "ClassLoader not filtered: " + classLoader);

            return classFileBuffer;
        }

        for (final String blackListEntry : blackListedPackages) {
            if (className.startsWith(blackListEntry)) {
                logger.log(Level.FINEST, "Blacklisted package: " + className);

                return classFileBuffer;
            }
        }

        final ClassReader classReader = new ClassReader(classFileBuffer);

        final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        final InstrumentClassVisitor probeClassVisitor = new InstrumentClassVisitor(classWriter, className);

        classReader.accept(probeClassVisitor, ClassReader.SKIP_DEBUG);

        return classWriter.toByteArray();
    }
}
