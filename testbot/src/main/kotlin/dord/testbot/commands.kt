package dord.testbot

import com.pattexpattex.dord.Dord
import com.pattexpattex.dord.commands.option

fun Dord.setupNewCommands() = commands {
    prefix("foo") {
        slash("far") {
            option<Int>("bornana", "bleh", isAutocomplete = true)
        }
    }
}