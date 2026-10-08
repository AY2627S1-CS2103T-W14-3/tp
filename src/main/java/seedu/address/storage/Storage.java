package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;

import io.vavr.control.Option;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyGameMates;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage {

    /**
     * Returns the file path of the UserPrefs data file.
     */
    Path getUserPrefsFilePath();

    /**
     * Returns UserPrefs data from storage.
     * Returns {@code Option.none()} if storage file is not found.
     *
     * @throws DataLoadingException if the loading of data from preference file failed.
     */
    Option<UserPrefs> readUserPrefs() throws DataLoadingException;

    /**
     * Saves the given {@link seedu.address.model.ReadOnlyUserPrefs} to the storage.
     * @param userPrefs cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    /**
     * Returns the file path of the GameMates data file.
     */
    Path getGameMatesFilePath();

    /**
     * Returns GameMates data as a {@link ReadOnlyGameMates}.
     * Returns {@code Option.none()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Option<ReadOnlyGameMates> readGameMates() throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyGameMates} to the storage.
     * @param gameMates cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveGameMates(ReadOnlyGameMates gameMates) throws IOException;

}
