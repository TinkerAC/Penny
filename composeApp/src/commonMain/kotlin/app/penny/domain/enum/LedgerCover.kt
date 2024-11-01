package app.penny.domain.enum

import org.jetbrains.compose.resources.DrawableResource
import penny.composeapp.generated.resources.Res
import penny.composeapp.generated.resources.ledger_cover_default

enum class LedgerCover(
    val coverName: String,
    val drawable: DrawableResource
) {


    DEFAULT("default", Res.drawable.ledger_cover_default),

    MARRIAGE("marriage", Res.drawable.ledger_cover_default),

    TRAVEL("travel", Res.drawable.ledger_cover_default),

    ENTERTAINMENT("entertainment", Res.drawable.ledger_cover_default),

    BABY("baby", Res.drawable.ledger_cover_default),

    //TODO("replace with actual drawables")

}