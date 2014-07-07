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

import instrumentj.Constants;
import instrumentj.StaticProfilerInterface;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class InstrumentMethodVisitor extends AdviceAdapter implements Constants {

    private final String className;

    private final String methodName;

    private final String methodDescription;

    /**
     *
     * @param className
     * @param mv
     * @param access
     * @param methodName
     * @param methodDescription
     */
    public InstrumentMethodVisitor(
            final String className,
            final MethodVisitor mv,
            final int access,
            final String methodName,
            final String methodDescription)
    {
        super(Opcodes.ASM4, mv, access, methodName, methodDescription);

        if (className == null) {
            throw new NullPointerException("className");
        }

        if (methodName == null) {
            throw new NullPointerException("methodName");
        }

        this.className = className;
        this.methodName = methodName;
        this.methodDescription = methodDescription;
    }

    /**
     * Adds a call to
     * {@link StaticProfilerInterface#objectAllocationProbes(String)} in the
     * case of constructor calls and also a call to
     * {@link StaticProfilerInterface#methodEnterProbes(String, String)}
     * <br>
     * <br>
     * {@inheritDoc}
     */
    @Override
    protected void onMethodEnter() {

        if ("<init>".equals(methodName)) {
            visitLdcInsn(className);

            visitMethodInsn(
                    INVOKESTATIC,
                    INSTRUMENTJ_STATIC_PROFILER_INTERFACE,
                    OBJECT_ALLOCATION_PROBES,
                    OBJECT_ALLOCATION_PROBES_DESC);
        }

        visitLdcInsn(className);
        visitLdcInsn(methodName);
        visitLdcInsn(methodDescription);

        visitMethodInsn(
            INVOKESTATIC,
            INSTRUMENTJ_STATIC_PROFILER_INTERFACE,
            METHOD_ENTER_PROBES,
            METHOD_ENTER_PROBES_DESC);
    }

    /**
     * Adds a call to
     * {@link StaticProfilerInterface#methodExitProbes(String, String, int)} <br>
     * <br>
     * {@inheritDoc}
     */
    @Override
    protected void onMethodExit(final int opcode) {
        // TODO: If we're exiting the constructor because of an exception then should that be counted differently?

        visitLdcInsn(className);
        visitLdcInsn(methodName);
        visitLdcInsn(methodDescription);

        visitLdcInsn(opcode);

        visitMethodInsn(
                INVOKESTATIC,
                INSTRUMENTJ_STATIC_PROFILER_INTERFACE,
                METHOD_EXIT_PROBES,
                METHOD_EXIT_PROBES_DESC);
    }

    @Override
    public String toString() {
        return super.toString() + " for " + className + "." + methodName + "(" + methodDescription + ")";
    }
}
