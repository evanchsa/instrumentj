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


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class InstrumentClassVisitor extends ClassVisitor {

    private final String className;

    /**
     *
     * @param cv
     * @param className
     */
    public InstrumentClassVisitor(final ClassVisitor cv, final String className) {
        super(Opcodes.ASM4, cv);

        if (className == null) {
            throw new NullPointerException("className");
        }

        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String desc,
            final String signature,
            final String[] exceptions)
    {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new InstrumentMethodVisitor(className, mv, access, name, desc);
    }

    @Override
    public String toString() {
        return super.toString() + " for " + className;
    }
}
