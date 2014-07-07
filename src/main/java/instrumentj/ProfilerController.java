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

import java.lang.instrument.Instrumentation;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface ProfilerController {

    /**
     * Activates this {@code ProfileController}
     * <p>
     * {@code ProfileController}s that are active will perform bytecode
     * instrumentation
     */
    public void activate();

    /**
     * Deactivates this {@code ProfileController}
     * <p>
     * {@code ProfileController}s that are deactivated will not perform any work
     */
    public void deactivate();

    /**
     * Returns an approximation of an object's size.
     *
     * @param objectToSize
     *            the object to examine
     * @return the approximate size of the object
     *
     * @see Instrumentation#getObjectSize(Object)
     */
    public long getObjectSize(final Object objectToSize);

    /**
     * Determines whether or not this {@code ProfileController} is active
     *
     * @return true if active; false otherwise
     */
    public boolean isActive();
}
