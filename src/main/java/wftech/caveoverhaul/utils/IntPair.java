package wftech.caveoverhaul.utils;

/*
ChatGPT generated. You have been warned.
I'm just sick of my code doing backflips when I'm not looking. It's 1 AM. I want sleep.
Sorry guys, I had to!
 */
public class IntPair {
    private final int x;
    private final int y;

    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntPair intPair = (IntPair) o;
        return x == intPair.x && y == intPair.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}