package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.friend.Friend;

/**
 * Represents the in-memory model of the GameMates data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final GameMates gameMates;
    private final UserPrefs userPrefs;
    private final FilteredList<Friend> filteredFriends;

    /**
     * Initializes a ModelManager with the given gameMates and userPrefs.
     */
    public ModelManager(ReadOnlyGameMates gameMates, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(gameMates, userPrefs);

        logger.fine("Initializing with GameMates: " + gameMates + " and user prefs " + userPrefs);

        this.gameMates = new GameMates(gameMates);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredFriends = new FilteredList<>(this.gameMates.getFriendList());
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
    public boolean hasFriend(Friend friend) {
        requireNonNull(friend);
        return gameMates.hasFriend(friend);
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

    //=========== Filtered Friend List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Friend} backed by the internal list of
     * {@code gameMates}
     */
    @Override
    public ObservableList<Friend> getFilteredFriendList() {
        return filteredFriends;
    }

    @Override
    public void updateFilteredFriendList(Predicate<Friend> predicate) {
        requireNonNull(predicate);
        filteredFriends.setPredicate(predicate);
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
                && filteredFriends.equals(otherModelManager.filteredFriends);
    }

}
