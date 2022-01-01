package cluedo.tracker

interface Card

enum class Character : Card {
    COLONEL_MUSTARD,
    MISS_SCARLETT,
    MRS_PEACOCK,
    MRS_WHITE,
    PROFESSOR_PLUM,
    REV_GREEN
}

enum class Weapon : Card {
    CANDLESTICK,
    DAGGER,
    LEAD_PIPE,
    REVOLVER,
    ROPE,
    WRENCH
}

enum class Room : Card {
    BILLIARD_ROOM,
    CELLAR,
    CONSERVATORY,
    DINING_ROOM,
    HALL,
    KITCHEN,
    LIBRARY,
    LOUNGE,
    STUDY
}

class Suggestion(val character: Character, val weapon: Weapon, val room: Room) {
    val cards: Iterable<Card> = listOf(character, weapon, room)

    operator fun contains(card: Card) =
        card in cards
}

class Turn(val suggestion: Suggestion, val refutedBy: String?, var shown: Card?, val skippedBy: Iterable<String> = listOf())
