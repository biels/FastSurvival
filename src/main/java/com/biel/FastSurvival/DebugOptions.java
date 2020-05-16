package com.biel.FastSurvival;

import com.biel.FastSurvival.Utils.GestorPropietats;
import com.biel.FastSurvival.Utils.TestArea;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DebugOptions {
    static boolean debugEnabled = false;
    static Map<World, TestArea> testAreaMap = new HashMap<>();
    public static TestArea getTestArea(World world){
        return testAreaMap.computeIfAbsent(world, TestArea::new);
    }
    public static boolean isDebugEnabled () {
        return getDebugGestorPropietats().ObtenirPropietatBoolean("debug");
    }
    public static String getServerPath () {
        return getDebugGestorPropietats().ObtenirPropietat("serverPath");
    }
    public static String getSrcPath () {
        return getDebugGestorPropietats().ObtenirPropietat("srcPath");
    }
    @NotNull
    public static GestorPropietats getDebugGestorPropietats() {
        return new GestorPropietats("debug.txt");
    }

    public static boolean skyGenerationMode(){
        return true;
    }
    public static boolean moonGenerationMode(){
        return false;
    }
}
