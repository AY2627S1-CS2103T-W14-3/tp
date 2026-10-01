package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.GameMates;
import seedu.address.testutil.TypicalFriends;

public class JsonSerializableGameMatesTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableGameMatesTest");
    private static final Path TYPICAL_FRIENDS_FILE = TEST_DATA_FOLDER.resolve("typicalFriendsGameMates.json");
    private static final Path INVALID_FRIEND_FILE = TEST_DATA_FOLDER.resolve("invalidFriendGameMates.json");
    private static final Path DUPLICATE_FRIEND_FILE = TEST_DATA_FOLDER.resolve("duplicateFriendGameMates.json");

    @Test
    public void toModelType_typicalFriendsFile_success() throws Exception {
        JsonSerializableGameMates dataFromFile = JsonUtil.readJsonFile(TYPICAL_FRIENDS_FILE,
                JsonSerializableGameMates.class).get();
        GameMates gameMatesFromFile = dataFromFile.toModelType();
        GameMates typicalFriendsGameMates = TypicalFriends.getTypicalGameMates();
        assertEquals(gameMatesFromFile, typicalFriendsGameMates);
    }

    @Test
    public void toModelType_invalidFriendFile_throwsIllegalValueException() throws Exception {
        JsonSerializableGameMates dataFromFile = JsonUtil.readJsonFile(INVALID_FRIEND_FILE,
                JsonSerializableGameMates.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateFriends_throwsIllegalValueException() throws Exception {
        JsonSerializableGameMates dataFromFile = JsonUtil.readJsonFile(DUPLICATE_FRIEND_FILE,
                JsonSerializableGameMates.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableGameMates.MESSAGE_DUPLICATE_FRIEND,
                dataFromFile::toModelType);
    }

}
