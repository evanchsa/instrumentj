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
package test.instrumentj;

import instrumentj.Exclude;
import instrumentj.StaticProfilerInterface;
import instrumentj.probes.Probe;
import instrumentj.probes.impl.LoggingMethodEntryProbe;
import instrumentj.probes.impl.LoggingMethodExitProbe;
import instrumentj.probes.impl.StatefulMethodProbe;
import test.instrumentj.impl.SomeClass;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class Main implements Runnable {

    private final SomeClass someClass = new SomeClass();

    @Exclude
    static class TestStatefulMethodProbe extends StatefulMethodProbe {

		@Override
		public String getClassNameFilter() {
			return Main.class.getName();
		}

		@Override
		public String getMethodNameFilter() {
			return Probe.WILDCARD;
		}

		@Override
		public String getMethodDescriptionFilter() {
			return Probe.WILDCARD;
		}

		@Override
		protected void doRun(boolean exiting, Object... args) {

			if (exiting) {
				System.out.println("Stateful exiting method: " + args[0] + "|" + args[1] + "|" + args[2]);
			} else {
				System.out.println("Stateful entering method: " + args[0] + "|" + args[1] + "|" + args[2]);
			}
		}
	}

    /**
     * @param args
     */
    public static void main(String[] args) {
        StaticProfilerInterface.INSTANCE.getProfiler().addProbe(new LoggingMethodEntryProbe());
        StaticProfilerInterface.INSTANCE.getProfiler().addProbe(new LoggingMethodExitProbe());
        StaticProfilerInterface.INSTANCE.getProfiler().addProbe(new TestStatefulMethodProbe());

        final Main m = new Main();
        m.run();
    }

    @Override
    public void run() {
        try {
            while (true) {
                someClass.aMethod();
                someClass.bMethod();

                Thread.sleep(10 * 1000);
            }
        } catch (final Throwable t) {

        }
    }

}
