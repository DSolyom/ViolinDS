/*
    Copyright 2016 Dániel Sólyom

    Licensed under the Apache License, Version 2.0 (the "License")
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package ds.violin.v1.model

import android.database.Cursor
import ds.violin.v1.model.entity.SelfLoadableModeling
import ds.violin.v1.model.modeling.CursorModel
import ds.violin.v1.datasource.base.DataLoading

/**
 * entity holding it's data in a [CursorModel]
 */
open class CursorEntity(values: Cursor? = null, cursorPosition: Int = 0) :
        CursorModel(values, cursorPosition), SelfLoadableModeling<Cursor, Any> {

    override lateinit var dataLoader: DataLoading
    override var valid: Boolean = false
    override var interrupted: Boolean = false
}