package com.pattexpattex.dord.options

import kotlin.reflect.KType

class DordResolverException(message: String, val resolvedType: KType) : RuntimeException(message)