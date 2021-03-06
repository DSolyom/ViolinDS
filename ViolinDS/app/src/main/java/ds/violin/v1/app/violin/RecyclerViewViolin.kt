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

package ds.violin.v1.app.violin

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ds.violin.v1.R
import ds.violin.v1.app.violin.LoadingViolin
import ds.violin.v1.app.violin.PlayingViolin
import ds.violin.v1.model.entity.SelfLoadable
import ds.violin.v1.model.modeling.Modeling
import ds.violin.v1.model.modeling.SerializableMapModel
import ds.violin.v1.util.common.Debug
import ds.violin.v1.viewmodel.AbsModelRecyclerViewItemBinder
import ds.violin.v1.viewmodel.binding.ModelViewBinding
import ds.violin.v1.widget.adapter.AbsHeaderedAdapter
import ds.violin.v1.widget.IRecyclerView
import java.util.*

interface RecyclerViewViolin {

    companion object {
        const val TAG_LM_STATE = "__recyclerviewviolin_lm_state_"
        const val TAG_ENTITY_ADAPTER = "__recyclerviewviolin_adapter__"
    }

    /** #Protected - the recycler view's id */
    val recyclerViewID: Int
    /** lateinit - the recycler view - set in [onViewCreated] */
    var recyclerView: IRecyclerView
    /** #Protected - the empty recycler view indicator view's id */
    val emptyViewID: Int?
    /** = null -  the empty recycler view indicator view - set in [onViewCreated] */
    var emptyView: View?
    /** =null - when it is needed to restore state of [RecyclerView.LayoutManager] */
    var layoutManagerState: Parcelable?
    /** =null - set before [RecyclerViewViolin.play] */
    var adapter: AbsRecyclerViewAdapter?
    /** =null - set before [RecyclerViewViolin.play] */
    var adapterViewBinder: RecyclerViewAdapterBinder?

    /** = if true, will search in [PlayingViolin.parentViolin] too for header view */
    val parentCanHoldHeader: Boolean
    /** = if true, will search in [PlayingViolin.parentViolin] too for footer view */
    val parentCanHoldFooter: Boolean

    /**
     * finding the [recyclerView]
     * !note: call before any other Violin's onViewCreated
     */
    fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        recyclerView = (this as PlayingViolin).findViewById(recyclerViewID) as IRecyclerView
        emptyView = this.findViewById(emptyViewID ?: 0)
    }

    /**
     * registering adapter if it is a [SelfLoadable] entity so no need to do that manually
     * this must be called before [LoadingViolin.play]
     */
    fun play() {
        if (!(this as PlayingViolin).played) {
            if (adapter is AbsHeaderedAdapter) {
                ensureListHeaderAndFooter()
            }

            if (recyclerView.adapter != adapter) {

                // new adapter - set it for the recycler view
                recyclerView.adapter = adapter
            }

            if (adapter is SelfLoadable && this is LoadingViolin && !(adapter as SelfLoadable).valid) {

                /** register adapter for loading */
                registerEntity(TAG_ENTITY_ADAPTER, adapter as SelfLoadable) { adapter, error ->
                    onAdapterLoadFinished(error)
                }
            } else if (adapter != null) {

                /** just bind the adapter (via [onAdapterLoadFinished]) */
                onAdapterLoadFinished(null)
            }
        }

        toggleEmptyViewVisibility()
    }

    /**
     * called when [adapter] load is finished, either it was successful or not
     */
    fun onAdapterLoadFinished(error: Throwable?) {
        if (error == null) {
            if (layoutManagerState != null) {
                recyclerView.restoreState(layoutManagerState)
                layoutManagerState = null
            }

            /** bind the [adapter] to the [recyclerView] */
            adapterViewBinder!!.bind(adapter!!, recyclerView, this as PlayingViolin)
        } else {
            Debug.logException(error)
        }
    }

    /**
     * #Protected - when called for the first time adds 'marked' [View]s as header and footer
     *              to the recycler view after removing them from their view structure
     *              marked [View] is a view with id:
     *              header - R.id.container_list_header
     *              footer - R.id.container_list_footer
     */
    fun ensureListHeaderAndFooter() {
        if (recyclerView.headerView != null || recyclerView.footerView != null) {

            // already added
            return
        }

        val listHeader = createHeaderViewFor(recyclerView, this as PlayingViolin)
        if (listHeader != null) {
            (listHeader.parent as ViewGroup?)?.removeView(listHeader)
            recyclerView.headerView = listHeader
        }

        val listFooter = createFooterViewFor(recyclerView, this)
        if (listFooter != null) {
            (listFooter.parent as ViewGroup?)?.removeView(listFooter)
            recyclerView.footerView = listFooter
        }
    }

    /**
     * return view or view group to be the [recyclerView]'s header
     *
     * !note: list might move out of the original [PlayingViolin.rootView]
     *
     * @return
     */
    fun createHeaderViewFor(recyclerView: IRecyclerView, on: PlayingViolin): View? {
        var headerView = on.findViewById(R.id.container_list_header)

        if (headerView == null && on.parentViolin != null && parentCanHoldHeader) {
            return createHeaderViewFor(recyclerView, on.parentViolin!!)
        }

        if (headerView != null) {
            val rv = headerView.findViewById(recyclerViewID)

            if (rv != null && (rv is IRecyclerView)) {

                /** list is inside its header - move it out */
                val lp = rv.getLayoutParams()
                val lpWidth = lp.width
                val lpHeight = lp.height
                (rv.parent as ViewGroup).removeViewInLayout(rv)

                (headerView.parent as ViewGroup).addView(rv, lpWidth, lpHeight)
            }
        }
        return headerView
    }

    /**
     * return view or view group to be the [recyclerView]'s footer
     * 
     * !note: list might move out of the original [PlayingViolin.rootView]
     *
     * @return
     */
    fun createFooterViewFor(recyclerView: IRecyclerView, on: PlayingViolin): View? {
        var footerView = on.findViewById(R.id.container_list_footer)

        if (footerView == null && on.parentViolin != null && parentCanHoldFooter) {
            return createFooterViewFor(recyclerView, on.parentViolin!!)
        }

        if (footerView != null) {
            val rv = footerView.findViewById(recyclerViewID)

            if (rv != null && (rv is IRecyclerView)) {

                /** list is inside its footer - move it out */
                val lp = rv.getLayoutParams()
                val lpWidth = lp.width
                val lpHeight = lp.height
                (rv.parent as ViewGroup).removeViewInLayout(rv)

                (footerView.parent as ViewGroup).addView(rv, lpWidth, lpHeight)
            }
        }
        return footerView
    }

    /**
     * toggle empty view's visibility
     */
    fun toggleEmptyViewVisibility() {
        if (emptyView != null) {
            emptyView!!.visibility = when (isListEmpty()) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        }
    }

    /**
     * should show empty view
     *
     * @param return
     */
    fun isListEmpty(): Boolean {
        val count = when (adapter is AbsHeaderedAdapter) {
            true -> (adapter as AbsHeaderedAdapter).getRealCount()
            false -> adapter!!.itemCount
        }
        return (adapter != null && (adapter !is SelfLoadable || (adapter as SelfLoadable).valid) && count == 0)
    }

    /**
     * get [recyclerView]'s last scroll (and all [RecyclerView.LayoutManager]) state
     */
    fun restoreInstanceState(savedInstanceState: Bundle) {
        layoutManagerState = savedInstanceState.getParcelable(TAG_LM_STATE)
    }

    /**
     * save[recyclerView]'s scroll (and all [RecyclerView.LayoutManager]) state
     */
    fun saveInstanceState(outState: Bundle) {

        try {
            outState.putParcelable(TAG_LM_STATE, recyclerView.getState())
        } catch(e: Throwable) {
            // recyclerView's existence is not ensured yet
        }
    }
}

/**
 * base interface for binding [AbsRecyclerViewAdapter] to the [IRecyclerView]
 *
 * !note: one binder is only usable for one [RecyclerView], if the layout is recreated
 *        (like after a fragment transformation) a new [RecyclerViewAdapterBinder] is required
 *        (@see [PlayingViolin.played] and @see [FragmentViolin.onCreateView]
 */
open class RecyclerViewAdapterBinder(layoutManager: RecyclerView.LayoutManager) : ModelViewBinding<AbsRecyclerViewAdapter> {

    override lateinit var rootView: View
    override lateinit var on: PlayingViolin

    val layoutManager: RecyclerView.LayoutManager = layoutManager

    override fun bind(adapter: AbsRecyclerViewAdapter) {

        /** set adapter */
        val recyclerView = rootView as RecyclerView

        if (recyclerView.layoutManager == null) {
            recyclerView.layoutManager = layoutManager
        }

        if (recyclerView.adapter != adapter) {

            // new adapter loaded - set it for the recycler view
            recyclerView.adapter = adapter
        } else {

            // 'only' data set has changed
            adapter.notifyDataSetChanged()
        }

        /** show / hide [emptyView] */
        if (on is RecyclerViewViolin) {
            (on as RecyclerViewViolin).toggleEmptyViewVisibility()
        }
    }
}

/**
 * abstract class with the things required for a [RecyclerView.Adapter] to be used in a
 * [RecyclerViewViolin]
 */
abstract class AbsRecyclerViewAdapter(on: PlayingViolin) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /** */
    val on: PlayingViolin = on
}
