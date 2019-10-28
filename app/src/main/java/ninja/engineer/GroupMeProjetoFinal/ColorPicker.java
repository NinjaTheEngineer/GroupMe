package ninja.engineer.GroupMeProjetoFinal;

import java.util.Random;

public class ColorPicker{
    private final static String[] colors = new String[] {"#b1ffb0", "#660002", "#191970", "#f38630", "#057e95", "#7bb020"};

    private final static Random random = new Random();

    public static String next()
    {
        return colors[random.nextInt(colors.length - 1 )];
    }
}
