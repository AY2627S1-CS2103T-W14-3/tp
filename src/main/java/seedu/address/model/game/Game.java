package seedu.address.model.game;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;

/**
 * Represents a game entry in GameMates.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Game {

    // Identity field
    private final GameName gameName;

    // Data field
    private final Username username;
    private final Set<Meta> metas;

    /**
     * Every field must be present and not null.
     */
    public Game(GameName gameName, Username username) {
        this(gameName, username, Collections.emptySet());
    }

    /**
     * Constructs a game entry with metadata. Keys must be unique after trimming, ignoring case.
     * The collection is copied to preserve immutability and checked before any pairs are discarded.
     */
    public Game(GameName gameName, Username username, Collection<Meta> metas) {
        requireAllNonNull(gameName, username, metas);
        Set<MetaKey> keys = new HashSet<>();
        Set<Meta> copy = new LinkedHashSet<>();
        for (Meta meta : metas) {
            requireAllNonNull(meta);
            checkArgument(keys.add(meta.getKey()), Meta.MESSAGE_DUPLICATE_KEY);
            copy.add(meta);
        }
        this.gameName = gameName;
        this.username = username;
        this.metas = Collections.unmodifiableSet(copy);
    }

    public GameName getGameName() {
        return gameName;
    }

    public Username getUsername() {
        return username;
    }

    public Set<Meta> getMetas() {
        return metas;
    }

    /**
     * Returns true if both game entries refer to the same game.
     * This defines a weaker notion of equality between two games.
     */
    public boolean isSameGame(Game otherGame) {
        if (otherGame == this) {
            return true;
        }

        return otherGame != null
                && otherGame.getGameName().equals(getGameName());
    }

    /**
     * Returns true if both game entries have the same fields.
     * This defines a stronger notion of equality between two games.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Game otherGame)) {
            return false;
        }

        return gameName.equals(otherGame.gameName)
                && username.equals(otherGame.username)
                && metas.equals(otherGame.metas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName, username, metas);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("gameName", gameName)
                .add("username", username)
                .add("metas", metas)
                .toString();
    }
}
