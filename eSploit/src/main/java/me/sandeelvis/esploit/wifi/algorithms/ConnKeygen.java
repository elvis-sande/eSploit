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
package me.sandeelvis.esploit.wifi.algorithms;

import java.util.List;

import me.sandeelvis.esploit.wifi.Keygen;

/*
 * This is not actual an algorithm as
 * it is just a default WEP password
 */
public class ConnKeygen extends Keygen{

  public ConnKeygen(String ssid, String mac, int level, String enc){
    super(ssid, mac, level, enc);
  }

  @Override
  public List<String> getKeys(){
    addPassword("1234567890123");
    return getResults();
  }
}
