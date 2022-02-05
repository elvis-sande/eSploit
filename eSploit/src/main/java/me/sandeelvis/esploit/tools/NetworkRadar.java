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

import me.sandeelvis.esploit.core.ChildManager;
import me.sandeelvis.esploit.core.System;
import me.sandeelvis.esploit.core.Child;
import me.sandeelvis.esploit.core.Logger;
import me.sandeelvis.esploit.events.Event;
import me.sandeelvis.esploit.events.Host;
import me.sandeelvis.esploit.events.HostLost;

import java.net.InetAddress;

public class NetworkRadar extends Tool {

  public NetworkRadar() {
    mHandler = "network-radar";
    mCmdPrefix = null;
  }

  public static abstract class HostReceiver extends Child.EventReceiver {

    public abstract void onHostFound(byte[] macAddress, InetAddress ipAddress, String name);
    public abstract void onHostLost(InetAddress ipAddress);

    public void onEvent(Event e) {
      if ( e instanceof Host ) {
        Host h = (Host)e;
        onHostFound(h.ethAddress, h.ipAddress, h.name);
      } else if ( e instanceof HostLost ) {
        onHostLost(((HostLost)e).ipAddress);
      } else {
        Logger.error("Unknown event: " + e);
      }
    }
  }

  public Child start(HostReceiver receiver) throws ChildManager.ChildNotStartedException {
    String ifName;

    if(System.getNetwork() == null) {
      throw new ChildManager.ChildNotStartedException();
    }

    ifName = System.getNetwork().getInterface().getDisplayName();

    return async(ifName, receiver);
  }
}
