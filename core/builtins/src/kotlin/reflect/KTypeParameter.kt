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

package kotlin.reflect

/**
 * Represents a declaration of a type parameter of a class or a callable.
 * See the [Kotlin language documentation](http://kotlinlang.org/docs/reference/generics.html#generics)
 * for more information.
 */
public interface KTypeParameter : KClassifier {
    /**
     * TODO
     */
    public val name: String

    /**
     * TODO
     */
    public val upperBounds: List<KType>

    /**
     * TODO
     */
    public val variance: KVariance
}
