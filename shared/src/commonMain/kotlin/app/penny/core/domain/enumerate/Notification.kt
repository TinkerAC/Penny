package app.penny.core.domain.enumerate

import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource

sealed class Notification {

    abstract val displayText: StringResource
    abstract val description: StringResource
    abstract val helpText: StringResource

    data object ScheduledNotification : Notification() {
        override val displayText: StringResource = SharedRes.strings.notification_scheduled
        override val description: StringResource =
            SharedRes.strings.notification_scheduled_description
        override val helpText: StringResource = SharedRes.strings.notification_scheduled_help

    }

    data object BudgetReachedNotification : Notification() {
        override val displayText: StringResource = SharedRes.strings.notification_budget_reached
        override val description: StringResource =
            SharedRes.strings.notification_budget_reached_description
        override val helpText: StringResource = SharedRes.strings.notification_budget_reached_help
    }

}

