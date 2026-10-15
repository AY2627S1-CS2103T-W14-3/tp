package seedu.address.ui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;

/** Utilities for running JavaFX tests on the JavaFX application thread. */
public final class FxTestUtil {
    private static final AtomicBoolean isPlatformStarted = new AtomicBoolean();

    private FxTestUtil() {}

    /** Runs an action on the JavaFX application thread and waits for it to finish. */
    public static void runAndWait(Runnable action) {
        startPlatform();
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        AtomicReference<Throwable> thrown = new AtomicReference<>();
        CountDownLatch completed = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                thrown.set(throwable);
            } finally {
                completed.countDown();
            }
        });
        await(completed);

        if (thrown.get() != null) {
            throw new AssertionError("JavaFX test action failed", thrown.get());
        }
    }

    private static void startPlatform() {
        if (!isPlatformStarted.compareAndSet(false, true)) {
            return;
        }

        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        await(started);
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("JavaFX test thread was interrupted", e);
        }
    }
}
