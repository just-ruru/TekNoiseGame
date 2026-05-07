package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Axe extends SuperObject {
    public OBJ_Axe() {
        name = "Axe";

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Axe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
