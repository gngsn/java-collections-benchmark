package benchmark;

/**
 * BenchRunnable
 *
 */
@FunctionalInterface
public interface BenchRunnable {
    /**
     * Runnable that can exploit the current loop index
     *
     * @param loopIndex loop index
     */
    void run(int loopIndex);
}
