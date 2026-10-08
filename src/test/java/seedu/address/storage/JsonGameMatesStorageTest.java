package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.HOON;
import static seedu.address.testutil.TypicalFriends.IDA;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyGameMates;
import seedu.address.model.UserPrefs;

public class JsonGameMatesStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonGameMatesStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readGameMates_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readGameMates(null));
    }

    private java.util.Optional<ReadOnlyGameMates> readGameMates(String filePath) throws Exception {
        return new JsonGameMatesStorage(Paths.get(filePath))
                .readGameMates(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readGameMates("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readGameMates("notJsonFormatGameMates.json"));
    }

    @Test
    public void readGameMates_invalidFriendGameMates_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readGameMates("invalidFriendGameMates.json"));
    }

    @Test
    public void readGameMates_invalidAndValidFriendGameMates_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readGameMates("invalidAndValidFriendGameMates.json"));
    }

    @Test
    public void readAndSaveGameMates_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempGameMates.json");
        GameMates original = getTypicalGameMates();
        JsonGameMatesStorage jsonGameMatesStorage = new JsonGameMatesStorage(filePath);

        // Save in new file and read back
        jsonGameMatesStorage.saveGameMates(original, filePath);
        ReadOnlyGameMates readBack = jsonGameMatesStorage.readGameMates(filePath).get();
        assertEquals(original, new GameMates(readBack));

        // Modify data, overwrite existing file, and read back
        original.addFriend(HOON);
        original.removeFriend(ALICE);
        jsonGameMatesStorage.saveGameMates(original, filePath);
        readBack = jsonGameMatesStorage.readGameMates(filePath).get();
        assertEquals(original, new GameMates(readBack));

        // Save and read without specifying file path
        original.addFriend(IDA);
        jsonGameMatesStorage.saveGameMates(original); // file path not specified
        readBack = jsonGameMatesStorage.readGameMates().get(); // file path not specified
        assertEquals(original, new GameMates(readBack));

    }

    @Test
    public void readAndSaveGameMates_optionalContacts_surviveReloadAndEdit() throws Exception {
        Model model = new ModelManager(new GameMates(), new UserPrefs());
        GameMatesParser parser = new GameMatesParser();
        parser.parseCommand("add n/Melody").execute(model);
        parser.parseCommand("add n/Joash p/91234567").execute(model);
        parser.parseCommand("add n/Felicia e/felicia@example.com").execute(model);
        Path filePath = testFolder.resolve("optional-contacts.json");
        JsonGameMatesStorage storage = new JsonGameMatesStorage(filePath);
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), new GameMates(storage.readGameMates().get()));
        parser.parseCommand("edit 1 p/98765432").execute(model);
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), new GameMates(storage.readGameMates().get()));
    }

    @Test
    public void saveGameMates_nullGameMates_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveGameMates(null, "SomeFile.json"));
    }

    /**
     * Saves {@code gameMates} at the specified {@code filePath}.
     */
    private void saveGameMates(ReadOnlyGameMates gameMates, String filePath) {
        try {
            new JsonGameMatesStorage(Paths.get(filePath))
                    .saveGameMates(gameMates, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveGameMates_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveGameMates(new GameMates(), null));
    }
}
