package object;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_Bag extends SuperObject{

    public OBJ_Bag(){
        name = "Bag";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/bag2.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
