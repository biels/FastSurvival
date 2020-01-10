
/**
 * API for hashing sequential data and zero-allocation, pretty fast implementations
 * of non-cryptographic hash functions.
 *
 * <p>Currently implemented (in alphabetical order):
 * <ul>
 *     <li>{@code long}-valued functions: see {@link com.biel.FastSurvival.Utils.Hashing.LongHashFunction}
 *     <ul>
 *         <li>
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#city_1_1() CityHash 1.1 without seeds},
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#city_1_1(long) with one seed} and
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#city_1_1(long, long) with two seeds}.
 *         </li>
 *         <li>
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#farmNa() FarmHash 1.0 (farmhashna)
 *         without seed}, {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#farmNa(long) with one
 *         seed} and {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#farmNa(long, long) with
 *         two seeds}.
 *         </li>
 *         <li>
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#farmUo() FarmHash 1.1 (farmhashuo)
 *         without seed}, {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#farmUo(long) with one
 *         seed} and {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#farmUo(long, long) with
 *         two seeds}.
 *         </li>
 *         <li>
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#metro() MetroHash without seed} and
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#metro(long) with a seed}.
 *         </li>
 *         <li>
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#murmur_3() MurmurHash3 without seed} and
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#murmur_3(long) with a seed}.
 *         </li>
 *         <li>
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#wy_3() WyHash v3 without seed} and
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#wy_3(long) with a seed}.
 *         </li>
 *         <li>
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#xx() xxHash without seed} and
 *         {@linkplain com.biel.FastSurvival.Utils.Hashing.LongHashFunction#xx(long) with a seed}.
 *         </li>
 *     </ul>
 *     </li>
 * </ul>
 */
package com.biel.FastSurvival.Utils.Hashing;