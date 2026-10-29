package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.GameMates;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonUserPrefsStorage;

public class MainAppTest {

    @TempDir
    public Path testFolder;

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
