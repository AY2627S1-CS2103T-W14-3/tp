package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyGameMates;

/**
 * A class to access GameMates data stored as a JSON file on the hard disk.
 */
public class JsonGameMatesStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonGameMatesStorage.class);

    private Path filePath;

    public JsonGameMatesStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getGameMatesFilePath() {
        return filePath;
    }

    /**
     * Returns GameMates data as a {@link ReadOnlyGameMates}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyGameMates> readGameMates() throws DataLoadingException {
        return readGameMates(filePath);
    }

    /**
     * Similar to {@link #readGameMates()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyGameMates> readGameMates(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableGameMates> jsonGameMates = JsonUtil.readJsonFile(
                filePath, JsonSerializableGameMates.class);
        if (!jsonGameMates.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonGameMates.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyGameMates} to the storage.
     * @param gameMates cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveGameMates(ReadOnlyGameMates gameMates) throws IOException {
        saveGameMates(gameMates, filePath);
    }

    /**
     * Similar to {@link #saveGameMates(ReadOnlyGameMates)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveGameMates(ReadOnlyGameMates gameMates, Path filePath) throws IOException {
        requireNonNull(gameMates);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableGameMates(gameMates), filePath);
    }

}
