package com.biel.FastSurvival.Dimensions.Sky.biomegen;

import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.HashMap;

/**
 * In order to find the biome of a region, we look up how close it's
 * conditions are to the conditions on this map. We can use it's proximity
 * to a biome to determine how much influence that biome's noise generators,
 * vegetation and features should have on the area of the map. This allows us
 * to have seamless transitions between the biomes (at the price of speed)
 * 
 * For example, a place with no precipitation and -30 degree (celcius) weather
 * is a tundra, but it is also much closer to a dessert than a rain forest.
 * 
 * There may be better methods of handling biomes out there (I don't know), 
 * but this is the only one I could come up with. Hopefully, in the comments below, 
 * you guys can propose some improvements to this method, but for now, this is 
 * the only method I can come up with.
 * 
 * One day (when I can finally figure it out, through the obfuscated mess), I'll make
 * a post about how the default generator works (hint hint, I'd love help hint).
 */
public enum Biomes {
	
	//We store the biome, handler and the temperature and rainfall for each biome.
	PARKOUR(Biome.DEEP_OCEAN, new DesertNoiseGenerator(), 70, 0),
	DESERT(Biome.DESERT, new DesertNoiseGenerator(), 70, 0),
	FOREST(Biome.FOREST, new ForestNoiseGenerator(), 50, 60),
	PLAINS(Biome.PLAINS, new PlainsNoiseGenerator(), 50, 30),
	SWAMP(Biome.SWAMP, new SwampNoiseGenerator(), 40, 70),
	HILLS(Biome.TAIGA, new HillsNoiseGenerator(), 50, 10);
	
	public final Biome biome;
	public final double optimumTemperature, optimumRainfall;
	public BiomeNoiseGenerator generator;
	
	private Biomes(Biome biome, BiomeNoiseGenerator generator, double temp, double rain) {
		this.biome = biome;
		this.generator = generator;
		this.optimumTemperature = temp;
		this.optimumRainfall = rain;
	}
	
	/** 
	 * Returns the mapping between the 3 closest biomes and "amount of the biome" in this location.
	 * This is just so that we can limit the amount of calculations we have to do.
	 * This could probably be cleaned up a bit
	 */
	public static HashMap<Biomes, Double> getBiomes(double temp, double rain) {
		//We tell it the capacity we need to avoid expensive dynamic lengthening
		HashMap<Biomes, Double> biomes = new HashMap<Biomes, Double>(3);
		
		Biomes closestBiome = null, secondClosestBiome = null, thirdClosestBiome = null;
		double closestDist = 10000000, secondClosestDist = 10000000, thirdClosestDist = 10000000;
		
		for (Biomes biome : Biomes.values()) {
			// To avoid having to do an expensive square root per biome per block, 
			// we just compare the square distances, and take the square root at the
			// end.
			double dist = getSquaredDistance(biome, temp, rain);
			
			if (dist <= closestDist) {
				thirdClosestDist = secondClosestDist; thirdClosestBiome = secondClosestBiome;
				secondClosestDist = closestDist; secondClosestBiome = closestBiome;
				closestDist = dist; closestBiome = biome;
			}
			
			else if (dist <= secondClosestDist) {
				thirdClosestDist = secondClosestDist; thirdClosestBiome = secondClosestBiome;
				secondClosestDist = dist; secondClosestBiome = biome;
			}
			
			else if (dist <= thirdClosestDist) {
				thirdClosestDist = dist; thirdClosestBiome = biome;
			}
		}
		
		// The 10 is just so that farther distances have less influence
		biomes.put(closestBiome, 10.0/Math.sqrt(closestDist));
		biomes.put(secondClosestBiome, 10.0/Math.sqrt(secondClosestDist));
		biomes.put(thirdClosestBiome, 10.0/Math.sqrt(thirdClosestDist));
		
		return biomes;
	}
	
	private static double getSquaredDistance(Biomes biome, double temp, double rain) {
		return Math.abs((biome.optimumTemperature-temp)*(biome.optimumTemperature-temp) + (biome.optimumRainfall-rain)*(biome.optimumRainfall-rain));
	}

	public static void setWorld(World world) {
		for (Biomes biome : Biomes.values()) {
			biome.generator.setWorld(world);
		}
		
	}
}
