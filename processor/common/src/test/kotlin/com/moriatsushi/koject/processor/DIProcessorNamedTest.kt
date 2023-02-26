package com.moriatsushi.koject.processor

import com.moriatsushi.koject.processor.assert.assertCompileSucceed
import com.moriatsushi.koject.processor.assert.assertFileExists
import com.moriatsushi.koject.processor.assert.assertFileTextEquals
import com.moriatsushi.koject.processor.compiletesting.KotlinCompilationFactory
import com.moriatsushi.koject.processor.symbol.Identifier
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DIProcessorNamedTest {
    @get:Rule
    val tempFolder: TemporaryFolder = TemporaryFolder()

    private val compilationFactory = KotlinCompilationFactory()

    @Test
    fun compile() {
        val folder = tempFolder.newFolder()
        val complication = compilationFactory.create(folder)
        complication.sources = listOf(inputCode)
        val result = complication.compile()

        assertCompileSucceed(result)

        val expectedClass1FactoryFile = folder.resolve(expectedName1FactoryFilePath)
        assertFileExists(expectedClass1FactoryFile)
        assertFileTextEquals(expectedName1FactoryText, expectedClass1FactoryFile)

        val expectedClass2FactoryFile = folder.resolve(expectedClassFactoryFilePath)
        assertFileExists(expectedClass2FactoryFile)
        assertFileTextEquals(expectedClassFactoryText, expectedClass2FactoryFile)

        val expectedClass3FactoryFile = folder.resolve(expectedFunctionFactoryFilePath)
        assertFileExists(expectedClass3FactoryFile)
        assertFileTextEquals(expectedFunctionFactoryText, expectedClass3FactoryFile)
    }

    private val inputCode = SourceFile.kotlin(
        "Test.kt",
        """
                package com.testpackage

                import com.moriatsushi.koject.Named
                import com.moriatsushi.koject.Provides

                @Named("name1")
                @Provides
                fun provideString1(): String {
                    return "name1"
                }

                @Named("name2")
                @Provides
                fun provideString1(): String {
                    return "name2"
                }

                @Provides
                class SampleClass(
                    @Named("name1")
                    private val name1: String,
                    @Named("name2")
                    private val name2: String,
                )

                @Named("by_function")
                @Provides
                fun provideSampleClass(
                    @Named("name1") name1: String,
                    @Named("name2") name2: String,
                ): SampleClass {
                    return SampleClass(name1, name2)
                }
            """,
    )

    private val expectedName1Identifier = Identifier("kotlin.String:Named(name1)")
    private val expectedName2Identifier = Identifier("kotlin.String:Named(name2)")

    private val expectedName1FactoryFilePath =
        "ksp/sources/kotlin/com/moriatsushi/koject/generated/factory/" +
            "_${expectedName1Identifier.asCodeName()}_Factory.kt"

    private val expectedClassFactoryFilePath =
        "ksp/sources/kotlin/com/moriatsushi/koject/generated/factory/" +
            "_com_testpackage_SampleClass_Factory.kt"

    private val expectedFunctionIdentifier =
        Identifier("com.testpackage.SampleClass:Named(by_function)")
    private val expectedFunctionFactoryFilePath =
        "ksp/sources/kotlin/com/moriatsushi/koject/generated/factory/" +
            "_${expectedFunctionIdentifier.asCodeName()}_Factory.kt"

    @Language("kotlin")
    private val expectedName1FactoryText = """
        |// Generated by Koject. Do not modify!
        |package com.moriatsushi.koject.generated.factory
        |
        |import com.moriatsushi.koject.Named
        |import com.moriatsushi.koject.`internal`.InternalKojectApi
        |import com.moriatsushi.koject.`internal`.identifier.Identifier
        |import com.moriatsushi.koject.`internal`.identifier.StringIdentifier
        |import com.testpackage.provideString1
        |import kotlin.Any
        |import kotlin.String
        |
        |@InternalKojectApi
        |@StringIdentifier("$expectedName1Identifier")
        |public class _${expectedName1Identifier.asCodeName()}_Factory() {
        |    public fun create(): Any = provideString1()
        |
        |    public companion object {
        |        public val identifier: Identifier = Identifier.of<String>(Named("name1"))
        |    }
        |}
        |
    """.trimMargin()

    @Language("kotlin")
    private val expectedClassFactoryText = """
        |// Generated by Koject. Do not modify!
        |package com.moriatsushi.koject.generated.factory
        |
        |import com.moriatsushi.koject.`internal`.InternalKojectApi
        |import com.moriatsushi.koject.`internal`.identifier.Identifier
        |import com.moriatsushi.koject.`internal`.identifier.StringIdentifier
        |import com.testpackage.SampleClass
        |import kotlin.Any
        |import kotlin.String
        |
        |@InternalKojectApi
        |@StringIdentifier("com.testpackage.SampleClass")
        |public class _com_testpackage_SampleClass_Factory(
        |    @StringIdentifier("$expectedName1Identifier")
        |    private val provide_${expectedName1Identifier.asCodeName()}: () -> Any,
        |    @StringIdentifier("$expectedName2Identifier")
        |    private val provide_${expectedName2Identifier.asCodeName()}: () -> Any,
        |) {
        |    public fun create(): Any = SampleClass(
        |        provide_${expectedName1Identifier.asCodeName()}() as String,
        |        provide_${expectedName2Identifier.asCodeName()}() as String,
        |    )
        |
        |    public companion object {
        |        public val identifier: Identifier = Identifier.of<SampleClass>()
        |    }
        |}
        |
    """.trimMargin()

    @Language("kotlin")
    private val expectedFunctionFactoryText = """
        |// Generated by Koject. Do not modify!
        |package com.moriatsushi.koject.generated.factory
        |
        |import com.moriatsushi.koject.Named
        |import com.moriatsushi.koject.`internal`.InternalKojectApi
        |import com.moriatsushi.koject.`internal`.identifier.Identifier
        |import com.moriatsushi.koject.`internal`.identifier.StringIdentifier
        |import com.testpackage.SampleClass
        |import com.testpackage.provideSampleClass
        |import kotlin.Any
        |import kotlin.String
        |
        |@InternalKojectApi
        |@StringIdentifier("$expectedFunctionIdentifier")
        |public class _${expectedFunctionIdentifier.asCodeName()}_Factory(
        |    @StringIdentifier("$expectedName1Identifier")
        |    private val provide_${expectedName1Identifier.asCodeName()}: () -> Any,
        |    @StringIdentifier("$expectedName2Identifier")
        |    private val provide_${expectedName2Identifier.asCodeName()}: () -> Any,
        |) {
        |    public fun create(): Any = provideSampleClass(
        |        provide_${expectedName1Identifier.asCodeName()}() as String,
        |        provide_${expectedName2Identifier.asCodeName()}() as String,
        |    )
        |
        |    public companion object {
        |        public val identifier: Identifier = Identifier.of<SampleClass>(Named("by_function"))
        |    }
        |}
        |
    """.trimMargin()
}
