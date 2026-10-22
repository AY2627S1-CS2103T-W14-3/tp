package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.FriendListScope;
import seedu.address.model.GameMates;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendSortCriterion;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class SquadMetadataCommandTest {
    private final ModelManager model = new ModelManager();
    private final GameName game = new GameName("Valorant");

    @Test
    public void execute_multipleConditions_checksSameGameBeforeAvailabilityAndPreservesState() throws Exception {
        Friend hidden = player("Hidden", "Healer", "Asia");
        Friend alpha = player("Alpha", "Healer", "Asia");
        Friend partial = new FriendBuilder().withName("Partial").withGames(
                new GameBuilder().withGameName("Valorant").addMeta("Role", "Healer").build(),
                new GameBuilder().withGameName("Minecraft").addMeta("Region", "Asia").build()).build();
        Friend beta = player("Beta", "Healer", "Asia");
        for (Friend friend : List.of(hidden, alpha, partial, beta, player("Wrong Case", "healer", "Asia"),
                player("Wrong Region", "Healer", "Europe"), new FriendBuilder().withName("Missing").withGames(
                        new GameBuilder().withGameName("Valorant").build()).build())) {
            model.addFriend(friend);
        }
        model.setMyself(player("Own Profile", "Tank", "Europe"));
        model.updateFilteredFriendList(friend -> !friend.equals(hidden));
        GameMates before = new GameMates(model.getGameMates());
        List<Friend> visible = new ArrayList<>(model.getFilteredFriendList());
        CommandResult result = command(3, "role:Healer", "REGION:Asia").execute(model);
        assertEquals("Found squad of 3 people:\n1. Me (username: Amy123)"
                + "\n2. Alpha (username: Amy123, index: 1)\n3. Beta (username: Amy123, index: 3)",
                result.getFeedbackToUser());
        assertTrue(result.isPreserveDetails());
        assertTrue(result.getFriendToShow().isEmpty());
        assertThrows(CommandException.class, "Could not find a squad of 4 people.", () ->
                command(4, "Role:Healer", "Region:Asia").execute(model));
        assertEquals(before, model.getGameMates());
        assertEquals(visible, model.getFilteredFriendList());
        model.updateFilteredFriendList(friend -> true);
        assertEquals(7, model.getFilteredFriendList().size());
    }

    @Test
    public void execute_sortedScopes_preservesIndicesPinArchiveStatusesAndState() throws Exception {
        for (FriendListScope scope : FriendListScope.values()) {
            ModelManager scoped = new ModelManager();
            Friend hidden = player("Hidden", "Healer", "Asia").withArchived(scope == FriendListScope.ARCHIVED);
            Friend alpha = player("Alpha", "Healer", "Asia").withPinned(true)
                    .withArchived(scope != FriendListScope.ACTIVE);
            Friend beta = player("Beta", "Healer", "Asia").withArchived(scope == FriendListScope.ARCHIVED);
            Friend wrongRole = player("Wrong Role", "Tank", "Asia").withArchived(scope == FriendListScope.ARCHIVED);
            for (Friend friend : List.of(hidden, alpha, wrongRole, beta)) {
                scoped.addFriend(friend);
            }
            switch (scope) {
                case ACTIVE -> scoped.updateFilteredFriendList(friend -> !friend.equals(hidden));
                case ARCHIVED -> scoped.updateArchivedFriendList(friend -> !friend.equals(hidden));
                case SEARCH -> scoped.updateSearchFriendList(friend -> !friend.equals(hidden));
                default -> throw new AssertionError("Unhandled scope: " + scope);
            }
            scoped.sortFilteredFriendList(FriendSortCriterion.NAME, true);
            GameMates before = new GameMates(scoped.getGameMates());
            List<Friend> displayed = List.copyOf(scoped.getFilteredFriendList());
            CommandResult result = command(3, "Role:Healer", "Region:Asia").execute(scoped);
            assertEquals("Found squad of 3 people:\n1. Me\n2. Beta (username: Amy123, index: 2)"
                    + "\n3. Alpha (username: Amy123, index: 3)", result.getFeedbackToUser());
            assertTrue(result.isPreserveDetails());
            assertThrows(CommandException.class, "Could not find a squad of 4 people.", () ->
                    command(4, "Role:Healer", "Region:Asia").execute(scoped));
            assertTrue(command(4).execute(scoped).getFeedbackToUser().contains("Wrong Role"));
            assertEquals(before, scoped.getGameMates());
            assertEquals(displayed, scoped.getFilteredFriendList());
            assertEquals(scope, scoped.getFriendListScope());
        }
    }

    @Test
    public void execute_sizeOne_meNeedsNeitherGameNorMetadata() throws Exception {
        assertEquals("Found squad of 1 person:\n1. Me", command(1, "Role:Healer").execute(model).getFeedbackToUser());
        model.setMyself(player("Own Profile", "Tank", "Europe"));
        assertEquals("Found squad of 1 person:\n1. Me (username: Amy123)",
                command(1, "Role:Healer").execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_insufficientAfterFiltering_throwsBeforeSamplingAndPreservesState() throws Exception {
        model.addFriend(player("Tank", "Tank", "Asia"));
        model.addFriend(player("Healer", "Healer", "Europe"));
        GameMates before = new GameMates(model.getGameMates());
        List<Friend> displayed = new ArrayList<>(model.getFilteredFriendList());
        Random unused = new Random(0) {
            @Override
            public int nextInt(int bound) {
                throw new AssertionError("Insufficient candidates must fail before sampling");
            }
        };
        SquadCommand filtered = new SquadCommand(game, 2,
                ParserUtil.parseMetas(List.of("Role:Healer", "Region:Asia")), unused);
        assertThrows(CommandException.class, "Could not find a squad of 2 people.", () -> filtered.execute(model));
        assertEquals(before, model.getGameMates());
        assertEquals(displayed, model.getFilteredFriendList());
        // Both game players remain eligible without metadata conditions.
        assertTrue(command(3).execute(model).getFeedbackToUser().startsWith("Found squad of 3 people:"));
    }

    @Test
    public void execute_optionalUsernames_omitsEmptyUsernameForMeAndSelectedFriend() throws Exception {
        Game withoutUsername = new Game(game, Username.EMPTY, ParserUtil.parseMetas(List.of("Role:Healer")));
        model.addFriend(new FriendBuilder().withName("Healer").withGames(withoutUsername).build());
        model.setMyself(new FriendBuilder().withName("Own Profile").withGames(withoutUsername).build());
        assertEquals("Found squad of 2 people:\n1. Me\n2. Healer (index: 1)",
                command(2, "Role:Healer").execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_seededSampling_distinctEligibleSubsetInDisplayedOrder() throws Exception {
        for (int i = 1; i <= 8; i++) {
            model.addFriend(player("Player " + i, i % 2 == 0 ? "Healer" : "Tank", "Asia"));
        }
        Random random = new Random(0) {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
        Set<Meta> pairs = ParserUtil.parseMetas(Set.of("Role:Healer"));
        String result = new SquadCommand(game, 3, pairs, random).execute(model).getFeedbackToUser();
        assertEquals("Found squad of 3 people:\n1. Me\n2. Player 4 (username: Amy123, index: 4)"
                + "\n3. Player 6 (username: Amy123, index: 6)", result);
        assertEquals(command(3, "Role:Healer").execute(model), command(3, "Role:Healer").execute(model));
        assertNotEquals(command(3, "Role:Healer"), command(3, "Role:Tank"));
        assertEquals(new SquadCommand(game, 3, new Random(0)).execute(model), command(3).execute(model));
    }

    private SquadCommand command(int size, String... pairs) throws Exception {
        return new SquadCommand(game, size, ParserUtil.parseMetas(List.of(pairs)), new Random(0));
    }

    private Friend player(String name, String role, String region) {
        return new FriendBuilder().withName(name).withGames(new GameBuilder().withGameName("VALORANT")
                .addMeta("Role", role).addMeta("Region", region).build()).build();
    }
}
