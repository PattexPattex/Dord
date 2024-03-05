# Dord

Another Discord bot library, built around JDA with simplicity in mind
*(a.k.a. a replacement for [Dikko](https://github.com/PattexPattex/Dikko), which is a dumpster fire compared to this)*

## Download

```kotlin
repositories {
  mavenCentral()
  maven { url("https://jitpack.io") } // add jitpack as a repository
}

dependencies {
  implementation ("com.github.PattexPattex:Dord:$VERSION$") // replace version with a short commit hash
}
```

## Using Dord

```kotlin
val dord = Dord {
  /* stuff */
}

// build JDA with jda-ktx
val jda = default("token", enableCoroutines = true) {
  addEventListeners(dord) // register Dord as an event listener
}
```

### Registering slash & context menu commands

```kotlin
dord.commands {
    slash("foo", "far") {
        option<String>("bar", "bar")
        option<User?>("baz", "baz") // if type is nullable, option is marked as not required
    }
    
    prefix("bornana") {
        slash("this is a description") { // this command is automatically prefixed with "bornana"
            /* ... */
        }
    }
    
    user("ping spam") // user context menu
    message("reply spam") { // message context menu
        /* ... */
    }
}
```

### Registering event handlers

```kotlin
dord.handlers {
    slash("foo") {
        // automatically resolve OptionMapping to desired types
        val bar = option<String>("bar")
        val baz = option<User?>("baz")
        
        event.reply(baz?.asMention ?: bar).await() // you can use suspending functions
    }
    
    // handler name prefixes
    prefix("bornana") {
        slash {
            onError<StupidException> {
                /* handle exceptions thrown inside this slash handler */
            }
            
            event.reply("orange rhymes with banana").queue()
            return@slash FooEnum.FOO
        }
    }

    // custom handler for return values
    // this one runs only when handlers of MessageReceivedEvents return a FooEnum
    onReturn<MessageReceivedEvent, FooEnum> { foo ->
        when (foo) {
            FOO -> { /* ... */ }
            FAR -> { /* ... */ }
        }
    }
    
    // exception handlers to recover exceptions in event handlers
    // this handler handles exceptions thrown by any handler
    onError<CustomException> { exception ->
        // event is a GenericEvent, but you can cast it
        val castEvent = eventAs<IReplyCallback>() // is null if event is not an instance of IReplyCallback
        
        // you can also use it like this
        eventAs<IReplyCallback> { callback ->
            // this block is not ran if the event is not an instance of IReplyCallback
            callback.reply("foo").await()
        }
    }
    
    // handle other types of events
    onEvent<MessageReceivedEvent> {
        /* ... */
    }
}
```

### Using option resolvers

```kotlin
// when getting options in events, they are automatically resolved to a desired type:
val bar = option<String>("bar")

// you can register custom option resolvers
class InstantResolver :
    OptionResolver<InstantResolver, Instant>(typeOf<Instant>()), // base class
    SlashOptionResolver<InstantResolver, Instant> // this resolver can resolve options from slash events
{
    override val optionType = OptionType.INTEGER
    
    // implement the resolve function
    suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) = 
        optionMapping?.asInt?.let { Instant.ofEpochSecond(it) }
}

// register the new resolver
Resolvers.register(InstantResolver())
/* ... */
val timestamp = option<Instant>("timestamp") // you can now get Instants automatically

// create enum resolvers
Resolvers.enumResolver<FooEnum>()

/* more examples are in the com.pattexpattex.dord.options package */
```
