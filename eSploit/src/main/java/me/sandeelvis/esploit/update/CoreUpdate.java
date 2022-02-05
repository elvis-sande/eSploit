package me.sandeelvis.esploit.update;

import android.content.Context;

import me.sandeelvis.esploit.R;
import me.sandeelvis.esploit.core.System;

/**
 * A core update
 */
public class CoreUpdate extends Update {
  public CoreUpdate(Context context, String url, String version) {
    this.url = url;
    this.version = version;
    name = "core.tar.xz";
    path = String.format("%s/%s", System.getStoragePath(), name);
    archiver = Update.archiveAlgorithm.tar;
    compression = Update.compressionAlgorithm.xz;
    executableOutputDir = outputDir = System.getCorePath();
    prompt = String.format(context.getString(R.string.new_core_found), version);
  }
}
