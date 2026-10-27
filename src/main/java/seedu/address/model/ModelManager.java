package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import io.vavr.control.Option;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendSortCriterion;
import seedu.address.model.friend.FriendTarget;

/**
 * Represents the in-memory model of the GameMates data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);
    private static final Comparator<Friend> PINNED_FRIENDS_FIRST = Comparator.comparing(Friend::isPinned)
            .reversed();

    private final GameMates gameMates;
    private final UserPrefs userPrefs;
    private final FilteredList<Friend> filteredFriends;
    private FriendListScope friendListScope = FriendListScope.ACTIVE;
    private final SortedList<Friend> sortedFriends;

    /**
     * Initializes a ModelManager with the given gameMates and userPrefs.
     */
    public ModelManager(ReadOnlyGameMates gameMates, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(gameMates, userPrefs);

        logger.fine("Initializing with GameMates: " + gameMates + " and user prefs " + userPrefs);

        this.gameMates = new GameMates(gameMates);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredFriends = new FilteredList<>(this.gameMates.getFriendList(), friend -> !friend.isArchived());
        sortedFriends = new SortedList<>(filteredFriends);
    }

    public ModelManager() {
        this(new GameMates(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== GameMates ================================================================================

    @Override
    public void setGameMates(ReadOnlyGameMates gameMates) {
        this.gameMates.resetData(gameMates);
    }

    @Override
    public ReadOnlyGameMates getGameMates() {
        return gameMates;
    }

    @Override
    public void setMyself(Friend myself) {
        gameMates.setMyself(myself);
    }

    @Override
    public FriendTarget resolveFriend(Index index) {
        requireNonNull(index);
        return index.isMyself() ? new FriendTarget.Myself(gameMates.getMyself())
                : new FriendTarget.ListedFriend(sortedFriends.get(index.getZeroBased()));
    }

    @Override
    public boolean hasFriend(Friend friend) {
        requireNonNull(friend);
        return gameMates.hasFriend(friend);
    }

    @Override
    public Option<Friend> findFriend(Friend friend) {
        return gameMates.findFriend(friend);
    }

    @Override
    public void deleteFriend(Friend target) {
        gameMates.removeFriend(target);
    }

    @Override
    public void addFriend(Friend friend) {
        gameMates.addFriend(friend);
        updateFilteredFriendList(PREDICATE_SHOW_ALL_FRIENDS);
    }

    @Override
    public void setFriend(Friend target, Friend editedFriend) {
        requireAllNonNull(target, editedFriend);

        gameMates.setFriend(target, editedFriend);
    }

    @Override
    public void setFriend(FriendTarget target, Friend editedFriend) {
        requireAllNonNull(target, editedFriend);
        switch (target) {
            case FriendTarget.Myself _ -> setMyself(editedFriend);
            case FriendTarget.ListedFriend listed -> setFriend(listed.friend(), editedFriend);
        }
    }

    //=========== Filtered Friend List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Friend} backed by the internal list of
     * {@code gameMates}
     */
    @Override
    public ObservableList<Friend> getFilteredFriendList() {
        return sortedFriends;
    }

    @Override
    public void updateFilteredFriendList(Predicate<Friend> predicate) {
        requireNonNull(predicate);
        friendListScope = FriendListScope.ACTIVE;
        filteredFriends.setPredicate(friend -> !friend.isArchived() && predicate.test(friend));
    }

    @Override
    public void updateArchivedFriendList(Predicate<Friend> predicate) {
        requireNonNull(predicate);
        friendListScope = FriendListScope.ARCHIVED;
        filteredFriends.setPredicate(friend -> friend.isArchived() && predicate.test(friend));
    }

    @Override
    public void updateSearchFriendList(Predicate<Friend> predicate) {
        requireNonNull(predicate);
        friendListScope = FriendListScope.SEARCH;
        filteredFriends.setPredicate(predicate);
    }

    @Override
    public FriendListScope getFriendListScope() {
        return friendListScope;
    }

    @Override
    public void sortFilteredFriendList(FriendSortCriterion sortCriterion, boolean isDescending) {
        requireNonNull(sortCriterion);
        Comparator<Friend> sortComparator = isDescending
                ? sortCriterion.comparator().reversed() : sortCriterion.comparator();
        sortedFriends.setComparator(PINNED_FRIENDS_FIRST.thenComparing(sortComparator));
    }

    @Override
    public void clearFriendListSort() {
        sortedFriends.setComparator(null);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return gameMates.equals(otherModelManager.gameMates)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredFriends.equals(otherModelManager.filteredFriends)
                && friendListScope == otherModelManager.friendListScope;
    }

}
