package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.commands.UnpinCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

class PinOrderIntegrationTest {
    @TempDir
    public Path directory;

    @Test
    void pinAndUnpin_restoreInsertionOrderAcrossSaveReloadAndSorting() throws Exception {
        ModelManager model = new ModelManager();
        JsonGameMatesStorage json = new JsonGameMatesStorage(directory.resolve("friends.json"));
        StorageManager storage = new StorageManager(json,
                new JsonUserPrefsStorage(directory.resolve("preferences.json")));
        LogicManager logic = new LogicManager(model, storage);
        for (String name : List.of("C", "B", "A")) {
            logic.execute("add n/" + name);
        }
        logic.execute("pin 3");
        assertNames(List.of("A", "C", "B"), model.getFilteredFriendList());
        assertNames(List.of("C", "B", "A"), json.readGameMates().get().getFriendList());

        model = new ModelManager(json.readGameMates().get(), new UserPrefs());
        logic = new LogicManager(model, storage);
        assertNames(List.of("A", "C", "B"), model.getFilteredFriendList());
        logic.execute("unpin 1");
        logic.execute("list s/name");
        assertNames(List.of("A", "B", "C"), model.getFilteredFriendList());
        logic.execute("list");
        assertNames(List.of("C", "B", "A"), model.getFilteredFriendList());
        assertEquals(model.getGameMates(), json.readGameMates().get());
        assertTrue(model.getGameMates().getFriendList().stream().noneMatch(Friend::isPinned));
    }

    @Test
    void multiplePinsAndArchiveChanges_keepInsertionOrderWithinEachGroup() throws Exception {
        ModelManager model = new ModelManager();
        LogicManager logic = new LogicManager(model, new StorageManager(
                new JsonGameMatesStorage(directory.resolve("friends.json")),
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        for (String name : List.of("C", "B", "A")) {
            logic.execute("add n/" + name);
        }
        logic.execute("pin 3");
        logic.execute("pin 3");
        assertNames(List.of("B", "A", "C"), model.getFilteredFriendList());
        logic.execute("find A C");
        assertNames(List.of("A", "C"), model.getFilteredFriendList());
        logic.execute("list");
        logic.execute("archive 1");
        assertNames(List.of("A", "C"), model.getFilteredFriendList());
        logic.execute("archivelist");
        assertNames(List.of("B"), model.getFilteredFriendList());
        assertFalse(model.getFilteredFriendList().getFirst().isPinned());
        assertThrows(CommandException.class, PinCommand.MESSAGE_ARCHIVED_FRIEND, () -> logic.execute("pin 1"));
        assertThrows(CommandException.class, UnpinCommand.MESSAGE_ARCHIVED_FRIEND, () -> logic.execute("unpin 1"));
        logic.execute("unarchive 1");
        logic.execute("list");
        assertNames(List.of("A", "C", "B"), model.getFilteredFriendList());
        logic.execute("unpin 1");
        assertNames(List.of("C", "B", "A"), model.getFilteredFriendList());
        assertNames(List.of("C", "B", "A"), model.getGameMates().getFriendList());
    }

    private void assertNames(List<String> expected, List<Friend> friends) {
        assertEquals(expected, friends.stream().map(friend -> friend.getName().fullName).toList());
    }
}
