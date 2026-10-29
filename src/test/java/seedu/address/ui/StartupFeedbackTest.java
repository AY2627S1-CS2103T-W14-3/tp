package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.ui.FxTestUtil.runAndWait;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import seedu.address.MainApp;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

public class StartupFeedbackTest {

    @TempDir
    public Path testFolder;

    @BeforeAll
    public static void initializeJavaFx() {
        runAndWait(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void start_failedDataLoad_displaysWarningFromMainApp() throws Exception {
        Path dataFile = testFolder.resolve("gamemates.json");
        Files.writeString(dataFile, "not JSON");
        TestMainApp app = new TestMainApp(createStorage(dataFile));
        app.init();

        runAndWait(() -> {
            Stage stage = new Stage();
            try {
                app.start(stage);

                assertTrue(stage.isShowing());
                String feedback = getFeedback(stage);
                assertEquals(app.getStartupMessage(), feedback);
                assertTrue(feedback.contains("Failed to load " + dataFile));
                assertTrue(feedback.contains("Unrecognized token"));
                assertTrue(feedback.contains("Starting with an empty GameMates."));
                assertTrue(feedback.endsWith(
                        "Close the window without entering commands to preserve the original file."
                        + " Back up or repair it before continuing."));
            } finally {
                stage.close();
            }
        });
        assertEquals("not JSON", Files.readString(dataFile));
    }

    @Test
    public void start_missingDataFile_displaysNoWarning() throws Exception {
        TestMainApp app = new TestMainApp(createStorage(testFolder.resolve("gamemates.json")));
        app.init();

        runAndWait(() -> {
            Stage stage = new Stage();
            try {
                app.start(stage);

                assertTrue(stage.isShowing());
                assertEquals("", getFeedback(stage));
            } finally {
                stage.close();
            }
        });
    }

    @Test
    public void start_defaultUiManager_displaysNoWarning() throws Exception {
        Storage storage = createStorage(testFolder.resolve("gamemates.json"));
        Logic logic = new LogicManager(new ModelManager(), storage);

        runAndWait(() -> {
            Stage stage = new Stage();
            try {
                new UiManager(logic, storage.getGameMatesFilePath()).start(stage);

                assertTrue(stage.isShowing());
                assertEquals("", getFeedback(stage));
            } finally {
                stage.close();
            }
        });
    }

    @Test
    public void showFeedbackToUser_updatesAndClearsDisplayedText() throws Exception {
        Storage storage = createStorage(testFolder.resolve("gamemates.json"));
        Logic logic = new LogicManager(new ModelManager(), storage);

        runAndWait(() -> {
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, storage.getGameMatesFilePath());
                window.fillInnerParts();

                window.showFeedbackToUser("Failed to load gamemates.json\nStarting with an empty GameMates.");
                assertEquals("Failed to load gamemates.json\nStarting with an empty GameMates.", getFeedback(stage));

                window.showFeedbackToUser("Replacement feedback");
                assertEquals("Replacement feedback", getFeedback(stage));

                window.showFeedbackToUser("");
                assertEquals("", getFeedback(stage));
            } finally {
                stage.close();
            }
        });
    }

    private Storage createStorage(Path dataFile) {
        return new StorageManager(new JsonGameMatesStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
    }

    private static String getFeedback(Stage stage) {
        return ((TextArea) stage.getScene().lookup("#resultDisplay")).getText();
    }

    /**
     * Exercises application initialization using temporary data without reading or writing personal files.
     */
    private static class TestMainApp extends MainApp {
        private final Storage testStorage;

        TestMainApp(Storage testStorage) {
            this.testStorage = testStorage;
        }

        @Override
        protected UserPrefs initPrefs(JsonUserPrefsStorage prefsStorage) {
            return new UserPrefs();
        }

        @Override
        protected Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
            return super.initModelManager(testStorage, userPrefs);
        }

        String getStartupMessage() {
            return startupMessage;
        }
    }
}
