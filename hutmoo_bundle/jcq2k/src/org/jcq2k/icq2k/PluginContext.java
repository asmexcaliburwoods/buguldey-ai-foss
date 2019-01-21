package org.jcq2k.icq2k;

/**
  Contains miscelanneous objects for the plugin instance context.
  <p>
  At the moment, contains a ICQ2KMessagingNetwork instance only.
  <p>
  Additionally, the ResourceManager instance can be got from the
  ICQ2KMessagingNetwork instance.
*/
public final class PluginContext
{
  private final ICQ2KMessagingNetwork plugin;
  public PluginContext(ICQ2KMessagingNetwork plugin)
  {
    org.jcq2k.util.joe.Lang.ASSERT_NOT_NULL(plugin, "plugin");
    this.plugin = plugin;
  }

  public final ICQ2KMessagingNetwork getICQ2KMessagingNetwork()
  {
    return plugin;
  }

  public final ResourceManager getResourceManager()
  {
    return plugin.getResourceManager();
  }
}