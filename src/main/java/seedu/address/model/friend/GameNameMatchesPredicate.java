package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.game.GameName;

/**
 * Tests that a {@code Friend} has a game with the specified {@code GameName}.
 */
public class GameNameMatchesPredicate implements Predicate<Friend> {
    private final GameName gameName;

    public GameNameMatchesPredicate(GameName gameName) {
        this.gameName = requireNonNull(gameName);
    }

    @Override
    public boolean test(Friend friend) {
        return friend.getGames().stream()
                .anyMatch(game -> game.getGameName().equals(gameName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GameNameMatchesPredicate otherGameNameMatchesPredicate)) {
            return false;
        }

        return gameName.equals(otherGameNameMatchesPredicate.gameName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("gameName", gameName).toString();
    }
}
