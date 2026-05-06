package objects;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class OBJ_Candle extends SuperObject {
    private final int candleNumber;
    private boolean lit = false;
    private BufferedImage offImage;
    private BufferedImage onImage;

    public OBJ_Candle(int candleNumber) {
        this.candleNumber = candleNumber;
        name = "Candle";

        try {
            offImage = ImageIO.read(getClass().getResourceAsStream("/objects/Candleoff.png"));
            onImage = ImageIO.read(getClass().getResourceAsStream("/objects/Candleon.png"));
            image = offImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCandleNumber() {
        return candleNumber;
    }

    public boolean isLit() {
        return lit;
    }

    public void setLit(boolean lit) {
        this.lit = lit;
        image = lit ? onImage : offImage;
    }

    public void turnOff() {
        setLit(false);
    }
}
