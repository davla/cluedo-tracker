package cluedo.tracker

import kotlin.collections.buildList
import kotlin.test.Test
import kotlin.test.assertEquals

class TrackerTest {
    companion object {
        private val allCards = buildList<Card>() {
            addAll(Character.values())
            addAll(Weapon.values())
            addAll(Room.values())
        }
        private val emptyState = allCards.associateWith { Uncertain() }
        private val initial = "Player 0" to listOf<Card>(Character.MISS_SCARLETT, Weapon.DAGGER, Room.HALL)
        private val initialState = emptyState + mapOf(
            Character.MISS_SCARLETT to Certain("Player 0"),
            Weapon.DAGGER to Certain("Player 0"),
            Room.HALL to Certain("Player 0")
        )
        private val players = List(7) { "Player $it" }
    }

    @Test
    fun shownTurn() {
        // Given
        val tracker = Tracker(players, initial)
        val turn = Turn(
            suggestion = Suggestion(Character.REV_GREEN, Weapon.ROPE, Room.CELLAR),
            refutedBy = "Player 1",
            shown = Character.REV_GREEN
        )

        // When
        tracker.addTurn(turn)

        // Then
        assertEquals(
            initialState + (Character.REV_GREEN to Certain("Player 1")),
            tracker.currentState
        )
    }

    @Test
    fun notShownTurn() {
        // Given
        val tracker = Tracker(players, initial)
        val turn = Turn(
            suggestion = Suggestion(Character.REV_GREEN, Weapon.ROPE, Room.CELLAR),
            refutedBy = "Player 1",
            shown = null
        )

        // When
        tracker.addTurn(turn)

        // Then
        assertEquals(
            initialState + mapOf(
                Character.REV_GREEN to Uncertain(possibleOwners = listOf("Player 1")),
                Weapon.ROPE to Uncertain(possibleOwners = listOf("Player 1")),
                Room.CELLAR to Uncertain(possibleOwners = listOf("Player 1"))
            ),
            tracker.currentState
        )
    }

    @Test
    fun skippedTurn() {
        // Given
        val tracker = Tracker(players, initial)
        val turn = Turn(
            suggestion = Suggestion(Character.REV_GREEN, Weapon.ROPE, Room.CELLAR),
            refutedBy = "Player 1",
            skippedBy = listOf("Player 2", "Player 4"),
            shown = null
        )

        // When
        tracker.addTurn(turn)

        // Then
        assertEquals(
            initialState + mapOf(
                Character.REV_GREEN to Uncertain(possibleOwners = listOf("Player 1"), notOwners = listOf("Player 2", "Player 4")),
                Weapon.ROPE to Uncertain(possibleOwners = listOf("Player 1"), notOwners = listOf("Player 2", "Player 4")),
                Room.CELLAR to Uncertain(possibleOwners = listOf("Player 1"), notOwners = listOf("Player 2", "Player 4"))
            ),
            tracker.currentState
        )
    }

    @Test
    fun unrefutedTurn() {
        // Given
        val tracker = Tracker(players, initial)
        val turn = Turn(
            suggestion = Suggestion(Character.REV_GREEN, Weapon.ROPE, Room.CELLAR),
            refutedBy = null,
            shown = null
        )

        // When
        tracker.addTurn(turn)

        // Then
        assertEquals(
            initialState + mapOf(
                Character.REV_GREEN to Uncertain(possibleOwners = listOf()),
                Weapon.ROPE to Uncertain(possibleOwners = listOf()),
                Room.CELLAR to Uncertain(possibleOwners = listOf())
            ),
            tracker.currentState
        )
    }
}
