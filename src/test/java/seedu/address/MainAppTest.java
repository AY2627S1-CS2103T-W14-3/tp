package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    private MainApp app;
    private StorageManager storage;
    private Path dataFile;

    @BeforeEach
    public void setUp() {
        app = new MainApp();
        dataFile = testFolder.resolve("gamemates.json");
        storage = new StorageManager(new JsonGameMatesStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
    }

    @Test
    public void initModelManager_missingFile_loadsSampleDataWithoutWarning() {
        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(SampleDataUtil.getSampleGameMates(), model.getGameMates());
        assertEquals("", app.startupMessage);
    }

    @Test
    public void initModelManager_validFile_loadsSavedDataWithoutWarning() throws Exception {
        storage.saveGameMates(getTypicalGameMates());

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(getTypicalGameMates(), model.getGameMates());
        assertEquals("", app.startupMessage);
    }

    @Test
    public void initModelManager_invalidJson_loadsEmptyDataWithWarning() throws Exception {
        String invalidJson = "not JSON";
        Files.writeString(dataFile, invalidJson);

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(new GameMates(), model.getGameMates());
        assertTrue(app.startupMessage.contains("Failed to load " + dataFile));
        assertTrue(app.startupMessage.contains("Unrecognized token"));
        assertTrue(app.startupMessage.contains("Starting with an empty GameMates."));
        assertTrue(app.startupMessage.endsWith(
                "Close the window without entering commands to preserve the original file."
                + " Back up or repair it before continuing."));
        assertEquals(invalidJson, Files.readString(dataFile));
    }

    @Test
    public void initModelManager_invalidValues_displaysValidationReason() throws Exception {
        Files.copy(Path.of("src", "test", "data", "JsonGameMatesStorageTest",
                "invalidFriendGameMates.json"), dataFile);

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(new GameMates(), model.getGameMates());
        assertTrue(app.startupMessage.contains("Failed to load " + dataFile));
        assertTrue(app.startupMessage.contains("due to: "));
        assertFalse(app.startupMessage.contains("due to: null"));
    }

    @Test
    public void initModelManager_unreadableFile_displaysIoReason() throws Exception {
        Files.createDirectory(dataFile);

        Model model = app.initModelManager(storage, new UserPrefs());

        assertEquals(new GameMates(), model.getGameMates());
        assertTrue(app.startupMessage.contains("Failed to load " + dataFile));
        assertTrue(app.startupMessage.contains("due to: "));
    }

    @Test
    public void initModelManager_successAfterFailure_clearsWarning() throws Exception {
        Files.writeString(dataFile, "not JSON");
        app.initModelManager(storage, new UserPrefs());
        storage.saveGameMates(getTypicalGameMates());

        app.initModelManager(storage, new UserPrefs());

        assertEquals("", app.startupMessage);
    }

    @Test
    public void initPrefs_nullGuiSettings_usesAndSavesDefaults() throws IOException, DataLoadingException {
        assertInvalidPrefsRecovered("{\"guiSettings\":null}");
    }

    @Test
    public void initPrefs_nullRoot_usesAndSavesDefaults() throws IOException, DataLoadingException {
        assertInvalidPrefsRecovered("null");
    }

    private void assertInvalidPrefsRecovered(String json) throws IOException, DataLoadingException {
        Path prefsFilePath = testFolder.resolve("preferences.json");
        Files.writeString(prefsFilePath, json);
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(prefsFilePath);

        UserPrefs actual = new MainApp().initPrefs(storage);

        assertEquals(new UserPrefs(), actual);
        assertEquals(actual, storage.readUserPrefs().get());
        // Model construction is where the reported startup crash occurred.
        assertEquals(actual, new ModelManager(new GameMates(), actual).getUserPrefs());
    }
}
