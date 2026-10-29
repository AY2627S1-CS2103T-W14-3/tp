package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.Messages.MESSAGE_FRIENDS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalFriends.CARL;
import static seedu.address.testutil.TypicalFriends.ELLE;
import static seedu.address.testutil.TypicalFriends.FIONA;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private final Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalGameMates(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertEquals(findFirstCommandCopy, findFirstCommand);

        // different types -> returns false
        assertNotEquals(1, findFirstCommand);

        // null -> returns false
        assertNotEquals(null, findFirstCommand);

        // different friend -> returns false
        assertNotEquals(findSecondCommand, findFirstCommand);
    }

    @Test
    public void execute_zeroKeywords_noFriendFound() {
        String expectedMessage = String.format(MESSAGE_FRIENDS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateSearchFriendList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredFriendList());
    }

    @Test
    public void execute_multipleKeywords_multipleFriendsFound() {
        String expectedMessage = String.format(MESSAGE_FRIENDS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateSearchFriendList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredFriendList());
    }

    @Test
    public void execute_filteredDisplay_doesNotIncludeHiddenMatches() {
        model.updateFilteredFriendList(friend -> friend.equals(CARL) || friend.equals(ELLE));

        new FindCommand(preparePredicate("Kurz Elle Kunz")).execute(model);

        assertEquals(List.of(CARL, ELLE), model.getFilteredFriendList());
    }

    @Test
    public void execute_repeatedSearch_onlyNarrowsPreviousResults() {
        new FindCommand(preparePredicate("Carl Elle")).execute(model);
        new FindCommand(preparePredicate("Elle Kunz")).execute(model);
        assertEquals(List.of(ELLE), model.getFilteredFriendList());

        new FindCommand(preparePredicate("Nobody")).execute(model);
        new FindCommand(preparePredicate("Carl")).execute(model);
        assertEquals(List.of(), model.getFilteredFriendList());
    }

    @Test
    public void execute_activeAndArchivedViews_searchesOnlyDisplayedStatus() {
        model.setFriend(CARL, CARL.withArchived(true));
        new FindCommand(preparePredicate("Carl Elle")).execute(model);
        assertEquals(List.of(ELLE), model.getFilteredFriendList());

        model.updateArchivedFriendList(friend -> true);
        new FindCommand(preparePredicate("Carl Elle")).execute(model);
        assertEquals(List.of(CARL.withArchived(true)), model.getFilteredFriendList());
    }

    @Test
    public void execute_oneFriendFound_returnsFriendToShow() {
        NameContainsKeywordsPredicate predicate = preparePredicate("Carl");
        FindCommand command = new FindCommand(predicate);

        CommandResult result = command.execute(model);

        assertEquals(CARL, result.getFriendToShow().orElseThrow());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
