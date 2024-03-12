package com.pattexpattex.dord.options

import kotlin.reflect.KType

sealed class IDordResolversException(
    message: String,
    val arg: String,
    val resolvedType: KType,
    cause: Throwable?
) : RuntimeException(message, cause)

class DordResolverException(
    message: String,
    arg: String,
    resolvedType: KType,
    cause: Throwable
) : IDordResolversException(message, arg, resolvedType, cause)

class DordResolvedValueException(
    message: String,
    arg: String,
    resolvedType: KType,
    cause: Throwable? = null
) : IDordResolversException(message, arg, resolvedType, cause)

