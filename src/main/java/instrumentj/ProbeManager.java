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

import instrumentj.probes.Probe;


/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface ProbeManager {

    /**
     * Adds the given {@link Probe}
     *
     * @param probe
     *            the {@code Probe} to add
     */
    public void addProbe(Probe probe);

    /**
     * Redefines the {@link Class} so that it has the required instrumentation
     *
     * @param clazz
     *            the {@code Class} to redefine
     * @return true if successfully instrumented; false otherwise
     */
    public boolean defineProbes(Class<?> clazz);

    /**
     * Removes the given {@link Probe}
     *
     * @param probe
     *            the {@code Probe} to remove
     * @return true if removed; false otherwise
     */
    public boolean removeProbe(Probe probe);
}
