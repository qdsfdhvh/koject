package com.moriatsushi.koject.processor.code

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.moriatsushi.koject.processor.analytics.locationString
import com.moriatsushi.koject.processor.error.CodeGenerationException
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSAnnotation.toNewInstanceCode(): CodeBlock {
    val type = annotationType.resolve()
    return buildCodeBlock {
        add("%T(", type.toTypeName())
        arguments.forEachIndexed { index, argument ->
            if (index > 0) {
                add(", ")
            }
            val name = argument.name!!.asString()
            val value = argument.value!!
            val block = valueCodeBlock(value)
                ?: throwCodeGenerationException(type, argument)
            add("$name = ")
            add(block)
        }
        add(")")
    }
}

private fun throwCodeGenerationException(
    annotationType: KSType,
    argument: KSValueArgument,
): Nothing {
    val variableTypeName = argument.value!!::class.qualifiedName
    throw CodeGenerationException(
        "${annotationType.declaration.locationString}: " +
            "$variableTypeName is an unsupported annotation member type.",
    )
}

private fun valueCodeBlock(value: Any): CodeBlock? {
    return when (value) {
        is KSType -> {
            val isEnum =
                (value.declaration as KSClassDeclaration).classKind == ClassKind.ENUM_ENTRY
            if (isEnum) {
                val parent = value.declaration.parentDeclaration as KSClassDeclaration
                val entry = value.declaration.simpleName.getShortName()
                CodeBlock.of("%T.%L", parent.toClassName(), entry)
            } else {
                CodeBlock.of("%T::class", value.toClassName())
            }
        }
        is KSName ->
            CodeBlock.of(
                "%T.%L",
                ClassName.bestGuess(value.getQualifier()),
                value.getShortName(),
            )
        is String -> CodeBlock.of("%S", value)
        is Char -> CodeBlock.of("\'$value\'")
        is Number, is Boolean -> CodeBlock.of("%L", value)
        else -> null
    }
}
