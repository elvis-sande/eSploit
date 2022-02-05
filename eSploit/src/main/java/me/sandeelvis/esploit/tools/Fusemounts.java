/*
 * This file is part of the cSploit.
 *
 * Copyleft of Massimo Dragano aka tux_mind <tux_mind@csploit.org>
 *
 * cSploit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cSploit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cSploit.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.sandeelvis.esploit.tools;

import me.sandeelvis.esploit.core.Child;
import me.sandeelvis.esploit.core.ChildManager;
import me.sandeelvis.esploit.core.Logger;
import me.sandeelvis.esploit.events.Event;
import me.sandeelvis.esploit.events.FuseBind;

public class Fusemounts extends Tool
{
  public Fusemounts() {
    mHandler = "fusemounts";
    mCmdPrefix = null;
  }

  public static abstract class fuseReceiver extends Child.EventReceiver {

    @Override
    public void onEvent(Event e) {
      if(e instanceof FuseBind) {
        FuseBind f = (FuseBind)e;
        onNewMountpoint(f.source, f.mountpoint);
      } else {
        Logger.error("unknown event: " + e);
      }
    }

    public abstract void onNewMountpoint(String source, String mountpoint);
  }

  public Child getSources(fuseReceiver receiver) throws ChildManager.ChildNotStartedException {
    return super.async(receiver);
  }
}