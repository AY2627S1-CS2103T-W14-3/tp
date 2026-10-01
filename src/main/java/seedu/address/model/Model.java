package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.friend.Friend;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Friend> PREDICATE_SHOW_ALL_FRIENDS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces GameMates data with the data in {@code gameMates}.
     */
    void setGameMates(ReadOnlyGameMates gameMates);

    /** Returns the GameMates */
    ReadOnlyGameMates getGameMates();

    /**
     * Returns true if a friend with the same identity as {@code friend} exists in GameMates.
     */
    boolean hasFriend(Friend friend);

    /**
     * Deletes the given friend.
     * The friend must exist in GameMates.
     */
    void deleteFriend(Friend target);

    /**
     * Adds the given friend.
     * {@code friend} must not already exist in GameMates.
     */
    void addFriend(Friend friend);

    /**
     * Replaces the given friend {@code target} with {@code editedFriend}.
     * {@code target} must exist in GameMates.
     * The friend identity of {@code editedFriend} must not be the same as another existing friend
     * in GameMates.
     */
    void setFriend(Friend target, Friend editedFriend);

    /** Returns an unmodifiable view of the filtered friend list */
    ObservableList<Friend> getFilteredFriendList();

    /**
     * Updates the filter of the filtered friend list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredFriendList(Predicate<Friend> predicate);
}
