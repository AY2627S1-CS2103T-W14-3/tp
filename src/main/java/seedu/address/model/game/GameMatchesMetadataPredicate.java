package seedu.address.model.game;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.model.game.meta.Meta;

/** Matches the exact game name and every required metadata pair on that same entry. */
public final class GameMatchesMetadataPredicate implements Predicate<Game> {
    private final GameName gameName;
    private final Set<Meta> conditions;

    /** Copies the immutable pairs so changes to the caller's collection cannot change filtering. */
    public GameMatchesMetadataPredicate(GameName gameName, Set<Meta> conditions) {
        this.gameName = requireNonNull(gameName);
        this.conditions = Set.copyOf(conditions);
    }

    @Override
    public boolean test(Game game) {
        return game.getGameName().equals(gameName) && game.getMetas().containsAll(conditions);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof GameMatchesMetadataPredicate otherPredicate
                && gameName.equals(otherPredicate.gameName)
                && conditions.equals(otherPredicate.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName, conditions);
    }
}
