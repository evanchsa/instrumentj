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
package instrumentj.shell;

import groovy.lang.Binding;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class ShellServer implements Runnable {

    private static final Logger logger = Logger.getLogger(ShellServer.class.getName());

    private final Binding binding;

    private final int port;

    private volatile boolean run = true;

    private final Executor clientExecutor = Executors.newCachedThreadPool(new ThreadFactory() {

        private final AtomicInteger clientNumber = new AtomicInteger();

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("profiler-client-" + clientNumber.incrementAndGet());
            return t;
        }
    });

    private ServerSocket serverSocket;

    public ShellServer(final int port) {
        this(port, new Binding());
    }

    public ShellServer(final int port, final Binding binding) {
        if (port < 1) {
            throw new IllegalArgumentException("port");
        }

        if (binding == null) {
            throw new NullPointerException("binding");
        }

        this.port = port;
        this.binding = binding;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(port);

            while(run) {
                Socket client = null;
                try {
                    client = serverSocket.accept();

                    if (!run) {
                        try {
                            client.close();
                        } catch (final IOException e) {
                            // Intentionally left blank
                        }

                        return;
                    }

                    final GroovyShellSession groovyShellSession = new GroovyShellSession(client, binding);
                    clientExecutor.execute(groovyShellSession);

                } catch (final IOException e) {
                    logger.log(Level.SEVERE, "Unable to accept client connection", e);
                }
            }
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "Unable to setup shell server", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (final IOException e) {
                    // Intentionally left blank
                }
            }
        }
    }

    public void stop() {
        this.run = false;
    }
}
