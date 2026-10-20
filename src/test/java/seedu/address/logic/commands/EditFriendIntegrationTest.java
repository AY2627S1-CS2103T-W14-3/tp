package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

/**
 * Tests complete edit commands, including contact removal, feedback, and persistence.
 */
public class EditFriendIntegrationTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager(new GameMates(), new UserPrefs());
    private final GameMatesParser parser = new GameMatesParser();

    private Friend addFriend(String name) {
        Friend friend = new FriendBuilder().withName(name).withTags("friend")
                .withGames(new GameBuilder().build()).build();
        model.addFriend(friend);
        return friend;
    }

    @Test
    public void execute_myselfWithoutFriends_updatesDetailsAndSurvivesReload() throws Exception {
        Friend original = new FriendBuilder().withName("Myself")
                .withGames(new GameBuilder().build()).withTags("friend").build();
        model.setMyself(original);

        CommandResult result = parser.parseCommand("edit myself n/Alex Tan p/ e/").execute(model);

        Friend expected = new Friend(new Name("Alex Tan"), Phone.EMPTY, Email.EMPTY,
                original.getGames(), original.getTags());
        assertEquals(expected, model.getGameMates().getMyself());
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals("Edited Alex Tan\n- changed name to Alex Tan\n- removed phone number\n- removed email",
                result.getFeedbackToUser());
        JsonGameMatesStorage storage = new JsonGameMatesStorage(testFolder.resolve("myself.json"));
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), storage.readGameMates().get());
    }

    @Test
    public void execute_myselfMatchingFriendName_rejectsAndPreservesFriendsAndFilter() throws Exception {
        Friend hidden = addFriend("Melody Tan");
        Friend displayed = addFriend("Joash");
        model.updateFilteredFriendList(friend -> friend.equals(displayed));
        GameMates before = new GameMates(model.getGameMates());

        for (String name : List.of("Melody Tan", "MELODY TAN", "melody   tan")) {
            assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_FRIEND, ()
                    -> parser.parseCommand("edit myself n/" + name + " p/").execute(model));
            assertEquals(before, model.getGameMates());
            assertEquals(List.of(displayed), model.getFilteredFriendList());
        }

        assertEquals(List.of(hidden, displayed), model.getGameMates().getFriendList());
    }

    @Test
    public void execute_friendMatchingMyselfName_rejectsWithoutChangingData() throws Exception {
        Friend displayed = addFriend("Joash");
        addFriend("Melody");
        model.setMyself(new FriendBuilder().withName("Alex Tan").build());
        model.updateFilteredFriendList(friend -> friend.equals(displayed));
        GameMates before = new GameMates(model.getGameMates());

        for (String name : List.of("Alex Tan", "ALEX TAN", "alex   tan")) {
            assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_MYSELF, ()
                    -> parser.parseCommand("edit 1 n/" + name + " p/").execute(model));
            assertEquals(before, model.getGameMates());
            assertEquals(List.of(displayed), model.getFilteredFriendList());
        }
    }

    @Test
    public void execute_addFriendMatchingMyselfName_rejectsWithoutChangingData() throws Exception {
        model.setMyself(new FriendBuilder().withName("Alex Tan").build());
        GameMates before = new GameMates(model.getGameMates());

        for (String name : List.of("Alex Tan", "ALEX TAN", "alex   tan")) {
            assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_MYSELF, ()
                    -> parser.parseCommand("add n/" + name).execute(model));
            assertEquals(before, model.getGameMates());
        }
    }

    @Test
    public void execute_myselfSameNameAndCaseOnlyRename_allowOwnIdentity() throws Exception {
        addFriend("Joash");
        model.setMyself(new FriendBuilder().withName("Alex Tan").build());

        parser.parseCommand("edit myself n/Alex Tan").execute(model);
        CommandResult result = parser.parseCommand("edit myself n/alex tan").execute(model);

        assertEquals("alex tan", model.getGameMates().getMyself().getName().fullName);
        assertEquals("Edited alex tan\n- changed name to alex tan", result.getFeedbackToUser());
    }

    @Test
    public void execute_myselfTags_toggleAndClearOnlyProfileTags() throws Exception {
        Friend friend = addFriend("Melody");
        Friend original = new FriendBuilder().withName("Myself").withTags("friend", "teammate").build();
        model.setMyself(original);

        parser.parseCommand("edit myself t/friend t/new").execute(model);
        assertEquals(new FriendBuilder(original).withTags("teammate", "new").build(),
                model.getGameMates().getMyself());

        parser.parseCommand("edit myself t/").execute(model);
        assertEquals(new FriendBuilder(original).withTags().build(), model.getGameMates().getMyself());
        assertEquals(friend, model.getFilteredFriendList().getFirst());
    }

    @Test
    public void execute_friendEdit_preservesMyselfAndInvalidProfileEditDoesNotChangeData() throws Exception {
        addFriend("Melody");
        Friend myself = model.getGameMates().getMyself();
        parser.parseCommand("edit 1 n/Joash").execute(model);
        assertEquals(myself, model.getGameMates().getMyself());

        GameMates before = new GameMates(model.getGameMates());
        assertThrows(ParseException.class, () -> parser.parseCommand("edit myself n/Alex e/invalid"));
        assertEquals(before, model.getGameMates());
        model.updateFilteredFriendList(friend -> false);
        parser.parseCommand("edit myself e/alex@example.com").execute(model);
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals("alex@example.com", model.getGameMates().getMyself().getEmail().value);
    }

    @Test
    public void execute_removeContacts_preservesNameGamesAndTagsAndSurvivesReload() throws Exception {
        Friend original = addFriend("Melody");
        CommandResult result = parser.parseCommand("edit 1 p/ e/").execute(model);
        Friend edited = model.getFilteredFriendList().getFirst();
        assertEquals("Edited Melody\n- removed phone number\n- removed email", result.getFeedbackToUser());
        assertEquals(Phone.EMPTY, edited.getPhone());
        assertEquals(Email.EMPTY, edited.getEmail());
        assertEquals(original.getName(), edited.getName());
        assertEquals(original.getGames(), edited.getGames());
        assertEquals(original.getTags(), edited.getTags());
        JsonGameMatesStorage storage = new JsonGameMatesStorage(testFolder.resolve("friends.json"));
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), storage.readGameMates().get());
        parser.parseCommand("edit 1 p/91234567 e/melody@example.com").execute(model);
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), storage.readGameMates().get());
    }

    @Test
    public void execute_partialEdit_onlyReportsChangesAndPreservesOmittedDetails() throws Exception {
        Friend original = addFriend("Melody");
        CommandResult result = parser.parseCommand("edit 1 n/  Melody Tan  p/91234567").execute(model);
        Friend edited = model.getFilteredFriendList().getFirst();
        assertEquals("Edited Melody Tan\n- changed name to Melody Tan\n- changed phone number to 91234567",
                result.getFeedbackToUser());
        assertEquals(original.getEmail(), edited.getEmail());
        assertEquals(original.getGames(), edited.getGames());
        assertEquals(original.getTags(), edited.getTags());
    }

    @Test
    public void execute_sameValue_doesNotReportUnchangedFields() throws Exception {
        Friend original = addFriend("Melody");
        CommandResult result = parser.parseCommand("edit 1 p/" + original.getPhone()).execute(model);
        assertEquals("Edited Melody", result.getFeedbackToUser());
        assertEquals(original, model.getFilteredFriendList().getFirst());
    }

    @Test
    public void execute_clearPhone_onlyRemovesPhone() throws Exception {
        Friend original = addFriend("Melody");
        CommandResult result = parser.parseCommand("edit 1 p/").execute(model);
        assertEquals("Edited Melody\n- removed phone number", result.getFeedbackToUser());
        assertTrue(model.getFilteredFriendList().getFirst().getPhone().isEmpty());
        assertEquals(original.getEmail(), model.getFilteredFriendList().getFirst().getEmail());
    }

    @Test
    public void execute_updateEmail_reportsNewValue() throws Exception {
        addFriend("Melody");
        CommandResult result = parser.parseCommand("edit 1 e/new@example.com").execute(model);
        assertEquals("Edited Melody\n- changed email to new@example.com", result.getFeedbackToUser());
    }

    @Test
    public void execute_existingTag_removesOnlyThatTagFromDisplayedFriend() throws Exception {
        Friend hidden = addFriend("Melody");
        Friend original = addFriend("Joash");
        Friend displayed = new FriendBuilder(original).withTags("friend", "teammate").build();
        model.setFriend(original, displayed);
        model.updateFilteredFriendList(friend -> friend.equals(displayed));

        CommandResult result = parser.parseCommand("edit 1 t/friend").execute(model);

        Friend expected = new FriendBuilder(displayed).withTags("teammate").build();
        assertEquals(expected, model.getGameMates().getFriendList().get(1));
        assertEquals(hidden, model.getGameMates().getFriendList().getFirst());
        assertEquals("Edited Joash\n- changed tags to teammate", result.getFeedbackToUser());
        JsonGameMatesStorage storage = new JsonGameMatesStorage(testFolder.resolve("tags.json"));
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), storage.readGameMates().get());
    }

    @Test
    public void execute_newTag_addsTagAndPreservesOtherDetails() throws Exception {
        Friend original = addFriend("Melody");

        CommandResult result = parser.parseCommand("edit 1 t/teammate").execute(model);

        assertEquals(new FriendBuilder(original).withTags("friend", "teammate").build(),
                model.getFilteredFriendList().getFirst());
        assertEquals("Edited Melody\n- changed tags to friend, teammate", result.getFeedbackToUser());
    }

    @Test
    public void execute_mixedRepeatedTags_addsAndRemovesEachTagOnce() throws Exception {
        Friend original = addFriend("Melody");
        Friend withTags = new FriendBuilder(original).withTags("friend", "teammate").build();
        model.setFriend(original, withTags);

        parser.parseCommand("edit 1 t/friend t/new t/friend t/new").execute(model);

        assertEquals(new FriendBuilder(withTags).withTags("teammate", "new").build(),
                model.getFilteredFriendList().getFirst());
    }

    @Test
    public void execute_lastExistingTag_removesAllTags() throws Exception {
        Friend original = addFriend("Melody");

        CommandResult result = parser.parseCommand("edit 1 t/friend").execute(model);

        assertEquals(new FriendBuilder(original).withTags().build(), model.getFilteredFriendList().getFirst());
        assertEquals("Edited Melody\n- removed tags", result.getFeedbackToUser());
    }

    @Test
    public void execute_tagWithDifferentCase_addsSeparateTag() throws Exception {
        Friend original = addFriend("Melody");

        parser.parseCommand("edit 1 t/Friend").execute(model);

        assertEquals(new FriendBuilder(original).withTags("friend", "Friend").build(),
                model.getFilteredFriendList().getFirst());
    }

    @Test
    public void execute_emptyTag_clearsAllTags() throws Exception {
        Friend original = addFriend("Melody");
        Friend withTags = new FriendBuilder(original).withTags("friend", "teammate").build();
        model.setFriend(original, withTags);

        CommandResult result = parser.parseCommand("edit 1 t/").execute(model);

        assertEquals(new FriendBuilder(withTags).withTags().build(), model.getFilteredFriendList().getFirst());
        assertEquals("Edited Melody\n- removed tags", result.getFeedbackToUser());
    }

    @Test
    public void execute_filteredIndex_editsDisplayedFriendAndPreservesOtherFriend() throws Exception {
        Friend hidden = addFriend("Melody");
        Friend displayed = addFriend("Joash");
        model.updateFilteredFriendList(friend -> friend.equals(displayed));
        parser.parseCommand("edit 1 e/").execute(model);
        assertEquals(hidden, model.getGameMates().getFriendList().getFirst());
        assertTrue(model.getGameMates().getFriendList().get(1).getEmail().isEmpty());
    }

    @Test
    public void execute_duplicateNameIgnoringCaseAndSpaces_doesNotChangeModel() throws Exception {
        addFriend("Melody Tan");
        Friend second = addFriend("Joash");
        model.updateFilteredFriendList(friend -> friend.equals(second));
        GameMates before = new GameMates(model.getGameMates());
        assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_FRIEND, ()
                -> parser.parseCommand("edit 1 n/melody   tan p/").execute(model));
        assertEquals(before, model.getGameMates());
    }

    @Test
    public void execute_caseOnlyRename_allowedAndReported() throws Exception {
        addFriend("Melody");
        CommandResult result = parser.parseCommand("edit 1 n/melody").execute(model);
        assertEquals("Edited melody\n- changed name to melody", result.getFeedbackToUser());
        assertEquals("melody", model.getFilteredFriendList().getFirst().getName().fullName);
    }

    @Test
    public void execute_emptyListAndOutOfRangeIndex_reportSpecificErrors() throws Exception {
        assertThrows(CommandException.class, EditCommand.MESSAGE_EMPTY_LIST, ()
                -> parser.parseCommand("edit 1 p/").execute(model));
        addFriend("Melody");
        assertThrows(CommandException.class, "No friend exists at index 2. Choose an index from 1 to 1.", ()
                -> parser.parseCommand("edit 2 p/").execute(model));
    }

    @Test
    public void execute_invalidDetails_doNotPartiallyEditFriend() {
        addFriend("Melody");
        GameMates before = new GameMates(model.getGameMates());
        assertThrows(ParseException.class, () -> parser.parseCommand("edit 1 n/Joash e/invalid"));
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> parser.parseCommand("edit 1 n/"));
        assertEquals(before, model.getGameMates());
    }

}
