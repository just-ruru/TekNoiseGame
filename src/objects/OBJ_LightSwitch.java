package objects;


import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_LightSwitch extends SuperObject {

    public OBJ_LightSwitch() {
        name      = "Switch";
//        collision = true; // blocks movement AND registers checkObject hit

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/objects/LightSwitch.png"));
//            uTool.scaleImage(image, 48, 48);
        } catch (IOException e) {
            e.printStackTrace();
        }

        }

    }

