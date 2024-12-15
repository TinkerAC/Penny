package app.penny.core.domain.enum

import org.jetbrains.compose.resources.DrawableResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.ledger_cover_default
import penny.shared.generated.resources.ledger_cover_marriage
import penny.shared.generated.resources.ledger_cover_travel
import penny.shared.generated.resources.ledger_cover_entertainment
import penny.shared.generated.resources.ledger_cover_baby
enum class LedgerCover(
    val coverName: String,
    val drawable: DrawableResource

) {

    DEFAULT("default", Res.drawable.ledger_cover_default),

    MARRIAGE("marriage", Res.drawable.ledger_cover_marriage),

    TRAVEL("travel", Res.drawable.ledger_cover_travel),

    ENTERTAINMENT("entertainment", Res.drawable.ledger_cover_entertainment),

    BABY("baby", Res.drawable.ledger_cover_baby),


}