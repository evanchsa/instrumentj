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

import instrumentj.CallStack;
import instrumentj.StaticProfilerInterface;
import instrumentj.probes.MethodEntryProbe;
import instrumentj.probes.MethodExitProbe;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public abstract class StatefulMethodProbe implements MethodEntryProbe, MethodExitProbe {

	@Override
	public final void run(Object... args) {
		final CallStack.Entry stack = StaticProfilerInterface.INSTANCE.getProfiler().peek();

		doRun(stack.isExiting(), args);
	}

    /**
     * Implement this method to perform stateful measurements
     *
     * @param exiting
     *            {@code true} if the method is exiting; {@code false} otherwise
     * @param args
     */
	protected abstract void doRun(boolean exiting, Object... args);
}
