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
package me.sandeelvis.esploit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import me.sandeelvis.esploit.core.ChildManager;
import me.sandeelvis.esploit.core.ExecChecker;
import me.sandeelvis.esploit.core.Logger;
import me.sandeelvis.esploit.core.System;
import me.sandeelvis.esploit.gui.DirectoryPicker;
import me.sandeelvis.esploit.gui.dialogs.ChoiceDialog;
import me.sandeelvis.esploit.gui.dialogs.ConfirmDialog;
import me.sandeelvis.esploit.net.GitHubParser;
import me.sandeelvis.esploit.services.Services;
import me.sandeelvis.esploit.tools.Raw;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.TwoStatePreference;

public class SettingsFragment extends Fragment {


    public static final int SETTINGS_DONE = 1285;
    public static final String SETTINGS_WIPE_START = "SettingsActivity.WIPE_START";
    public static final String SETTINGS_WIPE_DIR = "SettingsActivity.data.WIPE_DIR";
    public static final String SETTINGS_WIPE_DONE = "SettingsActivity.WIPE_DONE";
    public static final String SETTINGS_MSF_CHANGED = "SettingsActivity.MSF_CHANGED";
    public static final String SETTINGS_MSF_BRANCHES_AVAILABLE = "SettingsActivity.MSF_MSF_BRANCHES_AVAILABLE";

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences themePrefs = getActivity().getSharedPreferences("THEME", 0);
        if (themePrefs.getBoolean("isDark", false))
            getActivity().setTheme(R.style.PrefsThemeDark);
        else
            getActivity().setTheme(R.style.PrefsTheme);
        super.onCreate(savedInstanceState);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFrag())
                .commit();
    }


        public static class PrefsFrag extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


        private Preference mSavePath = null;
        private Preference mWipeMSF = null;
        private Preference mRubyDir = null;
        private Preference mMsfDir = null;
        private EditTextPreference mSnifferSampleTime = null;
        private EditTextPreference mProxyPort = null;
        private EditTextPreference mServerPort = null;
        private EditTextPreference mRedirectorPort = null;
        private EditTextPreference mMsfPort = null;
        private EditTextPreference mHttpBufferSize = null;
        private EditTextPreference mPasswordFilename = null;
        private TwoStatePreference mThemeChooser = null;
        private TwoStatePreference mMsfEnabled = null;
        private ListPreference mMsfBranch = null;
        private int mMsfSize = 0;
        private BroadcastReceiver mReceiver = null;
        private Thread mBranchesWaiter = null;

            @Override
            public void onViewCreated(View v, Bundle savedInstanceState) {
                super.onViewCreated(v, savedInstanceState);
                SharedPreferences themePrefs = getActivity().getSharedPreferences("THEME", 0);
                Boolean isDark = themePrefs.getBoolean("isDark", false);
                if (isDark) {
                    getActivity().setTheme(R.style.PrefsThemeDark);
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_window_dark));
                } else {
                    getActivity().setTheme(R.style.PrefsTheme);
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_window));
                }
            }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            SharedPreferences themePrefs = getActivity().getBaseContext().getSharedPreferences("THEME", 0);
            if (themePrefs.getBoolean("isDark", false))
                getContext().setTheme(R.style.PrefsThemeDark);
            else
                getActivity().setTheme(R.style.PrefsTheme);
            super.onCreate(savedInstanceState);

            mSavePath = getPreferenceScreen().findPreference("PREF_SAVE_PATH");
            mWipeMSF = getPreferenceScreen().findPreference("PREF_MSF_WIPE");
            mRubyDir = getPreferenceScreen().findPreference("RUBY_DIR");
            mMsfDir = getPreferenceScreen().findPreference("MSF_DIR");
            mMsfPort = (EditTextPreference) getPreferenceScreen().findPreference("MSF_RPC_PORT");
            mSnifferSampleTime = (EditTextPreference) getPreferenceScreen().findPreference("PREF_SNIFFER_SAMPLE_TIME");
            mProxyPort = (EditTextPreference) getPreferenceScreen().findPreference("PREF_HTTP_PROXY_PORT");
            mServerPort = (EditTextPreference) getPreferenceScreen().findPreference("PREF_HTTP_SERVER_PORT");
            mRedirectorPort = (EditTextPreference) getPreferenceScreen().findPreference("PREF_HTTPS_REDIRECTOR_PORT");
            mHttpBufferSize = (EditTextPreference) getPreferenceScreen().findPreference("PREF_HTTP_MAX_BUFFER_SIZE");
            mPasswordFilename = (EditTextPreference) getPreferenceScreen().findPreference("PREF_PASSWORD_FILENAME");
            mThemeChooser = (TwoStatePreference) getPreferenceScreen().findPreference("PREF_DARK_THEME");
            mMsfBranch = (ListPreference) getPreferenceScreen().findPreference("MSF_BRANCH");
            mMsfEnabled = (TwoStatePreference) getPreferenceScreen().findPreference("MSF_ENABLED");

            mThemeChooser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences themePrefs = getActivity().getBaseContext().getSharedPreferences("THEME", 0);
                    themePrefs.edit().putBoolean("isDark", (Boolean) newValue).apply();
                    Toast.makeText(getActivity().getBaseContext(), getString(R.string.please_restart), Toast.LENGTH_LONG).show();
                    return true;
                }
            });

            mSavePath.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startDirectoryPicker(preference);
                    return true;
                }
            });

            if (mMsfEnabled.isEnabled())
                onMsfEnabled();
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.preferences);
        }

        private void wipe_prompt() {
            String message = getString(R.string.pref_msfwipe_message);
            if (mMsfSize > 0) {
                message += "\n" + String.format(getString(R.string.pref_msfwipe_size), mMsfSize);
            }
            new ConfirmDialog(getString(R.string.warning), message, getActivity(), new ConfirmDialog.ConfirmDialogListener() {
                @Override
                public void onConfirm() {
                    getActivity().sendBroadcast(new Intent(SETTINGS_WIPE_START));
                }

                @Override
                public void onCancel() {

                }
            }).show();
        }

        private void wipe_prompt_older(final File oldDir) {
            new ConfirmDialog(getString(R.string.warning), getString(R.string.delete_previous_location), getActivity(), new ConfirmDialog.ConfirmDialogListener() {
                @Override
                public void onConfirm() {
                    Intent i = new Intent(SETTINGS_WIPE_START);
                    i.putExtra(SETTINGS_WIPE_DIR, oldDir.getAbsolutePath());
                    getActivity().sendBroadcast(i);
                }

                @Override
                public void onCancel() {

                }
            }).show();
        }

        private void measureMsfSize() {
            try {
                System.getTools().raw.async(String.format("du -xsm '%s' '%s'", System.getRubyPath(), System.getMsfPath()),
                        new Raw.RawReceiver() {
                            private int size = 0;

                            @SuppressWarnings("StatementWithEmptyBody")
                            @Override
                            public void onNewLine(String line) {
                                if (line.isEmpty())
                                    return;
                                try {
                                    int start, end;
                                    for (start = 0; start < line.length() && java.lang.Character.isSpaceChar(line.charAt(start)); start++)
                                        ;
                                    for (end = start + 1; end < line.length() && java.lang.Character.isDigit(line.charAt(end)); end++)
                                        ;
                                    size += Integer.parseInt(line.substring(start, end));
                                } catch (Exception e) {
                                    System.errorLogging(e);
                                }
                            }

                            @Override
                            public void onEnd(int exitCode) {
                                if (exitCode == 0)
                                    mMsfSize = size;
                            }
                        });
            } catch (ChildManager.ChildNotStartedException e) {
                Logger.error(e.getMessage());
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (requestCode == DirectoryPicker.PICK_DIRECTORY && resultCode != AppCompatActivity.RESULT_CANCELED) {
                Bundle extras = intent.getExtras();
                String path;
                String key;
                File folder;
                String oldPath = null;

                if (extras == null) {
                    Logger.debug("null extra: " + intent);
                    return;
                }

                path = (String) extras.get(DirectoryPicker.CHOSEN_DIRECTORY);
                key = (String) extras.get(DirectoryPicker.AFFECTED_PREF);

                if (path == null || key == null) {
                    Logger.debug("null path or key: " + intent);
                    return;
                }

                folder = new File(path);
                ExecChecker checker = null;


                if (key.equals("RUBY_DIR")) {
                    oldPath = System.getRubyPath();
                    checker = ExecChecker.ruby();
                } else if (key.equals("MSF_DIR")) {
                    oldPath = System.getMsfPath();
                    checker = ExecChecker.msf();
                }

                if (!folder.exists())
                    Toast.makeText(getActivity(), getString(R.string.pref_folder) + " " + path + " " + getString(R.string.pref_err_exists), Toast.LENGTH_SHORT).show();

                else if (!folder.canWrite())
                    Toast.makeText(getActivity(), getString(R.string.pref_folder) + " " + path + " " + getString(R.string.pref_err_writable), Toast.LENGTH_SHORT).show();

                else if (checker != null && !checker.canExecuteInDir(path))
                    Toast.makeText(getActivity(), getString(R.string.pref_folder) + " " + path + " " + getString(R.string.pref_err_executable), Toast.LENGTH_LONG).show();

                else {
                    //noinspection ConstantConditions
                    getPreferenceManager().getSharedPreferences().edit().putString(key, path).apply();
                    if (oldPath != null && !oldPath.equals(path)) {
                        File current = new File(oldPath);

                        if (current.exists() && current.isDirectory() && current.listFiles().length > 2) {
                            wipe_prompt_older(current);
                        }
                    }
                }
            }
        }

        private void startDirectoryPicker(Preference preference) {
            Intent i = new Intent(getActivity(), DirectoryPicker.class);
            i.putExtra(DirectoryPicker.AFFECTED_PREF, preference.getKey());
            startActivityForResult(i, DirectoryPicker.PICK_DIRECTORY);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String message = null;

            if (key.equals("PREF_SNIFFER_SAMPLE_TIME")) {
                double sampleTime;

                try {
                    sampleTime = Double.parseDouble(mSnifferSampleTime.getText());
                    if (sampleTime < 0.4 || sampleTime > 1.0) {
                        message = getString(R.string.pref_err_sample_time);
                        sampleTime = 1.0;
                    }
                } catch (Throwable t) {
                    message = getString(R.string.pref_err_invalid_number);
                    sampleTime = 1.0;
                }

                mSnifferSampleTime.setText(Double.toString(sampleTime));
            } else if (key.endsWith("_PORT")) {
                int port;

                try {
                    port = Integer.parseInt(mProxyPort.getText());
                    if (port < 1024 || port > 65536) {
                        message = getString(R.string.pref_err_port_range);
                        port = 0;
                    } else if (!System.isPortAvailable(port)) {
                        message = getString(R.string.pref_err_busy_port);
                        port = 0;
                    }
                } catch (Throwable t) {
                    message = getString(R.string.pref_err_invalid_number);
                    port = 0;
                }

                if (key.equals("PREF_HTTP_PROXY_PORT")) {
                    System.HTTP_PROXY_PORT = port;
                } else if (key.equals("PREF_HTTP_SERVER_PORT")) {
                    System.HTTP_SERVER_PORT = port;
                } else if (key.equals("PREF_HTTPS_REDIRECTOR_PORT")) {
                    System.HTTPS_REDIR_PORT = port;
                } else if (key.equals("MSF_RPC_PORT")) {
                    System.MSF_RPC_PORT = port;
                }

                if (port == 0) {
                    // reset to default value
                    port = getDefaultPortForKey(key);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(key, Integer.toString(port));
                    editor.apply();
                }
            } else if (key.equals("PREF_HTTP_MAX_BUFFER_SIZE")) {
                int maxBufferSize;

                try {
                    maxBufferSize = Integer.parseInt(mHttpBufferSize.getText());
                    if (maxBufferSize < 1024 || maxBufferSize > 104857600) {
                        message = getString(R.string.pref_err_buffer_size);
                        maxBufferSize = 10485760;
                    }
                } catch (Throwable t) {
                    message = getString(R.string.pref_err_invalid_number);
                    maxBufferSize = 10485760;
                }

                mHttpBufferSize.setText(Integer.toString(maxBufferSize));
            } else if (key.equals("PREF_PASSWORD_FILENAME")) {
                String passFileName;

                try {
                    passFileName = mPasswordFilename.getText();
                    if (!passFileName.matches("[^/?*:;{}\\]+]")) {
                        message = getString(R.string.invalid_filename);
                        passFileName = "csploit-password-sniff.log";
                    }
                } catch (Throwable t) {
                    message = getString(R.string.invalid_filename);
                    passFileName = "csploit-password-sniff.log";
                }

                mPasswordFilename.setText(passFileName);
            } else if (key.equals("MSF_ENABLED")) {
                if (mMsfEnabled.isChecked())
                    onMsfEnabled();
            } else if (key.equals("PREF_AUTO_PORTSCAN")) {
                Services.getNetworkRadar().onAutoScanChanged();
            }

            if (message != null)
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            System.onSettingChanged(key);
        }

        private int getDefaultPortForKey(String key) {
            switch (key) {
                case "PREF_HTTP_PROXY_PORT":
                    return 8080;
                case "PREF_HTTP_SERVER_PORT":
                    return 8081;
                case "PREF_HTTPS_REDIRECTOR_PORT":
                    return 8082;
                case "MSF_RPC_PORT":
                    return 55553;
                default:
                    return 0;
            }
        }

        private void onMsfEnabled() {
            // use mReceiver as "already did that"
            if (mReceiver != null)
                return;

            // start measureMsfSize ASAP
            onMsfPathChanged();

            Preference.OnPreferenceClickListener directoryPickerWithDefaultPath = new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    final String currentValue;
                    final String defaultValue;
                    final String key = preference.getKey();

                    switch (key) {
                        case "RUBY_DIR":
                            currentValue = System.getRubyPath();
                            defaultValue = System.getDefaultRubyPath();
                            break;
                        case "MSF_DIR":
                            currentValue = System.getMsfPath();
                            defaultValue = System.getDefaultMsfPath();
                            break;
                        default:
                            return true;
                    }

                    if (!currentValue.equals(defaultValue)) {
                        final Preference fPref = preference;
                        (new ChoiceDialog(
                                getActivity(),
                                getString(R.string.choose_an_option),
                                new String[]{getString(R.string.restore_default_path), getString(R.string.choose_a_custom_path)},
                                new ChoiceDialog.ChoiceDialogListener() {
                                    @Override
                                    public void onChoice(int choice) {
                                        if (choice == 0) {
                                            // create default directory if it does not exists
                                            File f = new File(defaultValue);
                                            if (!f.exists())
                                                f.mkdirs();
                                            // simulate directory picker
                                            Intent i = new Intent();
                                            i.putExtra(DirectoryPicker.AFFECTED_PREF, key);
                                            i.putExtra(DirectoryPicker.CHOSEN_DIRECTORY, defaultValue);
                                            onActivityResult(DirectoryPicker.PICK_DIRECTORY, AppCompatActivity.RESULT_OK, i);
                                        } else {
                                            startDirectoryPicker(fPref);
                                        }
                                    }
                                }
                        )).show();
                    } else {
                        startDirectoryPicker(preference);
                    }
                    return true;
                }
            };

            mRubyDir.setDefaultValue(System.getDefaultRubyPath());
            mRubyDir.setOnPreferenceClickListener(directoryPickerWithDefaultPath);

            mMsfDir.setDefaultValue(System.getDefaultMsfPath());
            mMsfDir.setOnPreferenceClickListener(directoryPickerWithDefaultPath);

            mWipeMSF.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    wipe_prompt();
                    return true;
                }
            });

            getMsfBranches();

            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(SETTINGS_WIPE_DONE)) {
                        onMsfPathChanged();
                    } else if (intent.getAction().equals(SETTINGS_MSF_BRANCHES_AVAILABLE)) {
                        onMsfBranchesAvailable();
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(SETTINGS_WIPE_DONE);
            filter.addAction(SETTINGS_MSF_BRANCHES_AVAILABLE);
            getActivity().registerReceiver(mReceiver, filter);
        }

        private void getMsfBranches() {
            if (mBranchesWaiter != null) { // run it once per settings activity
                if (mBranchesWaiter.getState() == Thread.State.TERMINATED)
                    try {
                        mBranchesWaiter.join();
                    } catch (InterruptedException e) {
                        Logger.error(e.getMessage());
                    }
                return;
            }

            mMsfBranch.setEnabled(false);
            mBranchesWaiter = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GitHubParser.getMsfRepo().getBranches();
                        getActivity().sendBroadcast(new Intent(SETTINGS_MSF_BRANCHES_AVAILABLE));
                    } catch (JSONException e) {
                        System.errorLogging(e);
                    } catch (IOException e) {
                        Logger.error(e.getMessage());
                    }
                }
            });
            mBranchesWaiter.start();
        }

        private void onMsfPathChanged() {
            measureMsfSize();
            boolean haveMsf = false;
            File dir;
            File[] content;

            if ((dir = new File(System.getRubyPath())).isDirectory() ||
                    (dir = new File(System.getMsfPath())).isDirectory()) {
                content = dir.listFiles();
                haveMsf = content != null && content.length > 2;
            }

            mWipeMSF.setEnabled(haveMsf);
        }

        private void onMsfBranchesAvailable() {
            String[] branches;
            boolean hasRelease = false;

            try {
                branches = GitHubParser.getMsfRepo().getBranches();
                mMsfBranch.setEntryValues(branches);
                mMsfBranch.setEntries(branches);
                for (int i = 0; !hasRelease && i < branches.length; i++) {
                    hasRelease = branches[i].equals("release");
                }
                mMsfBranch.setDefaultValue((hasRelease ? "release" : "master"));
                mMsfBranch.setEnabled(true);
            } catch (JSONException e) {
                System.errorLogging(e);
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    getActivity().onBackPressed();
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onDestroy() {
            if (mReceiver != null) {
                getActivity().unregisterReceiver(mReceiver);
                mReceiver = null;
            }
            super.onDestroy();
        }
    }

    public void onBackPressed() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.fadeout, R.anim.fadein);
    }
}
