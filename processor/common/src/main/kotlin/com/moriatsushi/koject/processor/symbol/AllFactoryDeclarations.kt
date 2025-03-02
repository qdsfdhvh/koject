package com.moriatsushi.koject.processor.symbol

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.moriatsushi.koject.processor.code.Names

internal data class AllFactoryDeclarations(
    val factories: Sequence<FactoryDeclaration>,
    val extrasHolders: Sequence<ComponentExtrasHolderDeclaration>,
) {
    val rootComponent: ComponentDeclaration.Root =
        ComponentDeclaration.Root(
            factories.filter { it.component == null },
        )

    val childComponents: Sequence<ComponentDeclaration.Child> =
        extrasHolders.map { extrasHolder ->
            ComponentDeclaration.Child(
                extrasHolder = extrasHolder,
                factories = factories.filter {
                    it.component == extrasHolder.componentName
                },
            )
        }
}

internal fun Resolver.collectAllFactoryDeclarations(): AllFactoryDeclarations {
    @OptIn(KspExperimental::class)
    val factories = getDeclarationsFromPackage(Names.factoryPackageName)
        .filterIsInstance<KSClassDeclaration>()
        .map { FactoryDeclaration.of(it) }

    @OptIn(KspExperimental::class)
    val extrasHolders = getDeclarationsFromPackage(Names.componentPackageName)
        .filterIsInstance<KSClassDeclaration>()
        .map { ComponentExtrasHolderDeclaration.of(it) }

    return AllFactoryDeclarations(factories, extrasHolders)
}
