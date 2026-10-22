package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;
import seedu.address.model.friend.NameContainsKeywordsPredicate;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameMatesBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new GameMates(), new GameMates(modelManager.getGameMates()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new GameMates(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasFriend_nullFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasFriend(null));
    }

    @Test
    public void hasFriend_friendNotInGameMates_returnsFalse() {
        assertFalse(modelManager.hasFriend(ALICE));
    }

    @Test
    public void hasFriend_friendInGameMates_returnsTrue() {
        modelManager.addFriend(ALICE);
        assertTrue(modelManager.hasFriend(ALICE));
    }

    @Test
    public void resolveFriend_numericIndex_usesDisplayedList() {
        assertThrows(IndexOutOfBoundsException.class, () -> modelManager.resolveFriend(Index.fromOneBased(1)));
        modelManager.addFriend(ALICE);
        modelManager.addFriend(BENSON);
        modelManager.updateFilteredFriendList(friend -> friend.equals(BENSON));

        FriendTarget target = modelManager.resolveFriend(Index.fromOneBased(1));
        assertEquals(BENSON, target.friend());
        assertFalse(target.isMyself());
        assertThrows(IndexOutOfBoundsException.class, () -> modelManager.resolveFriend(Index.fromOneBased(2)));
    }

    @Test
    public void setFriend_targetAfterFilterChanges_updatesOriginallyResolvedFriend() {
        modelManager.addFriend(ALICE);
        modelManager.addFriend(BENSON);
        Index index = Index.fromOneBased(1);
        FriendTarget target = modelManager.resolveFriend(index);
        Friend edited = new FriendBuilder(target.friend()).withPhone("91234567").build();
        FriendTarget myself = modelManager.resolveFriend(Index.myself());
        modelManager.updateFilteredFriendList(friend -> friend.equals(BENSON));

        modelManager.setFriend(target, edited);

        assertEquals(List.of(edited, BENSON), modelManager.getGameMates().getFriendList());
        assertEquals(List.of(BENSON), modelManager.getFilteredFriendList());
        assertEquals(myself, modelManager.resolveFriend(Index.myself()));
    }

    @Test
    public void setFriend_myself_preservesSameNamedFriendAndFilter() {
        modelManager.setFriend(modelManager.resolveFriend(Index.myself()), ALICE);
        modelManager.addFriend(ALICE);
        FriendTarget target = modelManager.resolveFriend(Index.myself());
        assertTrue(target.isMyself());
        modelManager.updateFilteredFriendList(friend -> false);
        Friend edited = new FriendBuilder(ALICE).withPhone("91234567").build();

        modelManager.setFriend(target, edited);

        assertEquals(edited, modelManager.resolveFriend(Index.myself()).friend());
        assertEquals(List.of(ALICE), modelManager.getGameMates().getFriendList());
        assertTrue(modelManager.getFilteredFriendList().isEmpty());
    }

    @Test
    public void friendTarget_nullArguments_rejected() {
        assertThrows(NullPointerException.class, () -> modelManager.resolveFriend(null));
        assertThrows(NullPointerException.class, () -> modelManager.setFriend((FriendTarget) null, BENSON));
        FriendTarget target = modelManager.resolveFriend(Index.myself());
        assertThrows(NullPointerException.class, () -> modelManager.setFriend(target, null));
    }

    @Test
    public void getFilteredFriendList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredFriendList().remove(0));
    }

    @Test
    public void equals_emptyListsWithDifferentScopes_distinguishesUserVisibleState() {
        ModelManager active = new ModelManager();
        ModelManager archived = new ModelManager();
        ModelManager search = new ModelManager();
        archived.updateArchivedFriendList(PREDICATE_SHOW_ALL_FRIENDS);
        search.updateSearchFriendList(PREDICATE_SHOW_ALL_FRIENDS);
        assertEquals(active.getFilteredFriendList(), archived.getFilteredFriendList());
        assertEquals(active.getFilteredFriendList(), search.getFilteredFriendList());
        assertFalse(active.equals(archived));
        assertFalse(archived.equals(search));
        assertFalse(search.equals(active));
        active.updateSearchFriendList(PREDICATE_SHOW_ALL_FRIENDS);
        assertEquals(active, search);
    }

    @Test
    public void equals() {
        GameMates gameMates = new GameMatesBuilder().withFriend(ALICE).withFriend(BENSON).build();
        GameMates differentGameMates = new GameMates();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(gameMates, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(gameMates, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different gameMates -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentGameMates, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredFriendList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(gameMates, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredFriendList(PREDICATE_SHOW_ALL_FRIENDS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(gameMates, differentUserPrefs)));
    }
}
