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
package instrumentj.probes.impl;

import instrumentj.probes.MethodProbe;
import instrumentj.probes.Probe;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class BaseMethodProbe implements MethodProbe {

    private final String classNameFilter;

    private final String methodDescriptionFilter;

    private final String methodNameFilter;

    public BaseMethodProbe() {
        this(Probe.WILDCARD, Probe.WILDCARD, Probe.WILDCARD);
    }

    public BaseMethodProbe(final String classNameFilter, final String methodNameFilter, final String methodDescriptionFilter) {
        this.classNameFilter = classNameFilter;
        this.methodNameFilter = methodNameFilter;
        this.methodDescriptionFilter = methodDescriptionFilter;
    }

    @Override
    public String getMethodNameFilter() {
        return methodNameFilter;
    }

    @Override
    public String getMethodDescriptionFilter() {
        return methodDescriptionFilter;
    }

    @Override
    public String getClassNameFilter() {
        return classNameFilter;
    }

    @Override
    public void run(Object... args) {
    }
}
