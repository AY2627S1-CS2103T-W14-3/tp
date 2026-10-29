package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.SquadCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.FriendBuilder;

/** Reproduces issue #122, where a game name added with prefix-like text could not be selected. */
public class PrefixLikeGameNameIntegrationTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_squadWithQuotedPrefixLikeGameName_findsSquad() throws Exception {
        ModelManager model = new ModelManager();
        model.addFriend(new FriendBuilder().withName("Player").withGames().build());
        LogicManager logic = new LogicManager(model,
                new StorageManager(new JsonGameMatesStorage(directory.resolve("data.json")),
                        new JsonUserPrefsStorage(directory.resolve("preferences.json"))));

        // gameadd has no n/ prefix, so "n/2" is part of the game name.
        logic.execute("gameadd 1 g/Version n/2");
        assertThrows(ParseException.class, () -> logic.execute("squad g/Version n/2 n/2"));
        assertTrue(logic.execute("squad g/\"Version n/2\" n/2").getFeedbackToUser()
                .startsWith(String.format(SquadCommand.MESSAGE_SUCCESS, 2)));
    }
}
