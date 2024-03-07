package dord.testbot

import kotlinx.serialization.Serializable

@Serializable
data class CustomResolvedThing(
    val foo: Foo,
    val bornana: Boolean
) {
    override fun toString() = "$foo is $bornana"
}