package me.sandeelvis.esploit.net.datasource;

import me.sandeelvis.esploit.core.Logger;
import me.sandeelvis.esploit.net.RemoteReader;
import me.sandeelvis.esploit.net.reference.Link;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * set a reference summary from HTTP title
 */
class Generic {
  public static class Receiver implements RemoteReader.Receiver {
    private static final Pattern TITLE = Pattern.compile("<title>(.*?)</title>", Pattern.DOTALL | Pattern.MULTILINE);
    private final Link link;

    public Receiver(Link link) {
      this.link = link;
    }

    @Override
    public void onContentFetched(byte[] content) {
      String html = new String(content);
      Matcher matcher = TITLE.matcher(html);

      if(!matcher.find())
        return;

      link.setName(new String(matcher.group(1).toCharArray()));
    }

    @Override
    public void onError(byte[] description) {
      Logger.warning(String.format("%s: %s", link.getUrl(), new String(description)));
    }
  }
}