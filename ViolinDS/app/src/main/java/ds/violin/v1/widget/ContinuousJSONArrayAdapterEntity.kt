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

package ds.violin.v1.widget

import android.os.Parcel
import android.os.Parcelable
import ds.violin.v1.app.violin.PlayingViolin
import ds.violin.v1.datasource.BackgroundDataLoader
import ds.violin.v1.model.entity.ContinuousListDataLoading
import ds.violin.v1.model.entity.ContinuousMutableListing
import ds.violin.v1.datasource.base.DataLoading
import ds.violin.v1.model.modeling.JSONModel
import ds.violin.v1.model.modeling.Modeling
import ds.violin.v1.util.common.Debug
import ds.violin.v1.util.common.JSONArrayWrapper
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.Serializable
import java.util.*

/**
 * Parcelable data for [ContinuousJSONArrayAdapterEntity]
 */
class ContinuousJSONArrayAdapterDataParcelable(modelsString: String, offset: Int) : Parcelable {

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<ContinuousJSONArrayAdapterDataParcelable> = object :
                Parcelable.Creator<ContinuousJSONArrayAdapterDataParcelable> {
            override fun createFromParcel(source: Parcel): ContinuousJSONArrayAdapterDataParcelable {
                return ContinuousJSONArrayAdapterDataParcelable(source)
            }

            override fun newArray(size: Int): Array<ContinuousJSONArrayAdapterDataParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }

    val modelsString: String = modelsString
    val offset: Int = offset

    constructor(source: Parcel) : this(source.readString(), source.readInt())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(modelsString)
        dest.writeInt(offset)
    }

    override fun describeContents(): Int {
        return 0
    }
}

/**
 * an adapter for loading a list continuously as scrolling which uses a [JSONArrayAdapter] as base
 * and a [ContinuousMutableListing] with [JSONArray] and [JSONObject] to load and hold the data
 * !note: the list must contain [JSONObject]s only
 *
 * !note: the adapter's [dataLoader] should be a [BackgroundDataLoader] and [ContinuousListDataLoading]
 */
abstract class ContinuousJSONArrayAdapterEntity(on: PlayingViolin, dataLoader: DataLoading, models: JSONArray = JSONArray()) :
        JSONArrayAdapterEntity(on, dataLoader, models), ContinuousMutableListing<JSONArray, Any> {

    override var offset: Int = 0
    override var presumedSize: Int? = null

    override var firstLoad: Boolean = true
    override var loadingForward: Boolean = true

    override var sizeChange: Int = 0
    override var offsetChange: Int = 0

    /**
     * call 'get' with a completion block which notifies about range insertion or removal
     * to load the list continuously
     */
    override fun getItemDataModel(dataPosition: Int, section: Int): Modeling<*, *> {
        return JSONModel(get(dataPosition, { entity, error ->
            if (error == null) {
                val newItemsAdded = sizeChange + offsetChange
                if (newItemsAdded > 0) {
                    notifyItemRangeInserted(size - sizeChange, newItemsAdded)
                } else if (newItemsAdded < 0) {
                    notifyItemRangeRemoved(size, -newItemsAdded)
                }
                if (offsetChange < 0) {
                    notifyItemRangeInserted(0, -offsetChange)
                } else if (offsetChange > 0) {
                    notifyItemRangeRemoved(0, offsetChange)
                }
            } else {
                Debug.logException(error)
            }
        }) as JSONObject)
    }

    override fun dataToSerializable(): Serializable {
        val data = HashMap<String, Any?>()
        data.put("data", JSONArrayWrapper(values))
        data.put("offset", offset)
        data.put("presumed_size", presumedSize)
        data.put("first_load", firstLoad)
        return data
    }

    override fun createDataFrom(serializedData: Serializable) {
        @Suppress("UNCHECKED_CAST")
        val data = serializedData as HashMap<String, Any?>
        values = (data["data"] as JSONArrayWrapper).values!!
        offset = data["offset"] as Int
        presumedSize = data["presumed_size"] as Int?
        firstLoad = data["first_load"] as Boolean
    }

/* this is an example if the adapter would implement HasParcelableData
    override fun dataToParcelable(): Parcelable {
        return ContinuousJSONArrayAdapterDataParcelable(values.toString(), offset)
    }

    override fun createDataFrom(parcelableData: Parcelable) {
        values = JSONValue.parse((parcelableData as ContinuousJSONArrayAdapterDataParcelable).modelsString) as JSONArray
        offset = parcelableData.offset
    }
    */
}