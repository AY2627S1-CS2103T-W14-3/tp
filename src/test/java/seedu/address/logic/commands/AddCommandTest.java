package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.ALICE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyGameMates;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_friendAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingFriendAdded modelStub = new ModelStubAcceptingFriendAdded();
        Friend validFriend = new FriendBuilder().build();

        CommandResult commandResult = new AddCommand(validFriend).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, validFriend.getName(),
                        validFriend.getPhone(), validFriend.getEmail()),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validFriend), modelStub.friendsAdded);
    }

    @Test
    public void execute_duplicateFriend_throwsCommandException() {
        Friend validFriend = new FriendBuilder().build();
        AddCommand addCommand = new AddCommand(validFriend);
        ModelStub modelStub = new ModelStubWithFriend(validFriend);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_FRIEND, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_nameDiffersOnlyInCaseAndWhitespace_throwsCommandException() {
        Friend existingFriend = new FriendBuilder().withName("Melody").build();
        Friend duplicateFriend = new FriendBuilder().withName("  melody  ").build();
        ModelStub modelStub = new ModelStubWithFriend(existingFriend);
        AddCommand addCommand = new AddCommand(duplicateFriend);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_FRIEND, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Friend alice = new FriendBuilder().withName("Alice").build();
        Friend bob = new FriendBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different friend -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addFriend(Friend friend) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGameMates(ReadOnlyGameMates newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyGameMates getGameMates() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setMyself(Friend myself) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasFriend(Friend friend) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteFriend(Friend target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setFriend(Friend target, Friend editedFriend) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Friend> getFilteredFriendList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredFriendList(Predicate<Friend> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single friend.
     */
    private class ModelStubWithFriend extends ModelStub {
        private final Friend friend;

        ModelStubWithFriend(Friend friend) {
            requireNonNull(friend);
            this.friend = friend;
        }

        @Override
        public boolean hasFriend(Friend friend) {
            requireNonNull(friend);
            return this.friend.isSameFriend(friend);
        }
    }

    /**
     * A Model stub that always accepts the friend being added.
     */
    private class ModelStubAcceptingFriendAdded extends ModelStub {
        final ArrayList<Friend> friendsAdded = new ArrayList<>();

        @Override
        public boolean hasFriend(Friend friend) {
            requireNonNull(friend);
            return friendsAdded.stream().anyMatch(friend::isSameFriend);
        }

        @Override
        public void addFriend(Friend friend) {
            requireNonNull(friend);
            friendsAdded.add(friend);
        }

        @Override
        public ReadOnlyGameMates getGameMates() {
            return new GameMates();
        }
    }

}
