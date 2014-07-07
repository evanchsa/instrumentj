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
import instrumentj.ProbeManager;
import instrumentj.ProfilerController;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class ProfilerControllerImpl implements ProfilerController {

    private final AtomicBoolean active = new AtomicBoolean(false);

    private final Instrumentation instrumentation;

    private final ProbeExecutor probeExecutor;

    private ProbeManagerImpl probeManagerImpl;

    /**
     *
     * @param instrumentation
     */
    public ProfilerControllerImpl(final Instrumentation instrumentation) {
        if (instrumentation == null) {
            throw new NullPointerException("instrumentation");
        }

        this.instrumentation = instrumentation;
        this.probeManagerImpl = new ProbeManagerImpl(instrumentation);
        this.probeExecutor = new ProbeExecutorImpl(probeManagerImpl);
    }

    @Override
    public void activate() {
        active.set(true);
    }

    @Override
    public void deactivate() {
        active.set(false);
    }

    @Override
    public long getObjectSize(final Object objectToSize) {
        return instrumentation.getObjectSize(objectToSize);
    }

    public ProbeExecutor getProbeExecutor() {
        return probeExecutor;
    }

    public ProbeManager getProbeManager() {
        return probeManagerImpl;
    }

    /**
     * Initializes the {@link Instrumentation} with the
     * {@link InstrumentingClassFileTransformer} and then {@link #activate()}'s
     * this {@code ProfilerControllerImpl|

     */
    public void initialize() {
        instrumentation.addTransformer(new InstrumentingClassFileTransformer(this), true);

        activate();
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    void setProbeManagerImpl(final ProbeManagerImpl probeManagerImpl) {
        this.probeManagerImpl = probeManagerImpl;
    }
}
