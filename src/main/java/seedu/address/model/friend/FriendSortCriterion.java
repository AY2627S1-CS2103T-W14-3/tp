package seedu.address.model.friend;

import java.util.Comparator;

/**
 * The supported ways to order friends in a displayed list.
 */
public enum FriendSortCriterion {
    NAME,
    GAME;

    static final Comparator<Friend> PINNED_FRIENDS_FIRST = Comparator.comparing(Friend::isPinned)
            .reversed();
    private static final Comparator<Friend> NAME_COMPARATOR = Comparator.comparing(
            friend -> friend.getName().fullName, String.CASE_INSENSITIVE_ORDER);
    private static final Comparator<Friend> GAME_COMPARATOR = Comparator.comparing(
            FriendSortCriterion::getFirstGameName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
            .thenComparing(NAME_COMPARATOR);
    private static final Comparator<Friend> DESCENDING_GAME_COMPARATOR = Comparator.comparing(
            FriendSortCriterion::getFirstGameName,
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()))
            .thenComparing(NAME_COMPARATOR.reversed());

    /**
     * Returns the comparator that places pinned friends first, then orders friends within each group according to
     * this criterion in the requested direction.
     *
     * <p>Friends without games always appear after friends with games when sorting by game. Friends with more than
     * one game are ordered by their alphabetically first game name. Name is used to break game-name ties.</p>
     */
    public Comparator<Friend> comparator(boolean isDescending) {
        Comparator<Friend> criterionComparator = switch (this) {
            case NAME -> isDescending ? NAME_COMPARATOR.reversed() : NAME_COMPARATOR;
            case GAME -> isDescending ? DESCENDING_GAME_COMPARATOR : GAME_COMPARATOR;
        };
        return PINNED_FRIENDS_FIRST.thenComparing(criterionComparator);
    }

    private static String getFirstGameName(Friend friend) {
        return friend.getGames().stream()
                .map(game -> game.getGameName().value)
                .min(String.CASE_INSENSITIVE_ORDER)
                .orElse(null);
    }
}
