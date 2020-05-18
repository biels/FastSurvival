package com.biel.FastSurvival.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FontRenderer {
    public static BufferedImage stringToBufferedImage(String s, int size) {
        //First, we have to calculate the string's width and height

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = img.getGraphics();
//        Stream.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).forEach(st -> System.out.println(st));
        //Set the font to be used when drawing the string
        Font f = new Font(Font.SANS_SERIF, Font.PLAIN, size);

        g.setFont(f);

        //Get the string visual bounds
        FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
        Rectangle2D rect = f.getStringBounds(s, frc);
        //Release resources
        g.dispose();

        //Then, we have to draw the string on the final image

        //Create a new image where to print the character
        img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
        g = img.getGraphics();
        g.setColor(Color.black); //Otherwise the text would be white
        g.setFont(f);

        //Calculate x and y for that string
        FontMetrics fm = g.getFontMetrics();
        int x = 0;
        int y = fm.getAscent(); //getAscent() = baseline
        g.drawString(s, x, y);

        //Release resources
        g.dispose();

        //Return the image
        return img;
    }

    public static void renderText(String s, Location start, Vector direction, Vector normal, int size, Material material) {
        BufferedImage img = stringToBufferedImage(s, size);
        System.out.println(img.getWidth());
        System.out.println(img.getHeight());
        List<Vector> vectors = new ArrayList<>();
        Vector xAxis = direction.normalize();
        Vector yAxis = direction.getCrossProduct(normal.normalize()).multiply(-1).normalize();
        Vector shiftVector =xAxis.clone().multiply(img.getWidth() / -2).add(yAxis.clone().multiply(img.getHeight() / 2));

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i, j);
                if (rgb != 0) {
                    vectors.add(shiftVector.clone().add(xAxis.clone().multiply(i)).add(yAxis.clone().multiply(-j)));
                }

            }
        }
        vectors.forEach(b -> {
            b.add(start.toVector()).toLocation(start.getWorld()).getBlock().setType(material);
        });
    }
}
