package infra;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Game {
    
    public Dimension screenSize;
    public Point2D screenScale;
    
    public List<Actor> actors = new ArrayList<Actor>();
    public BitmapFontRenderer bitmapFontRenderer = new BitmapFontRenderer("/res/font8x8.png", 16, 16);

    public void init() {
    }
    
    public void update() {
        for (Actor actor : actors) {
            actor.update();
        }
    }
    
    public void draw(Graphics2D g) {
        for (Actor actor : actors) {
            actor.draw(g);
        }
    }

    public void broadcastMessage(String message) {
        for (Actor obj : actors) {
            try {
                Method method = obj.getClass().getMethod(message);
                if (method != null) {
                    method.invoke(obj);
                }
            } catch (Exception ex) {
            }
        }
    }

    public void drawText(Graphics2D g, String text, int x, int y) {
        bitmapFontRenderer.drawText(g, text, x, y);
    }
    
}
