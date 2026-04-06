package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_LightSwitch extends SuperObject{
    public OBJ_LightSwitch(){
        name = "LightSwitch";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/LightSwitch.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
