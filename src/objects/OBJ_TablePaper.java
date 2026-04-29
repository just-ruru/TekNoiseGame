package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_TablePaper extends SuperObject{
    public OBJ_TablePaper(){
        name = "TablePaper";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/TableWithPaper.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
//        If we want to set a specific collision solid Area
//        solidArea.x = 5;


        collision = true;
    }

}
