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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import me.sandeelvis.esploit.R;
import me.sandeelvis.esploit.core.Logger;
import me.sandeelvis.esploit.core.System;
import me.sandeelvis.esploit.net.GitHubParser;
import org.json.JSONException;

import java.io.IOException;

public class ChangelogDialog extends AlertDialog
{
  private final String ERROR_HTML = getContext().getString(R.string.something_went_wrong_changelog);

  private ProgressDialog mLoader = null;

  @SuppressLint("SetJavaScriptEnabled")
  public ChangelogDialog(final AppCompatActivity activity){
    super(activity);

    this.setTitle("Changelog");


    TextView view = new TextView(activity);

    this.setView(view);

    if(mLoader == null)
      mLoader = ProgressDialog.show(activity, "", getContext().getString(R.string.loading_changelog));

    try {
      view.setText(GitHubParser.getcSploitRepo().getReleaseBody(System.getAppVersionName()));
    } catch (JSONException e) {
      view.setText(Html.fromHtml(ERROR_HTML.replace("{DESCRIPTION}", e.getMessage())));
      System.errorLogging(e);
    } catch (IOException e) {
      view.setText(Html.fromHtml(ERROR_HTML.replace("{DESCRIPTION}", e.getMessage())));
      Logger.error(e.getMessage());
    }

    mLoader.dismiss();

    this.setCancelable(false);
    this.setButton(BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener(){
      public void onClick(DialogInterface dialog, int id){
        dialog.dismiss();
      }
    });
  }
}
