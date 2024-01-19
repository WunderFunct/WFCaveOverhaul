package wftech.caveoverhaul.carvertypes;

/*
 * Extra carving to break up walls and other monotonous geography.
 * 
 * Every 8 y tiles, I check if I'm in a room. If I am, I shoot a ball in each of the X/Z directions. The ball will travel in the air (rocks cancel the ball)
 * for n steps. Upon colliding with a wall, the ball will "splat" and send 4 new balls out, in the relative directions of up, down, left, right.
 * These will look for 3 wide continuous ribbons. If they successfully travel m distance without any changes in the fact that there is a block there (no air
 * or changes), it will assume that a wall has been discovered and will request more carvings either at the center (where the splat occurred), or along
 * the spokes, or whatever.
 */
public class NoiseExtraCarve {

}
