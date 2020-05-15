package com.biel.FastSurvival.Utils;

import com.biel.FastSurvival.DebugOptions;
import com.biel.FastSurvival.FastSurvival;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HotReload {
    static BukkitTask task;
    static GestorPropietats gestorPropietats;

    public static void startWatching() {
        gestorPropietats = new GestorPropietats("hotreload.txt");
        boolean activeTask = gestorPropietats.ObtenirPropietatBoolean("reloadTask");
//        if (activeTask) return;
//        int lastTaskId = gestorPropietats.ObtenirPropietatInt("reloadTaskId");
//        AtomicBoolean found = new AtomicBoolean(false);
//        List<BukkitTask> pendingTasks = Bukkit.getScheduler().getPendingTasks();

//        pendingTasks.stream().filter(task -> {
//            return task.getTaskId() == lastTaskId;
//        }).findAny().ifPresent(bukkitTask -> {
//            if (bukkitTask.isCancelled()) return;
//            System.out.println("Previous task found! " + bukkitTask.getTaskId());
//            found.set(true);
//        });
//        if(found.get()) return;

        task = Bukkit.getScheduler().runTaskAsynchronously(FastSurvival.getPlugin(), new Runnable() {
            @Override
            public void run() {
                System.out.println("CREATING BUKKIT WATCHER TASK");
//                System.out.println("Thread Running");
                Path dir = Paths.get(DebugOptions.getSrcPath());
                try {
                    gestorPropietats.EstablirPropietat("reloadTask", true);
                    new WatchDir(dir, true, (s, path) -> null, (events) -> {
//                        Bukkit.getServer().reload();
                        Bukkit.getScheduler().runTask(FastSurvival.getPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                Bukkit.broadcastMessage(ChatColor.GOLD + "[FS] Compiling... ");
                                try {
                                    HotReload.deployPlugin();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Bukkit.broadcastMessage(ChatColor.GOLD + "[FS] Reloading... ");
                                Bukkit.getServer().reload();
                                Bukkit.broadcastMessage(ChatColor.GREEN + "[FS] Reloaded! ");
//                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "reload confirm");

                            }
                        });
                        return null;
                    }).processEvents();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void deployPlugin() throws IOException, InterruptedException {
        File dir = new File(DebugOptions.getServerPath());
        String cmd = "deploy-fs.cmd";
        Process process = Runtime.getRuntime().exec(cmd, null, dir);
        process.waitFor(10, TimeUnit.SECONDS);
    }

    public static void stopWatching() {
//        task.cancel();
    }


}
