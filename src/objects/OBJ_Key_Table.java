package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Key_Table extends SuperObject {
    public OBJ_Key_Table() {
        name = "Key_Table";
        collision = true;

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Key_Table.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
