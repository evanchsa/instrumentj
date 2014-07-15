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

import instrumentj.ProbeManager;
import instrumentj.probes.MethodEntryProbe;
import instrumentj.probes.MethodExitProbe;
import instrumentj.probes.MethodProbe;
import instrumentj.probes.MonitorProbe;
import instrumentj.probes.ObjectAllocationProbe;
import instrumentj.probes.Probe;

import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
class ProbeManagerImpl implements ProbeManager {

    private final class ProbeHolder<T extends Probe> {

        private final Queue<T> classWildCards = new ConcurrentLinkedQueue<T>();

        private final Queue<T> methodWildCards = new ConcurrentLinkedQueue<T>();

        private final ConcurrentMap<String, List<T>> probes = new ConcurrentHashMap<String, List<T>>();

        /**
         *
         * @param classNameFilter
         * @param methodNameFilter
         * @param probe
         */
        public void add(final String classNameFilter, final String methodNameFilter, final T probe) {

            final boolean classWildCard = Probe.WILDCARD.equals(classNameFilter);
            final boolean methodWildCard = Probe.WILDCARD.equals(methodNameFilter);

            if (classWildCard) {
                classWildCards.add(probe);
            }

            if (methodWildCard) {
                methodWildCards.add(probe);
            }

            if (classWildCard && methodWildCard) {
                return;
            }

            final String key = generateCombinedKey(classNameFilter, methodNameFilter);

            final List<T> methodProbeList = new CopyOnWriteArrayList<T>();
            if (probes.putIfAbsent(key, methodProbeList) == null) {
                methodProbeList.add(probe);
            } else {
                probes.get(key).add(probe);
            }
        }

        /**
         *
         * @param key
         * @return
         */
        public Collection<T> get(final String classNameFilter, final String methodNameFilter) {
            final String key;
            if ("<init>".equals(methodNameFilter)) {
                /*
                 *  Don't generate the suffixes for the "<init>"
                 *  because the token is not divisible unlike
                 *  method names
                 */
                key = generateCombinedKey(classNameFilter, "");
            } else {
                key = generateCombinedKey(classNameFilter, methodNameFilter);
            }

            final Set<T> values = new LinkedHashSet<T>();
            values.addAll(classWildCards);
            values.addAll(methodWildCards);

            final List<String> keyPrefixes = generateKeys(key);
            for (final String keyPrefix : keyPrefixes) {
                final List<T> list = probes.get(keyPrefix);
                if (list == null) {
                    continue;
                }

                values.addAll(list);
            }

            return values;
        }

        /**
         *
         * @param classNameFilter
         * @param methodNameFilter
         * @return
         */
        private String generateCombinedKey(final String classNameFilter, final String methodNameFilter) {
            return classNameFilter + methodNameFilter;
        }

        /**
         *
         * @param key
         * @return
         */
        private List<String> generateKeys(final String key) {
            final List<String> keyList = new LinkedList<String>();

            for (int i = 0; i < key.length(); i++) {
                final String newKey = key.substring(0, key.length() - i);
                keyList.add(newKey);
            }

            return keyList;
        }
    }

    private final Instrumentation instrumentation;

    private final ProbeHolder<MethodEntryProbe> methodEntryProbes = new ProbeHolder<MethodEntryProbe>();

    private final ProbeHolder<MethodExitProbe> methodExitProbes = new ProbeHolder<MethodExitProbe>();

    private final ProbeHolder<MethodProbe> methodProbes = new ProbeHolder<MethodProbe>();

    private final ProbeHolder<MonitorProbe> monitorProbes = new ProbeHolder<MonitorProbe>();

    private final ProbeHolder<ObjectAllocationProbe> objectAllocationProbes = new ProbeHolder<ObjectAllocationProbe>();

    /**
     * Constructor
     *
     * @param instrumentation
     *            {@link Instrumentation} instance used to add {@link Probe}s to
     *            the classes
     */
    public ProbeManagerImpl(final Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public void addProbe(final Probe probe) {
        final String processClassName = normalizeClassName(probe.getClassNameFilter());

        if (probe instanceof MethodProbe) {
            final MethodProbe methodProbe = (MethodProbe) probe;
            final String methodNameFilter = methodProbe.getMethodNameFilter();

            if (probe instanceof MethodEntryProbe) {
                methodEntryProbes.add(processClassName, methodNameFilter, (MethodEntryProbe) probe);
            }

            if (probe instanceof MethodExitProbe) {
                methodExitProbes.add(processClassName, methodNameFilter, (MethodExitProbe) probe);
            }

            if (!(probe instanceof MethodEntryProbe) && !(probe instanceof MethodExitProbe)) {
                methodProbes.add(processClassName, methodNameFilter, (MethodProbe) probe);
            }

        } else if (probe instanceof ObjectAllocationProbe) {
            objectAllocationProbes.add(processClassName, Probe.WILDCARD, (ObjectAllocationProbe) probe);
        } else if (probe instanceof MonitorProbe) {
            monitorProbes.add(processClassName, Probe.WILDCARD, (MonitorProbe) probe);
        } else {
            // Generic probe?
        }
    }

    @Override
    public boolean defineProbes(final Class<?> clazz) {
        try {
            instrumentation.retransformClasses(clazz);

            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeProbe(final Probe probe) {
        // TODO: Removing these things is not terribly important

        return false;
    }

    MethodEntryProbe getMethodEntryProbe(final String className, final String methodName) {
        final Collection<MethodEntryProbe> methods = methodEntryProbes.get(className, methodName);
        return new MethodEntryProbe() {
            @Override
            public String getClassNameFilter() {
                return className;
            }

            @Override
            public String getMethodNameFilter() {
                return methodName;
            }

            @Override
            public String getMethodDescriptionFilter() {
                return Probe.WILDCARD;
            }

            @Override
            public void run(final Object... args) {
                if (methods == null) {
                    return;
                }

                for (final MethodEntryProbe p : methods) {
                    p.run(args);
                }
            };
        };
    }

    MethodExitProbe getMethodExitProbe(final String className, final String methodName) {
        final Collection<MethodExitProbe> methods = methodExitProbes.get(className, methodName);
        return new MethodExitProbe() {

            @Override
            public void run(Object... args) {
                if (methods == null) {
                    return;
                }

                for (final MethodExitProbe p : methods) {
                    p.run(args);
                }
            }

            @Override
            public String getClassNameFilter() {
                return className;
            }

            @Override
            public String getMethodNameFilter() {
                return methodName;
            }

            @Override
            public String getMethodDescriptionFilter() {
                return Probe.WILDCARD;
            }
        };
    }

    MonitorProbe getMonitorProbe(final String className) {
        final Collection<MonitorProbe> probes = monitorProbes.get(className, Probe.WILDCARD);
        return new MonitorProbe() {

            @Override
            public void run(Object... args) {
                if (probes == null) {
                    return;
                }

                for (final MonitorProbe p : probes) {
                    p.run(args);
                }
            }

            @Override
            public String getClassNameFilter() {
                return className;
            }
        };
    }

    ObjectAllocationProbe getObjectAllocationProbe(final String className) {
        final Collection<ObjectAllocationProbe> probes = objectAllocationProbes.get(className, Probe.WILDCARD);
        return new ObjectAllocationProbe() {

            @Override
            public void run(Object... args) {
                if (probes == null) {
                    return;
                }

                for (final ObjectAllocationProbe p : probes) {
                    p.run(args);
                }
            }

            @Override
            public String getClassNameFilter() {
                return className;
            }
        };
    }

    /**
     * Normalizes the class name from a typical "instrumentj.impl.SomeClass" to
     * "instrumentj/impl/SomeClass"
     *
     * @param className
     *            the class name
     * @return the normalized class string
     */
    private String normalizeClassName(final String className) {
        final String processClassName = className.replace(".", "/");
        return processClassName;
    }
}
