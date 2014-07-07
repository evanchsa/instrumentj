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
import instrumentj.Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class GroovyShellSession implements Runnable {

    private static final Logger logger = Logger.getLogger(GroovyShellSession.class.getName());

    private static final String OUT_KEY = "out";

    private final Binding binding;

    private final Socket socket;

    public GroovyShellSession(final Socket socket, final Binding binding) {
        if (socket == null) {
            throw new NullPointerException("socket");
        }

        if (binding == null) {
            throw new NullPointerException("binding");
        }

        this.socket = socket;
        this.binding = binding;
    }

    @Override
    public void run() {
        PrintStream out = null;
        InputStream in = null;

        try {
            out = new PrintStream(socket.getOutputStream());
            in = socket.getInputStream();

            binding.setVariable(OUT_KEY, out);

            final IO io = new IO(in, out, out);
            final Groovysh groovy = new Groovysh(Main.class.getClassLoader(), binding, io);

            try {
                groovy.run((String[])null);
            } catch (final Exception e) {
                logger.log(Level.SEVERE, "Exception encountered while running shell", e);
            }

            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (final Exception e) {
            logger.log(Level.SEVERE, "Exception encountered while creating shell", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final Exception e) {
                    // Intentionally left blank
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (final Exception e) {
                    // Intentionally left blank
                }
            }
        }
    }

    /**
     *
     */
    public synchronized void stop() {
        try {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (final IOException e) {
                    // Intentionally left blank;
                }
            }
        } catch (final Exception e) {
            // Intentionally left blank
        }
    }
}
