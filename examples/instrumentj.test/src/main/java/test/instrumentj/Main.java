package test.instrumentj;

import instrumentj.StaticProfilerInterface;
import instrumentj.probes.impl.LoggingMethodEntryProbe;
import instrumentj.probes.impl.LoggingMethodExitProbe;
import test.instrumentj.impl.SomeClass;

public class Main implements Runnable {

    private final SomeClass someClass = new SomeClass();
    /**
     * @param args
     */
    public static void main(String[] args) {
        StaticProfilerInterface.INSTANCE.getProfiler().addProbe(new LoggingMethodEntryProbe());
        StaticProfilerInterface.INSTANCE.getProfiler().addProbe(new LoggingMethodExitProbe());

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
        } catch (Throwable t) {

        }
    }

}
