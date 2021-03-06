package com.biel.FastSurvival.Utils;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class MazeGenerator {
	private final int x;
	private final int y;
	private final int[][] maze;
 
	public MazeGenerator(int x, int y) {
		this.x = x;
		this.y = y;
		maze = new int[this.x][this.y];
		generateMaze(0, 0);
	}
 
	public void display(Location loc) {
		ArrayList<Location> lcs = new ArrayList<Location>();
		ArrayList<Location> lcs2 = new ArrayList<Location>();

		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				int pixel = maze[i][j];
				System.out.print(pixel + " ");
//				Optional<DIR> first = Arrays.stream(DIR.values()).filter(d -> d.bit == pixel).findFirst();
//				first.ifPresent(d -> System.out.print(first + " "));
				loc.clone().add(i * 2, 0, j * 2).getBlock().setType(pixel == 0 ? Material.AIR : Material.GOLD_BLOCK);
			}
			System.out.println("");
		}


		// Old
		for (int i = 0; i < y; i++) {
			// draw the north edge
			for (int j = 0; j < x; j++) {
				System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
				Location l0 = loc.clone().add(j * 2, 0, 0);
				lcs2.add(l0);
				if ((maze[j][i] & 1) == 0){
					Location l1 = loc.clone().add(j * 2 + 1, 0, 0);
					lcs.add(l1);
					Location l2 = l1.clone().add(1, 0, 0);
					lcs.add(l2);

				}
			}
			System.out.println("+");
			// draw the west edge
			for (int j = 0; j < x; j++) {
				System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
				if ((maze[j][i] & 8) == 0){
					Location l0 = loc.clone().add(0, 0, i * 2);
					lcs2.add(l0);
					Location l1 = loc.clone().add(0, 0, i * 2 + 1);
					lcs.add(l1);
					Location l2 = loc.clone().add(0, 0, i * 2 + 2);
					lcs.add(l2);
				}
			}
			System.out.println("|");
		}
		// draw the bottom line
		for (int j = 0; j < x; j++) {
			System.out.print("+---");

		}
		System.out.println("+");
//		BUtils.fillBlocks(BUtils.locListToBlock(lcs), Material.GOLD_BLOCK);
//		BUtils.fillBlocks(BUtils.locListToBlock(lcs2), Material.DIAMOND_BLOCK);
	}
 
	private void generateMaze(int cx, int cy) {
		DIR[] dirs = DIR.values();
		Collections.shuffle(Arrays.asList(dirs));
		for (DIR dir : dirs) {
			int nx = cx + dir.dx;
			int ny = cy + dir.dy;
			if (between(nx, x) && between(ny, y)
					&& (maze[nx][ny] == 0)) {
				maze[cx][cy] |= dir.bit;
				maze[nx][ny] |= dir.opposite.bit;
				generateMaze(nx, ny);
			}
		}
	}
 
	private static boolean between(int v, int upper) {
		return (v >= 0) && (v < upper);
	}
 
	private enum DIR {
		N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
		private final int bit;
		private final int dx;
		private final int dy;
		private DIR opposite;
 
		// use the static initializer to resolve forward references
		static {
			N.opposite = S;
			S.opposite = N;
			E.opposite = W;
			W.opposite = E;
		}
 
		private DIR(int bit, int dx, int dy) {
			this.bit = bit;
			this.dx = dx;
			this.dy = dy;
		}
	};
 
//	public static void main(String[] args) {
//		int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 8;
//		int y = args.length == 2 ? (Integer.parseInt(args[1])) : 8;
//		MazeGenerator maze = new MazeGenerator(x, y);
//		maze.display();
//	}
 
}