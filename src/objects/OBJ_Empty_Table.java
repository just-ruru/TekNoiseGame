package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Empty_Table extends SuperObject {
    public OBJ_Empty_Table() {
        name = "Empty_Table";
        collision = true;

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Empty_Table.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
