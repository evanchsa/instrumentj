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
package instrumentj;

import instrumentj.impl.InstrumentMethodVisitor;
import instrumentj.impl.ProfilerControllerImpl;
import instrumentj.probes.Probe;


/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public enum StaticProfilerInterface {

    INSTANCE;

    /**
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
    public interface Profiler extends ProbeManager, ProfilerController {
    };

    private static boolean initialized = false;

    /**
     * Invoked by the profile whenever a method entry is detected. This includes
     * constructors (&lt;init&gt; methods)
     *
     * @param className
     *            the name of the class
     * @param methodName
     *            the name of the method
     * @param methodDescription
     *            the argument descriptors for the method
     */
    public static void methodEnterProbes(final String className, final String methodName, final String methodDescription) {
        if (!INSTANCE.profilerController.isActive()) {
            return;
        }

        INSTANCE.probeExecutor.methodEntryProbes(className, methodName, methodDescription);
    }

    /**
     * Invoked by the profile whenever a method exit is detected. This includes
     * constructors (&lt;init&gt; methods) and exceptions
     *
     * @param className
     *            the name of the class
     * @param methodName
     *            the name of the method
     * @param methodDescription
     *            the argument descriptors for the method
     * @param opcode
     *            consult {@link InstrumentMethodVisitor#MethodExit(int)} for
     *            more information about this value
     */
    public static void methodExitProbes(final String className, final String methodName, final String methodDescription, final int opcode) {
        if (!INSTANCE.profilerController.isActive()) {
            return;
        }

        INSTANCE.probeExecutor.methodExitProbes(className, methodName, methodDescription, opcode);
    }

    public static void objectAllocationProbes(final String className) {
        if (!INSTANCE.profilerController.isActive()) {
            return;
        }

        INSTANCE.probeExecutor.objectAllocationProbes(className);
    };

    /**
     * Initializes the fields of this enum singleton
     *
     * @param profilerController
     */
    static void initialize(final ProfilerControllerImpl profilerController) {
        if (!initialized) {
            INSTANCE.profilerController = profilerController;
            INSTANCE.probeManager = profilerController.getProbeManager();
            INSTANCE.probeExecutor = profilerController.getProbeExecutor();

            initialized = true;
        }
    }

    private ProbeExecutor probeExecutor;

    /*
     * Static methods called by the instrumented code
     */

    private ProbeManager probeManager;

    private ProfilerController profilerController;

    /**
     * Returns a new instance of the {@link Profiler} interface
     *
     * @return a new instance of the {@link Profiler} interface
     */
    public Profiler getProfiler() {
        return new Profiler() {

            @Override
            public void activate() {
                profilerController.activate();
            }

            @Override
            public void addProbe(final Probe probe) {
                probeManager.addProbe(probe);
            }

            @Override
            public void deactivate() {
                profilerController.deactivate();
            }

            @Override
            public boolean defineProbes(final Class<?> clazz) {
                return probeManager.defineProbes(clazz);
            }

            @Override
            public long getObjectSize(final Object objectToSize) {
                return profilerController.getObjectSize(objectToSize);
            }

            @Override
            public boolean isActive() {
                return profilerController.isActive();
            }

            @Override
            public boolean removeProbe(final Probe probe) {
                return probeManager.removeProbe(probe);
            }
        };
    }
}
