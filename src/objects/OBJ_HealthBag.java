package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_HealthBag extends SuperObject {
    public OBJ_HealthBag() {
        name = "HealthBag";

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/HealthBag.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
