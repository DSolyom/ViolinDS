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
import ds.violin.v1.datasource.AbsBackgroundDataLoader
import ds.violin.v1.model.entity.ContinuousListDataLoading
import ds.violin.v1.model.entity.ContinuousMutableListing
import ds.violin.v1.datasource.dataloading.DataLoading
import ds.violin.v1.model.modeling.Modeling
import ds.violin.v1.util.common.Debug
import org.json.JSONArray
import org.json.JSONObject

/**
 * Parcelable data for [ContinuousJSONArrayAdapter]
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
 *
 * !note: the adapter's [dataLoader] should be a [AbsBackgroundDataLoader] and [ContinuousListDataLoading]
 */
abstract class ContinuousJSONArrayAdapter(on: PlayingViolin, dataLoader: DataLoading, models: JSONArray = JSONArray()) :
        JSONArrayAdapterEntity(on, dataLoader, models), ContinuousMutableListing<JSONArray, JSONObject> {

    override var offset: Int = 0
    override var presumedSize: Int? = null

    override var firstLoad: Boolean = true
    override var loadingForward: Boolean = true

    override var sizeChange: Int = 0
    override var offsetChange: Int = 0

    /**
     * to load the list continuously [get] should be called with a completion block which notify
     * about range insertion or removal
     */
    override fun getRowDataModel(position: Int): Modeling<*> {
        return get(position, { entity, error ->
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
        })
    }

    override fun dataToParcelable(): Parcelable {
        return ContinuousJSONArrayAdapterDataParcelable(models.toString(), offset)
    }

    override fun createDataFrom(parcelableData: Parcelable) {
        models = JSONArray((parcelableData as ContinuousJSONArrayAdapterDataParcelable).modelsString)
        offset = parcelableData.offset
    }
}