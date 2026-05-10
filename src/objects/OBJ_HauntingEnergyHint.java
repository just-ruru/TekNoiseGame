package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_HauntingEnergyHint extends SuperObject {
    public OBJ_HauntingEnergyHint() {
        name = "HauntingEnergyHint";
        collision = true;
        try {
            // Using tile 2 image as requested
            image = ImageIO.read(getClass().getResourceAsStream("/tiles/2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
