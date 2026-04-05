package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Bag extends object.SuperObject {
    public OBJ_Bag(){
        name = "Bag";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/Bag3.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}




