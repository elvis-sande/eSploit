/*
 * This file is part of the dSploit.
 *
 * Copyleft of Simone Margaritelli aka evilsocket <evilsocket@gmail.com>
 *
 * dSploit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dSploit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dSploit.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.sandeelvis.esploit.gui.dialogs;

import android.content.DialogInterface;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import me.sandeelvis.esploit.BuildConfig;
import me.sandeelvis.esploit.R;
import me.sandeelvis.esploit.core.System;

import java.text.DateFormat;

public class AboutDialog extends AlertDialog {
  public AboutDialog(FragmentActivity activity){
    super(activity);

    DateFormat df = DateFormat.getDateTimeInstance();
    final View view = LayoutInflater.from(activity).inflate(R.layout.about_dialog, null);
    final TextView tv = (TextView) view.findViewById(R.id.buildinfo);
    tv.setText("Built by " + BuildConfig.BUILD_NAME + " on " + df.format(BuildConfig.BUILD_TIME));
    this.setTitle(activity.getString(R.string.about_csploit_v_) + System.getAppVersionName());
    this.setView(view);

    this.setButton(BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.dismiss();
      }
    });
  }
}
