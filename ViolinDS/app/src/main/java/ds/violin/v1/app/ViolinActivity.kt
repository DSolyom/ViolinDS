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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import ds.violin.v1.app.violin.ActivityViolin
import ds.violin.v1.app.violin.NoTransportData
import ds.violin.v1.app.violin.PlayingViolin
import java.io.Serializable
import java.util.*

abstract class ViolinActivity : AppCompatActivity(), ActivityViolin {

    override val violins: HashMap<String, PlayingViolin> = HashMap()
    override var rootView: View? = null
    override var rootViewId: Int? = null
    override var parentViolin: PlayingViolin? = null
    override var played: Boolean = false
    override var transportDone: Boolean = false
    override var violinActivity: ActivityViolin = this
    override var activityActivated: Boolean = false
    override var transportData: Serializable? = NoTransportData
    override var activityResult: Any? = null
    override var activityRequestCode: Int = ActivityViolin.TRANSPORT_ACTION_CODE
    override var activityResultCode: Int = Activity.RESULT_OK
    override var afterTransport: Boolean = false
    override var hasSceneTransition: Boolean = false
    override var enterTransitionPostponed: Boolean = false
    override var startedEnterTransition: Boolean = false
    override var appBar: Toolbar? = null
    override var appBarShadow: View? = null
    override var excludeAppBarFromTransportAnimation: Boolean = false
    override var excludeNavigationBarFromTransportAnimation: Boolean = false
    override var excludeStatusBarFromTransportAnimation: Boolean = false
    override val requestedPermissions: MutableMap<String, PlayingViolin.RequestedPermission> = HashMap()

    override fun play() {
        // do your magic here

        super.play()
    }

    override fun findViewById(layoutResID: Int): View? {
        return super<AppCompatActivity>.findViewById(layoutResID)
    }

    override fun restoreInstanceState(savedInstanceState: Bundle) {
        // restore instance state here
    }

    override fun saveInstanceState(outState: Bundle) {
        // save instance stet here
    }

    override fun onResume() {
        super<ActivityViolin>.onResume()
        super<AppCompatActivity>.onResume()
    }

    override fun onBackPressed() {
        if (!actOnBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super<AppCompatActivity>.onNewIntent(intent)
        super<ActivityViolin>.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<AppCompatActivity>.onActivityResult(requestCode, resultCode, data)
        super<ActivityViolin>.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        saveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super<ActivityViolin>.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super<AppCompatActivity>.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}