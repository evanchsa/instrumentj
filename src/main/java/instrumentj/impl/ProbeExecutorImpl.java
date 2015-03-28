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

import instrumentj.ProbeExecutor;
import instrumentj.probes.MethodEntryProbe;
import instrumentj.probes.MethodExitProbe;
import instrumentj.probes.ObjectAllocationProbe;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ProbeExecutorImpl implements ProbeExecutor {

    private final ProbeManagerImpl probeManagerImpl;

    public ProbeExecutorImpl(final ProbeManagerImpl probeManagerImpl) {
        this.probeManagerImpl = probeManagerImpl;
    }

    @Override
    public void objectAllocationProbes(final String className) {
        final ObjectAllocationProbe objectAllocationProbe =
                probeManagerImpl.getObjectAllocationProbe(className);

        if (objectAllocationProbe == null) {
            return;
        }

        objectAllocationProbe.run(className);
    }

    @Override
    public void methodEntryProbes(final String className, final String methodName, final String methodDescription, final Object[] methodArgs) {
        final MethodEntryProbe methodEntryProbe = probeManagerImpl.getMethodEntryProbe(className, methodName);

        if (methodEntryProbe == null) {
            return;
        }

        methodEntryProbe.run(className, methodName, methodDescription, methodArgs);
    }

    @Override
    public void methodExitProbes(final String className, final String methodName, final String methodDescription, final int opcode) {
        final MethodExitProbe methodExitProbe = probeManagerImpl.getMethodExitProbe(className, methodName);

        if (methodExitProbe == null) {
            return;
        }

        methodExitProbe.run(className, methodName, methodDescription, Integer.valueOf(opcode));
    }
}
