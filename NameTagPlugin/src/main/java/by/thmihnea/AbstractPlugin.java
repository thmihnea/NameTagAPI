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

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public abstract class AbstractPlugin extends JavaPlugin {

    /**
     * The abstract class' instance.
     */
    private static AbstractPlugin instance;

    /**
     * World Directory of player worlds so we can save their Hideouts
     */
    private static File hideoutDirectory;

    /**
     * This is just a shortcut for getDescription().getVersion()
     *
     * @return plugin version
     */
    public String getVersion() {
        return getDescription().getVersion();
    }

    /**
     * This is just a shortcut for getDataFolder()
     *
     * @return plugin data folder
     */
    public File getData() {
        return getDataFolder();
    }

    /**
     * This is just a short for getFile()
     *
     * @return plugin source file (jar file)
     */
    public File getSourceFile() {
        return getFile();
    }

    /**
     * Returns the instance of {@link AbstractPlugin}
     *
     * @return this instance
     */
    public static AbstractPlugin getInstance() {
        if (instance == null) {
            try {
                instance = JavaPlugin.getPlugin(AbstractPlugin.class);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().log(Level.SEVERE, "Failed to instantiate plugin. Please, make sure you installed everything correctly!");
            }
        }
        return instance;
    }

    /**
     * @return if instance is set
     */
    public static boolean hasInstance() {
        return instance != null;
    }

    /**
     * Call this method when the plugin is started.
     * {@link JavaPlugin#onLoad()}
     */
    protected void load() {
    }

    /**
     * Call this method before the plugin gets loaded.
     */
    protected void preStart() {
    }

    /**
     * This is the main starting method of the plugin. Replaces onEnable.
     * {@link JavaPlugin#onEnable()}
     */
    protected abstract void start();

    /**
     * This is the main stopping method of the plugin. Replaces onDisable.
     * {@link JavaPlugin#onDisable()}
     */
    protected abstract void stop();

    /**
     * Invoked after plugin is reloaded.
     */
    protected void reload() {
    }

    /**
     * Invoked before plugin is reloaded.
     */
    protected void preReload() {
    }

    /**
     * Here we override our onLoad method to apply our own methods to it.
     * {@link #onLoad()}
     */
    @Override
    public void onLoad() {
        this.load();
    }

    /**
     * Here we override our onEnable method to apply our own methods to it.
     * {@link #onEnable()}
     */
    @Override
    public void onEnable() {
        if (!isEnabled()) return;
        this.preStart();
        if (!isEnabled()) return; // Detects if an error occurred during plugin prestart.
        this.start();
    }

    /**
     * Here we override our onDisable method to apply our own methods to it.
     * {@link #onDisable()}
     */
    @Override
    public void onDisable() {
        this.stop();
    }

    /**
     * Registers an event if a certain condition is met
     *
     * @param listener  - Listener to be registered
     * @param condition - Condition to be met when registering wanted listener
     */
    protected final void registerEventIf(Listener listener, boolean condition) {
        if (condition) this.registerEvent(listener);
    }

    /**
     * Registers an event
     *
     * @param listener - Listener to be registered
     */
    protected final void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    /**
     * Registers a list of listeners
     *
     * @param listeners - The list of listeners which have to be registered
     */
    protected final void registerEvents(List<Listener> listeners) {
        listeners.forEach(this::registerEvent);
    }

    /**
     * Registers each listener in a list of listeners if a certain condition is met
     *
     * @param listeners - The list of listeners which have to be registered
     * @param condition - Condition to be met when registering wanted listener
     */
    protected final void registerEventsIf(List<Listener> listeners, boolean condition) {
        listeners.forEach(listener -> this.registerEventIf(listener, condition));
    }

    /**
     * Logs a message to the console using the INFO Level
     *
     * @param message - Message to be logged
     */
    public void logInfo(String... message) {
        String s = Arrays.toString(message);
        Bukkit.getLogger().log(Level.INFO, "[" + getName() + "]" + " " + s.replace(s.substring(0, 1), "").replace(s.substring(s.length() - 1), ""));
    }

    /**
     * Logs a message to the console using the SEVERE Level
     *
     * @param message - Message to be logged
     */
    public void logSevere(String... message) {
        String s = Arrays.toString(message);
        Bukkit.getLogger().log(Level.INFO, "[" + getName() + "]" + " " + s.replace(s.substring(0, 1), "").replace(s.substring(s.length() - 1), ""));
    }

    public void disable() {
        this.setEnabled(false);
    }
}