/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.scripts

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.descriptors.ScriptDescriptor
import org.jetbrains.kotlin.descriptors.ScriptExternalParameters
import org.jetbrains.kotlin.descriptors.ScriptValueParameter
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtScript
import org.jetbrains.kotlin.script.*
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.reflect.KClass

abstract class TestScriptDefinition @JvmOverloads constructor(val extension: String, val classpath: List<String>? = null) : KotlinScriptDefinition {
    override val name = "Test Kotlin Script"
    override fun isScript(file: VirtualFile): Boolean = file.name.endsWith(extension)
    override fun isScript(file: PsiFile): Boolean = file.name.endsWith(extension)
    override fun getScriptName(script: KtScript): Name = ScriptNameUtil.fileNameWithExtensionStripped(script, extension)
    override fun getScriptDependenciesClasspath(): List<String> =
            classpath ?: (classpathFromProperty() + classpathFromClassloader(TestScriptDefinition::class.java.classLoader)).distinct()
}

open class SimpleParams(val parameters: List<ScriptValueParameter>) : ScriptExternalParameters {
    override val valueParameters: List<ScriptValueParameter> = parameters
}

class ReflectedParamClass(scriptDescriptor: ScriptDescriptor, paramName: String, parameter: KClass<out Any>) : ScriptExternalParameters {
    override val valueParameters = listOf(makeReflectedClassScriptParameter(scriptDescriptor, Name.identifier(paramName), parameter))
}

fun makeReflectedClassScriptParameter(scriptDescriptor: ScriptDescriptor, propertyName: Name, kClass: KClass<out Any>): ScriptValueParameter =
        ScriptValueParameter(propertyName, getKotlinType(scriptDescriptor, kClass))

open class ReflectedSuperclass(scriptDescriptor: ScriptDescriptor, parameters: List<ScriptValueParameter>, superclass: KClass<out Any>) :
        SimpleParams(parameters) {
    override val supertypes = listOf(getKotlinType(scriptDescriptor, superclass))
}

class ReflectedSuperclassWithParams(
        scriptDescriptor: ScriptDescriptor,
        parameters: List<ScriptValueParameter>,
        superclass: KClass<out Any>,
        superclassParameters: List<ScriptValueParameter>
) : ReflectedSuperclass(scriptDescriptor, parameters, superclass) {
    override val superclassConstructorParametersToScriptParametersMap = superclassParameters.map { Pair(it.name, it.type) }
}

class SimpleScriptExtraImport(
        override val classpath: List<String>,
        override val names: List<String> = emptyList()
) : KotlinScriptExtraImport

fun classpathFromProperty(): List<String> =
    System.getProperty("java.class.path")?.let {
        it.split(String.format("\\%s", File.pathSeparatorChar).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                .map { File(it).canonicalPath }
    } ?: emptyList()

private fun URL.toFile() =
    try {
        File(toURI().schemeSpecificPart)
    }
    catch (e: java.net.URISyntaxException) {
        if (protocol != "file") null
        else File(file)
    }

fun classpathFromClassloader(classLoader: ClassLoader): List<String> =
    (classLoader as? URLClassLoader)?.urLs
            ?.mapNotNull { it.toFile()?.canonicalPath }
            ?: emptyList()

fun testScriptDefinition(
        extension: String, classpath: List<String>? = null,
        params: (ScriptDescriptor) -> ScriptExternalParameters
) = object: TestScriptDefinition(extension, classpath) {
    override fun getScriptExternalParameters(scriptDescriptor: ScriptDescriptor) = params(scriptDescriptor)
}


class SimpleParamsTestScriptDefinition @JvmOverloads constructor(
        extension: String, val scriptParams: List<ScriptValueParameter>,
        classpath: List<String>? = null
) : TestScriptDefinition(extension, classpath) {
    override fun getScriptExternalParameters(scriptDescriptor: ScriptDescriptor) = object : ScriptExternalParameters {
        override val valueParameters = scriptParams
    }
}