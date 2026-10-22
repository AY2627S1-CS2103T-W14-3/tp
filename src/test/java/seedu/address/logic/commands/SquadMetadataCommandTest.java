package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.GameMates;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.GameName;
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
        assertEquals("Could not find a squad of 4 people.",
                command(4, "Role:Healer", "Region:Asia").execute(model).getFeedbackToUser());
        assertTrue(command(4, "Role:Healer", "Region:Asia").execute(model).isPreserveDetails());
        assertEquals(before, model.getGameMates());
        assertEquals(visible, model.getFilteredFriendList());
        model.updateFilteredFriendList(friend -> true);
        assertEquals(7, model.getFilteredFriendList().size());
    }

    @Test
    public void execute_sizeOne_meNeedsNeitherGameNorMetadata() throws Exception {
        assertEquals("Found squad of 1 people:\n1. Me", command(1, "Role:Healer").execute(model).getFeedbackToUser());
        model.setMyself(player("Own Profile", "Tank", "Europe"));
        assertEquals("Found squad of 1 people:\n1. Me (username: Amy123)",
                command(1, "Role:Healer").execute(model).getFeedbackToUser());
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
