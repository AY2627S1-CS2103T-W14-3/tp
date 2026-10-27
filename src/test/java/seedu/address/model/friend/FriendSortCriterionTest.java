package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.testutil.FriendBuilder;

public class FriendSortCriterionTest {

    @Test
    public void nameComparator_sortsNamesIgnoringCase() {
        Friend zoe = new FriendBuilder().withName("Zoe").build();
        Friend alex = new FriendBuilder().withName("alex").build();

        List<Friend> friends = List.of(zoe, alex).stream().sorted(FriendSortCriterion.NAME.comparator()).toList();

        assertEquals(List.of(alex, zoe), friends);
    }

    @Test
    public void gameComparator_sortsByFirstGameThenNameAndPutsFriendsWithoutGamesLast() {
        Friend zelda = new FriendBuilder().withName("Zelda").withGames(
                new Game(new GameName("Zelda"), new Username("zeldaPlayer"))).build();
        Friend anna = new FriendBuilder().withName("Anna").withGames(
                new Game(new GameName("Minecraft"), new Username("miner")),
                new Game(new GameName("Animal Crossing"), new Username("islander"))).build();
        Friend andy = new FriendBuilder().withName("Andy").withGames(
                new Game(new GameName("animal crossing"), new Username("anotherIslander"))).build();
        Friend noGames = new FriendBuilder().withName("No Games").build();

        List<Friend> friends = List.of(zelda, anna, noGames, andy).stream()
                .sorted(FriendSortCriterion.GAME.comparator()).toList();

        assertEquals(List.of(andy, anna, zelda, noGames), friends);
    }

    @Test
    public void descendingGameComparator_sortsByFirstGameThenNameAndPutsFriendsWithoutGamesLast() {
        Friend zelda = new FriendBuilder().withName("Zelda").withGames(
                new Game(new GameName("Zelda"), new Username("zeldaPlayer"))).build();
        Friend anna = new FriendBuilder().withName("Anna").withGames(
                new Game(new GameName("Minecraft"), new Username("miner")),
                new Game(new GameName("Animal Crossing"), new Username("islander"))).build();
        Friend andy = new FriendBuilder().withName("Andy").withGames(
                new Game(new GameName("animal crossing"), new Username("anotherIslander"))).build();
        Friend noGames = new FriendBuilder().withName("No Games").build();

        List<Friend> friends = List.of(zelda, anna, noGames, andy).stream()
                .sorted(FriendSortCriterion.GAME.comparator(true)).toList();

        assertEquals(List.of(zelda, anna, andy, noGames), friends);
    }
}
