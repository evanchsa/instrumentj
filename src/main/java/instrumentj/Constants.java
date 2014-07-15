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

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface Constants {

	public static final String EXCLUDE_ANNOTATION = "Linstrumentj/Exclude;";

    public static final String INSTRUMENTJ_STATIC_PROFILER_INTERFACE = "instrumentj/StaticProfilerInterface";

    public static final String METHOD_ENTER_PROBES = "methodEnterProbes";

    public static final String METHOD_ENTER_PROBES_DESC = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    public static final String METHOD_EXIT_PROBES = "methodExitProbes";

    public static final String METHOD_EXIT_PROBES_DESC = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V";

    public static final String OBJECT_ALLOCATION_PROBES = "objectAllocationProbes";

    public static final String OBJECT_ALLOCATION_PROBES_DESC = "(Ljava/lang/String;)V";
}
