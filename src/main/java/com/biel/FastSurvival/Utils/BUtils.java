package com.biel.FastSurvival.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BUtils {
	@SuppressWarnings("deprecation")
	public static void fillBlocks(List<Block> blks, ArrayList<Material> ms, ArrayList<Byte> datas, ArrayList<Integer> chances){
		for(Block b : blks){
			Material m = null;
			if (chances == null){m = ms.get(0);}
			while(m == null){
				for(Material mat : ms){
					if (Utils.Possibilitat(chances.get(ms.indexOf(mat)), 1000)){
						m = mat;
					}
				}
			}
			b.setType(m);
			if (datas != null){
				// TODO Data not being set
//				b.setData(datas.get(ms.indexOf(m)));
			}
			
		}
	}
	public static void fillBlocks(List<Block> blks, ArrayList<Material> ms, ArrayList<Integer> chances){
		fillBlocks(blks, ms, null, chances);
	}
	public static void fillBlocks(List<Block> blks, Material m, byte data){
		ArrayList<Material> ms = new ArrayList<Material>();
		
		ms.add(m);
		if (data != 0){
			ArrayList<Byte> ds = new ArrayList<Byte>();
			ds.add(data);
			fillBlocks(blks, ms, ds, null);
		}else{
			fillBlocks(blks, ms, null, null);
		}
	}
	public static void fillBlocks(List<Block> blks, Material m){
		fillBlocks(blks, m, (byte)0);
	}
	public static List<Block> locListToBlock(List<Location> locs) {
		ArrayList<Block> blks = new ArrayList<Block>();
		for(Location l : locs){
			blks.add(l.getBlock());
		}
		return blks;
	}
		
}
