/*
 *	Copyright 2020-2022 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.jamplate.jamfn

/**
 * DSL marker for primary scope utility functions.
 *
 * This applies to core functionalities and their
 * overrides. Like, but not limited to:
 * - [Jamfn.createSubJamfn]
 * - [Jamfn.current]
 * - [Jamfn.withLocal]
 *
 * @author LSafer
 * @since 0.4.0
 */
@DslMarker
annotation class JamfnPrimaryCompanion

/**
 * DSL marker for scope utility functions.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.13
 */
@DslMarker
annotation class JamfnCompanion

/**
 * DSL marker for jamfn component functions.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.13
 */
@DslMarker
annotation class JamfnComponent

/**
 * Marks the target as an experimental component.
 *
 * @author LSafer
 * @since 0.4.0
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalJamfnApi

/**
 * Marks the target as a component only to be
 * implemented and not used directly.
 *
 * @author LSafer
 * @since 0.4.0
 */
@RequiresOptIn(
    "This component isn't meant to be accessed directly.",
    RequiresOptIn.Level.ERROR
)
annotation class JamfnImplementationApi
