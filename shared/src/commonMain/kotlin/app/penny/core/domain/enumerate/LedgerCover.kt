package app.penny.core.domain.enumerate

import app.penny.shared.SharedRes
import dev.icerock.moko.resources.ImageResource

enum class LedgerCover(
    val coverName: String,
    val drawable: ImageResource

) {

    DEFAULT("default", SharedRes.images.ledger_cover_default),

    MARRIAGE("marriage", SharedRes.images.ledger_cover_marriage),

    TRAVEL("travel", SharedRes.images.ledger_cover_travel),

    ENTERTAINMENT("entertainment", SharedRes.images.ledger_cover_entertainment),

    BABY("baby", SharedRes.images.ledger_cover_baby),


}