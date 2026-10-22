package seedu.address.model;

import java.util.function.Predicate;

import io.vavr.control.Option;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;

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

    /** Replaces the user profile independently of the friend list. */
    void setMyself(Friend myself);

    /** Returns the GameMates */
    ReadOnlyGameMates getGameMates();

    /**
     * Resolves the given displayed index to a friend target, or selects the separately stored Myself
     * profile when {@code index} is {@link Index#myself()}. The target retains the selected friend
     * and storage location even if the displayed list changes afterward.
     *
     * @throws IndexOutOfBoundsException if a numeric index is outside the filtered friend list.
     * @throws NullPointerException if {@code index} is null.
     */
    FriendTarget resolveFriend(Index index);

    /**
     * Returns true if a friend with the same identity as {@code friend} exists in GameMates.
     */
    boolean hasFriend(Friend friend);

    /** Returns the stored active or archived friend with the same identity, regardless of display filters. */
    Option<Friend> findFriend(Friend friend);

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

    /**
     * Replaces the profile or friend selected by a previously resolved {@code target}.
     * Does not resolve a displayed index again, so filter changes cannot redirect the update.
     * The same requirements as
     * {@link #setFriend(Friend, Friend)} apply when updating a friend.
     *
     * @throws NullPointerException if any argument is null.
     */
    void setFriend(FriendTarget target, Friend editedFriend);

    /** Returns an unmodifiable view of the filtered friend list */
    ObservableList<Friend> getFilteredFriendList();

    /**
     * Shows active friends matching the given {@code predicate}, leaving the archive view.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredFriendList(Predicate<Friend> predicate);

    /** Shows archived friends matching the given predicate. */
    void updateArchivedFriendList(Predicate<Friend> predicate);

    /** Shows matching friends from both active and archived lists as search results. */
    void updateSearchFriendList(Predicate<Friend> predicate);

    /** Returns the scope of the displayed list, including when it is empty. */
    FriendListScope getFriendListScope();
}
