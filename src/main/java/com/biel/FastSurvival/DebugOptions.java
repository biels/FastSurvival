package com.biel.FastSurvival;

import com.biel.FastSurvival.Utils.GestorPropietats;
import org.jetbrains.annotations.NotNull;

public class DebugOptions {
    static boolean debugEnabled = false;
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
