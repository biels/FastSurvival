package com.biel.FastSurvival.Utils;

import com.biel.FastSurvival.FastSurvival;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileExtraction {


    public static boolean extractResourcesFolder(String folderName) {
        return extractResourcesFolder(folderName, true);
    }

    public static boolean extractResourcesFolder(String folderName, boolean overwrite) {
        try {
            //If folder exist, delete it.
            String destPathParent = FastSurvival.getPlugin().getDataFolder().toString() + File.separator;
            String destPath = destPathParent + File.separator + folderName;
            if(!overwrite && new File(destPath).exists()) return true;
            if (overwrite) FileUtils.deleteDirectory(new File(destPath));
            JarFile jarFile = new JarFile(new File(FastSurvival.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
            Enumeration<JarEntry> enums = jarFile.entries();
            while (enums.hasMoreElements()) {
                JarEntry entry = enums.nextElement();
                if (entry.getName().startsWith(folderName)) {
                    File toWrite = new File(destPathParent + entry.getName());
                    if (entry.isDirectory()) {
                        boolean mkdirs = toWrite.mkdirs();
                        continue;
                    }
                    InputStream in = new BufferedInputStream(jarFile.getInputStream(entry));
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(toWrite));
                    byte[] buffer = new byte[2048];
                    for (; ; ) {
                        int nBytes = in.read(buffer);
                        if (nBytes <= 0) {
                            break;
                        }
                        out.write(buffer, 0, nBytes);
                    }
                    out.flush();
                    out.close();
                    in.close();
                }
//                System.out.println(entry.getName());
            }
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(FileExtraction.class.getName()).log(Level.SEVERE, "Could not extract embedded file for " + folderName, ex);
            return false;
        }
        return true;
    }
//    public static void extractDefaults(){
//        File dataFolder = FastSurvival.getPlugin().getDataFolder();
//        if (!dataFolder.exists()) {
////            InputStream jarURL = FastSurvival.class.get
//            try {
//                copyFile(jarURL, new File(QuestsDIR + "QuestMaker.yml"));
//
//            } catch (Exception ex) {
//                Logger.getLogger(FastSurvival.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
}
