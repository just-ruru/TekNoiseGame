package objects;

import javax.imageio.ImageIO;
import java.io.IOException;

public class OBJ_TablePaper extends SuperObject{
    public OBJ_TablePaper(){
        name = "TableWithPaper";
        try{
            image = ImageIO.read(getClass().getResourceAsStream("/objects/TableWithPaper.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
