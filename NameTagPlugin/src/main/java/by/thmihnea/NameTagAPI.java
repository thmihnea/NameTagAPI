/*
Copyright (c) thmihnea <mihneathm@gmail.com>
Copyright (c) contributors

This file is part of NameTagAPI, licensed under the MIT License.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE..
 */

package by.thmihnea;

import by.thmihnea.cache.CacheManager;
import by.thmihnea.cache.EntityIDCacheManager;
import by.thmihnea.cache.LineCacheManager;
import by.thmihnea.listener.PlayerJoinListener;
import by.thmihnea.listener.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class NameTagAPI extends AbstractPlugin {

    /**
     * Variable which allows us to track
     * how much time the plugin spends in order
     * to get enabled.
     */
    private long timeEnabled;

    /**
     * NMS version which the server
     * is currently running with.
     */
    private String version;

    /**
     * The list of {@link Listener} which
     * has to be registered.
     */
    private final List<Listener> listeners = Arrays.asList(
            new PlayerJoinListener(),
            new PlayerQuitListener()
    );

    /**
     * An instance of the
     * {@link INMSHandler} interface.
     */
    private static INMSHandler handler;

    /**
     * Returns an instance of the
     * {@link INMSHandler} interface, after the
     * plugin has been initialized for the
     * correct NMS version.
     * @return {@link INMSHandler}
     */
    public static INMSHandler getNMSHandler() {
        return handler;
    }

    /**
     * Our Main class instance.
     * Will maybe become useful in the future
     * for outside users.
     * Right now, it is heavily used for the
     * PacketUpdateTasks.
     */
    private static NameTagAPI instance;

    /**
     * Returns the instance of our main class.
     * @return {@link NameTagAPI}
     */
    public static NameTagAPI getInstance() {
        return instance;
    }

    /**
     * Overwritten start method as can be seen
     * in our {@link AbstractPlugin} class.
     */
    @Override
    protected void start() {
        this.setupTime();
        this.setupInstance();
        this.registerEvents(this.listeners);
        this.initModules();
        this.setupPipelineMonitoring();
        this.logInfo("The NameTagAPI has been successfully initialized. Initialization took a total of: " + (System.currentTimeMillis() - this.timeEnabled) + "ms");
    }

    /**
     * Overwritten stop method as can be seen
     * in our {@link AbstractPlugin} class.
     */
    @Override
    protected void stop() {
        this.logInfo("The NameTagAPI has been successfully disabled. Goodbye!");
    }

    /**
     * Initializes NMS modules using version
     * reflection.
     */
    private void initModules() {
        this.logInfo("Attempting to load net.minecraft.server methods using your server version.");
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        this.version = version;
        try {
            Class<?> clazz = Class.forName("by.thmihnea.nms." + version + ".NMSHandler");
            if (INMSHandler.class.isAssignableFrom(clazz))
                handler = (INMSHandler) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            this.logInfo("Couldn't initialize NMS for the Spigot version which you're running. (" + version + "). Please, contact thmihnea!");
            this.setEnabled(false);
            return;
        }
        this.logInfo("Sucessfully loaded net.minecraft.server support. You're running on version: " + version + ".");
    }

    /**
     * Sets up the static plugin
     * instance.
     */
    private void setupInstance() {
        instance = this;
    }

    /**
     * Sets up the time at which the
     * plugin has been enabled.
     */
    private void setupTime() {
        this.timeEnabled = System.currentTimeMillis();
    }

    /**
     * Initializes io.netty pipeline
     * monitoring for each {@link org.bukkit.entity.Player}.
     */
    private void setupPipelineMonitoring() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            getNMSHandler().abortPipelineMonitoring(player);
            getNMSHandler().startPipelineMonitoring(player);
        });
    }

    /**
     * Returns the NMS version which
     * the server is currently running on.
     * @return {@link String}
     */
    public String getVersion() {
        return this.version;
    }
}
