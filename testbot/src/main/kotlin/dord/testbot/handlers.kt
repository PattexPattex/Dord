package dord.testbot

import com.pattexpattex.dord.Dord
import com.pattexpattex.dord.options.Resolvers
import dev.minn.jda.ktx.messages.MessageCreate
import dev.minn.jda.ktx.messages.into
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.Command
import dev.minn.jda.ktx.interactions.components.button as buttonKTX

fun Dord.setupHandlers() = handlers {
    prefix("foo stink") {
        slash {
            event.reply("${option<Int>("bornana")} bornana ${option<String?>("bleh")}")
        }

        autocomplete("bleh") {
            event.replyChoices((1..5).map { Command.Choice(it.toString(), it.toString()) })
        }

        onReturn<GenericInteractionCreateEvent, String> {
            eventAs<IReplyCallback> {
                it.reply("salam")
            }
        }
    }

    slash("resovertest") {
        val thing = option<CustomResolvedThing>("thing")

        val rawOption = event.getOption("thing")!!.asString

        event.reply(MessageCreate("${thing.foo} was ${thing.bornana}") {
            components += buttonKTX("dord.resovertest.${Resolvers.serialize(thing)}", rawOption).into()
        })
    }

    button("dord.resovertest.{thing}") {
        val thing = option<CustomResolvedThing>("thing")
        event.reply("${thing.foo} was ${thing.bornana} again!")
    }
}
