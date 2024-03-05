package com.pattexpattex.dord.options

import kotlin.reflect.KType

abstract class OptionResolver<T : OptionResolver<T, R>, R : Any>(val resolvedType: KType)