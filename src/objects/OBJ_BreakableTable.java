package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_BreakableTable extends SuperObject {
    public OBJ_BreakableTable() {
        this(1);
    }

    public OBJ_BreakableTable(int variant) {
        name = "BreakableTable";
        collision = true;

        try {
            int safeVariant = Math.max(1, Math.min(4, variant));
            image = ImageIO.read(getClass().getResourceAsStream(
                    "/objects/breakable/breakabletable" + safeVariant + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
