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

package ds.violin.v1.app

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ds.violin.v1.R
import ds.violin.v1.app.violin.*
import ds.violin.v1.model.entity.SelfLoadable
import ds.violin.v1.util.common.Debug
import ds.violin.v1.viewmodel.binding.ViewBinding
import java.io.Serializable
import java.util.*

abstract class ViolinFragment : DialogFragment(), FragmentViolin, LoadingViolin, ViewBinding {

    override val violins: HashMap<String, PlayingViolin> = HashMap()
    override var rootView: View? = null
    override var rootViewId: Int? = null
    override var parentViolin: PlayingViolin? = null
    override var played: Boolean = false
    override var transportDone: Boolean = false
    override lateinit var violinActivity: ActivityViolin

    override val registeredEntities: MutableMap<String, LoadingViolin.RegisteredEntity> = HashMap()
    override val situationalEntities: MutableMap<String, LoadingViolin.RegisteredEntity> = HashMap()
    override val loadingEntities: MutableList<SelfLoadable> = ArrayList()
    override var allDataLoadListener: LoadingViolin.AllDataLoadListener? = null
    override var savedStates: HashMap<String, Any> = HashMap()
    override var idsOfParcelable: ArrayList<String> = ArrayList()
    override var idsOfLoaded: ArrayList<String> = ArrayList()

    override val loadingViewID: Int? = null
    override var loadingView: View? = null

    override val requestedPermissions: MutableMap<String, PlayingViolin.RequestedPermission> = HashMap()

    override fun onAttach(activity: Activity) {
        super<DialogFragment>.onAttach(activity)
        super<FragmentViolin>.onAttach(activity as PlayingViolin)
    }

    override fun onAttach(context: Context) {
        super<DialogFragment>.onAttach(context)
        super<FragmentViolin>.onAttach(context as PlayingViolin)
    }

    override fun onDetach() {
        super<FragmentViolin>.onDetach()
        super<DialogFragment>.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super<FragmentViolin>.onCreateView(inflater,  container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super<FragmentViolin>.onViewCreated(view, savedInstanceState)
        super<LoadingViolin>.onViewCreated(view, savedInstanceState)
    }

    override fun invalidateRegisteredEntities(subViolinsToo: Boolean) {
        super<LoadingViolin>.invalidateRegisteredEntities(subViolinsToo)
        super<FragmentViolin>.invalidateRegisteredEntities(subViolinsToo)
    }

    override fun getContext(): Context? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getContext()
        } else {
            return violinActivity as Context?
        }
    }

    override fun play() {
        super<LoadingViolin>.play()
        super<FragmentViolin>.play()
    }

    override fun onResume() {
        super<DialogFragment>.onResume()
        super<FragmentViolin>.onResume()
    }

    override fun onPause() {
        super<LoadingViolin>.onPause()
        super<DialogFragment>.onPause()
    }

    override fun goBack(result: Serializable?) {
        super<LoadingViolin>.goBack(result)
        super<FragmentViolin>.goBack(result)
    }

    override fun goBackTo(target: Any, result: Serializable?) {
        super<LoadingViolin>.goBackTo(target, result)
        super<FragmentViolin>.goBackTo(target, result)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveInstanceState(outState)
    }

    override fun saveInstanceState(outState: Bundle) {
        super<FragmentViolin>.saveInstanceState(outState)
        super<LoadingViolin>.saveInstanceState(outState)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        super<LoadingViolin>.restoreInstanceState(savedInstanceState)
        super<FragmentViolin>.restoreInstanceState(savedInstanceState)
    }

    override fun onDestroyView() {
        super<FragmentViolin>.onDestroyView()
        super<DialogFragment>.onDestroyView()
    }

    override fun onDestroy() {
        super<LoadingViolin>.onDestroy()
        super<DialogFragment>.onDestroy()
    }

    override fun stopEverything() {
        super<LoadingViolin>.stopEverything()
        super<FragmentViolin>.stopEverything()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super<DialogFragment>.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super<FragmentViolin>.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<FragmentViolin>.onActivityResult(requestCode, resultCode, data)
        super<DialogFragment>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConnectionChanged(connected: Boolean) {
        super<FragmentViolin>.onConnectionChanged(connected)
        super<LoadingViolin>.onConnectionChanged(connected)
    }

    fun bind(value: Any?, viewResID: Int, method: Int) {
        bind(value, viewResID, method, violinActivity, rootView!!)
    }
}