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

import groovy.lang.Binding;
import instrumentj.impl.ProfilerControllerImpl;
import instrumentj.shell.ShellServer;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class Main {

    private static ProfilerControllerImpl profilerController;

    private static Executor shell = Executors.newSingleThreadExecutor(new ThreadFactory() {

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("Interactive instrumentation console");

            return t;
        }
    });

    private static ShellServer shellServer;

    /**
     *
     * @param args
     * @param instrumentation
     */
    public static void agentmain(final String args, final Instrumentation instrumentation) throws Exception {
        profilerController = new ProfilerControllerImpl(instrumentation);

        StaticProfilerInterface.initialize(profilerController);

        profilerController.initialize();

        final Binding binding = new Binding();
        binding.setProperty("profiler", StaticProfilerInterface.INSTANCE.getProfiler());

        shellServer = new ShellServer(7000, binding);
        shell.execute(shellServer);
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        if (args == null || args.length < 1) {
            throw new IllegalStateException("args");
        }

        final String pid = args[0];

        final File thisJar = getJarFile();

        final VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(thisJar.getAbsolutePath(), "");
        vm.detach();
    }

    /**
     * @return
     */
    private static File getJarFile() {
        final File thisJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return thisJar;
    }

    /**
     *
     * @param args
     * @param instrumentation
     */
    public static void premain(final String args, final Instrumentation instrumentation) throws Exception {
        agentmain(args, instrumentation);
    }
}
