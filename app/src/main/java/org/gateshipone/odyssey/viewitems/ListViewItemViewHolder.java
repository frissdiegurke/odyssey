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

package org.gateshipone.odyssey.viewitems;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import org.gateshipone.odyssey.models.TrackModel;

public class ListViewItemViewHolder extends RecyclerView.ViewHolder {

    public ListViewItemViewHolder(final ListViewItem itemView) {
        super(itemView);
    }

    public void setAlbumTrack(final Context context, final TrackModel trackModel, final boolean mShowDiscNumber) {
        ((ListViewItem) itemView).setAlbumTrack(context, trackModel, mShowDiscNumber);
    }
}