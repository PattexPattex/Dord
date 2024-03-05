package dord.testbot

import com.pattexpattex.dord.Dord
import dev.minn.jda.ktx.jdabuilder.default

fun main() {
    val dord = Dord {
        setupNewCommands()
        setupHandlers()
    }

    val jda = default(System.getenv("TOKEN"), enableCoroutines = true) {
        addEventListeners(dord)
    }
}