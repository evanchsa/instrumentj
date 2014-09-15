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

import instrumentj.CallStack;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class CallStackHelper implements CallStack {

    private static final class CallStackEntryImpl implements Entry {

        private boolean exiting;

        private final String methodDescription;

        private final String methodName;

        public CallStackEntryImpl(final String methodName, final String methodDescription) {
            this.methodName = methodName;
            this.methodDescription = methodDescription;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof CallStackEntryImpl)) {
                return false;
            }

            final CallStackEntryImpl other = (CallStackEntryImpl) obj;
            if (methodDescription == null) {
                if (other.methodDescription != null) {
                    return false;
                }
            } else if (!methodDescription.equals(other.methodDescription)) {
                return false;
            }

            if (methodName == null) {
                if (other.methodName != null) {
                    return false;
                }
            } else if (!methodName.equals(other.methodName)) {
                return false;
            }

            return true;
        }

        @Override
        public String getMethodDescription() {
            return methodDescription;
        }

        @Override
        public String getMethodName() {
            return methodName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((methodDescription == null) ? 0 : methodDescription.hashCode());
            result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
            return result;
        }

        @Override
        public boolean isExiting() {
            return exiting;
        }

        @Override
        public void markExiting() {
            exiting = true;
        }

        @Override
        public String toString() {
            return methodName + methodDescription;
        }
    }

    private static final ThreadLocal<Deque<Entry>> CALL_STACK = new ThreadLocal<Deque<Entry>>() {
        @Override
        protected java.util.Deque<Entry> initialValue() {
            return new ArrayDeque<Entry>();
        };
    };

    @Override
    public boolean findFirstParent(final String methodName) {
        final Iterator<Entry> stackElement = CALL_STACK.get().descendingIterator();
        while (stackElement.hasNext()) {
            if (methodName.equals(stackElement.next().getMethodName()) && stackElement.hasNext()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean findParent(final String methodName) {
        for (final Entry stackElement : CALL_STACK.get()) {
            if (methodName.equals(stackElement.getMethodName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Entry peek() {
        return CALL_STACK.get().peek();
    }

    @Override
    public void pop() {
        CALL_STACK.get().pop();
    }

    @Override
    public void printStackTrace(PrintStream out) {
        out.println("Thread: " + Thread.currentThread().getName());

        for (final Entry stackElement : CALL_STACK.get()) {
            out.println("\t" + stackElement);
        }
    }

    @Override
    public void push(String methodName, String methodDescription) {
        CALL_STACK.get().push(new CallStackEntryImpl(methodName, methodDescription));
    }
}
