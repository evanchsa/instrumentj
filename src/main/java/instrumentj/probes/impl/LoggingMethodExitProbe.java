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

import instrumentj.probes.MethodExitProbe;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 */
public class LoggingMethodExitProbe extends BaseMethodProbe implements MethodExitProbe {

    @Override
    public void run(Object... args) {
        final String threadName = Thread.currentThread().getName();

        final StringBuilder sb = new StringBuilder("Exit:" + threadName);
        for (final Object o : args) {
            sb.append("|");
            sb.append(o.toString());
        }

        System.out.println(sb.toString());
    }
}
