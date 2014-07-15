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

import java.io.PrintStream;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface CallStack {

    /**
     * Represents an entry in a call stack
     *
     * @author Stephen Evanchik (evanchsa@gmail.com)
     *
     */
	interface Entry {

        /**
         * Determines if this call stack entry is about to exit
         *
         * @return {@code true} if the call stack is exiting; {@code false} if
         *         the entry is new
         */
		boolean isExiting();

        /**
         * Returns the argument descriptors for the method
         *
         * @return the argument descriptors for the method
         */
		String getMethodDescription();

        /**
         * Returns the name of the method
         *
         * @return the name of the method
         */
		String getMethodName();

		/**
		 * Marks the entry as about to exit
		 */
		void markExiting();
	}

	boolean findFirstParent(String methodName);

	boolean findParent(String methodName);

    Entry peek();

    void pop();

    void push(String methodName, String methodDescription);

    void printStackTrace(PrintStream out);
}
