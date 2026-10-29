package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.GameMates;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.TypicalFriends;

public class JsonSerializableGameMatesTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableGameMatesTest");
    private static final Path TYPICAL_FRIENDS_FILE = TEST_DATA_FOLDER.resolve("typicalFriendsGameMates.json");
    private static final Path INVALID_FRIEND_FILE = TEST_DATA_FOLDER.resolve("invalidFriendGameMates.json");
    private static final Path INVALID_MYSELF_FILE = TEST_DATA_FOLDER.resolve("invalidMyselfGameMates.json");
    private static final Path DUPLICATE_FRIEND_FILE = TEST_DATA_FOLDER.resolve("duplicateFriendGameMates.json");

    @Test
    public void roundTrip_myselfWithDistinctName_remainsSeparate() throws Exception {
        GameMates original = TypicalFriends.getTypicalGameMates();
        original.setMyself(TypicalFriends.HOON);
        String json = JsonUtil.toJsonString(new JsonSerializableGameMates(original));
        GameMates restored = JsonUtil.fromJsonString(json, JsonSerializableGameMates.class).toModelType();
        assertEquals(original, restored);
        assertEquals(original.getFriendList(), restored.getFriendList());
        assertEquals(TypicalFriends.HOON, restored.getMyself());
    }

    @Test
    public void toModelType_archivedPinnedFriend_throwsIllegalValueException() throws Exception {
        String json = """
                {"friends": [
                  {"name": "Active One", "pinned": true},
                  {"name": "Active Two", "pinned": true},
                  {"name": "Active Three", "pinned": true},
                  {"name": "Active Four", "pinned": true},
                  {"name": "Active Five", "pinned": true},
                  {"name": "Archived Friend", "archived": true, "pinned": true}
                ]}
                """;
        JsonSerializableGameMates data = JsonUtil.fromJsonString(json, JsonSerializableGameMates.class);
        assertThrows(IllegalValueException.class, Friend.MESSAGE_ARCHIVED_PINNED, data::toModelType);
    }

    @Test
    public void toModelType_invalidMyself_throwsIllegalValueException() throws Exception {
        JsonSerializableGameMates data = JsonUtil.readJsonFile(INVALID_MYSELF_FILE,
                JsonSerializableGameMates.class).get();
        assertThrows(IllegalValueException.class, data::toModelType);
    }

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

    @Test
    public void toModelType_moreThanMaximumPinnedFriends_throwsIllegalValueException() throws Exception {
        String json = """
                {"friends": [
                  {"name": "Pinned Friend 1", "pinned": true},
                  {"name": "Pinned Friend 2", "pinned": true},
                  {"name": "Pinned Friend 3", "pinned": true},
                  {"name": "Pinned Friend 4", "pinned": true},
                  {"name": "Pinned Friend 5", "pinned": true},
                  {"name": "Pinned Friend 6", "pinned": true}
                ]}
                """;
        JsonSerializableGameMates data = JsonUtil.fromJsonString(json, JsonSerializableGameMates.class);

        assertThrows(IllegalValueException.class, JsonSerializableGameMates.MESSAGE_TOO_MANY_PINNED_FRIENDS,
                data::toModelType);
    }

}
