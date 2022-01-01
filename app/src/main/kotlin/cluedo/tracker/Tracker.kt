package cluedo.tracker

// TODO: cards per player

sealed class CardOwnership
data class Certain(val owner: String) : CardOwnership()
data class Uncertain(val possibleOwners: Iterable<String> = listOf(), val notOwners: Iterable<String> = listOf()) : CardOwnership()

private val emptyState: Map<Card, CardOwnership> = (Character.values().toList() + Weapon.values() + Room.values())
    .associateWith { Uncertain() }

class Tracker(val players: Iterable<String>, initialCards: Pair<String, Iterable<Card>>) {
    private val turns = initialCards.second.map {
        Turn(
            suggestion = Suggestion(Character.MISS_SCARLETT, Weapon.DAGGER, Room.CELLAR),
            refutedBy = initialCards.first,
            shown = it
        )
    }.toMutableList()

    fun addTurn(turn: Turn) {
        turns.add(turn)

        do {
            val hasResolved = turns.map { it.resolve() }.any { it }
        } while (hasResolved)
    }

    val currentState: Map<Card, CardOwnership>
        get() = emptyState.mapValues { it.key.ownership }

    private val Card.owner
        get() = turns.find { it.isResolved && it.shown == this }?.refutedBy

    private val Card.ownership
        get() = if (owner != null) {
            Certain(owner!!)
        } else {
            val possibleOwners = turns.filter { !it.isResolved && this in it.suggestion }
                .map { it.refutedBy }.filterNotNull()
                .distinct()
            val notOwners = turns.filter { this in it.suggestion }
                .flatMap { it.skippedBy }
                .distinct()
            Uncertain(possibleOwners, notOwners)
        }

    private val Turn.isResolved
        get() = shown != null || refutedBy == null

    private fun Turn.resolve(): Boolean {
        if (isResolved) {
            return false
        }

        val refuterOwnsACard = suggestion.cards.any { it.owner == refutedBy }
        if (refuterOwnsACard) {
            return false
        }

        val shownCandidates = suggestion.cards.filter {
            when (val ownership = it.ownership) {
                is Certain -> false
                is Uncertain ->
                    refutedBy in ownership.possibleOwners &&
                        refutedBy !in ownership.notOwners
            }
        }
        shown = shownCandidates.singleOrNull()
        return shown != null
    }
}
