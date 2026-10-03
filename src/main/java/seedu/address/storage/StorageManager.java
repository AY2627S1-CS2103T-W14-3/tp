package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyGameMates;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of GameMates data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonGameMatesStorage gameMatesStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given GameMates and user prefs storage.
     */
    public StorageManager(JsonGameMatesStorage gameMatesStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.gameMatesStorage = gameMatesStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ GameMates methods ==============================

    @Override
    public Path getGameMatesFilePath() {
        return gameMatesStorage.getGameMatesFilePath();
    }

    @Override
    public Optional<ReadOnlyGameMates> readGameMates() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + gameMatesStorage.getGameMatesFilePath());
        return gameMatesStorage.readGameMates();
    }

    @Override
    public void saveGameMates(ReadOnlyGameMates gameMates) throws IOException {
        logger.fine("Attempting to write to data file: " + gameMatesStorage.getGameMatesFilePath());
        gameMatesStorage.saveGameMates(gameMates);
    }

}
