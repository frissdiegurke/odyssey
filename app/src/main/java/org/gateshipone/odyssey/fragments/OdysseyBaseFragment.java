/*
 * Copyright (C) 2018 Team Team Gateship-One
 * (Hendrik Borghorst & Frederik Luetkes)
 *
 * The AUTHORS.md file contains a detailed contributors list:
 * <https://github.com/gateship-one/odyssey/blob/master/AUTHORS.md>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.gateshipone.odyssey.fragments;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;

import org.gateshipone.odyssey.listener.ToolbarAndFABCallback;
import org.gateshipone.odyssey.models.GenericModel;

import java.util.List;

abstract public class OdysseyBaseFragment<T extends GenericModel> extends Fragment implements LoaderManager.LoaderCallbacks<List<T>> {

    /**
     * Callback to setup toolbar and fab
     */
    protected ToolbarAndFABCallback mToolbarAndFABCallback;

    /**
     * The reference to the possible refresh layout
     */
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Holds if trimming for this Fragment is currently allowed or not.
     */
    protected boolean mTrimmingEnabled;

    /**
     * Callback to check the current memory state
     */
    private OdysseyComponentCallback mComponentCallback;

    /**
     * Holds if data is ready of has to be refetched (e.g. after memory trimming)
     */
    private boolean mDataReady;

    abstract void swapModel(List<T> model);

    abstract void resetModel();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (null == mComponentCallback) {
            mComponentCallback = new OdysseyComponentCallback();
        }

        // Register the memory trim callback with the system.
        context.registerComponentCallbacks(mComponentCallback);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mToolbarAndFABCallback = (ToolbarAndFABCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ToolbarAndFABCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Unregister the memory trim callback with the system.
        getActivity().getApplicationContext().unregisterComponentCallbacks(mComponentCallback);
    }

    /**
     * Method to reload the data and start the refresh indicator if a refreshlayout exists.
     */
    public void refreshContent() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
        }

        mDataReady = false;
        getLoaderManager().restartLoader(0, getArguments(), this);
    }

    /**
     * Checks if data is available or not. If not it will start getting the data.
     * This method should be called from onResume and if the fragment is part of an view pager,
     * every time the View is activated because the underlying data could be cleaned because
     * of memory pressure.
     */
    public void getContent() {
        // Check if data was fetched already or not (or removed because of trimming)
        if (!mDataReady) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
            }

            // Prepare loader ( start new one or reuse old )
            getLoaderManager().initLoader(0, getArguments(), this);
        }
    }

    /**
     * Called when the loader finished loading its data.
     * <p/>
     * The refresh indicator will be stopped if a refreshlayout exists.
     * If the new model is empty a special empty view will be shown if exists.
     *
     * @param loader The used loader itself
     * @param model  Data of the loader
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<T>> loader, List<T> model) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
        }

        // Indicate that the data is ready now.
        mDataReady = true;

        swapModel(model);
    }

    /**
     * If the loader is reset, the model data should be cleared.
     *
     * @param loader Loader that was resetted.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<T>> loader) {
        resetModel();
    }

    /**
     * This method can be used to prevent one fragment from triming its necessary data (e.g. active in a pager)
     *
     * @param enabled Enable the memory trimming
     */
    public void enableMemoryTrimming(boolean enabled) {
        mTrimmingEnabled = enabled;
    }

    /**
     * Private callback class used to monitor the memory situation of the system.
     * If memory reaches a certain point, we will relinquish our data.
     */
    private class OdysseyComponentCallback implements ComponentCallbacks2 {

        @Override
        public void onTrimMemory(int level) {
            if (mTrimmingEnabled && level >= TRIM_MEMORY_RUNNING_LOW) {
                getLoaderManager().destroyLoader(0);
                mDataReady = false;
            }
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
        }

        @Override
        public void onLowMemory() {
        }
    }
}
