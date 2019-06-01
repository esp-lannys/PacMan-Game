package front;

import java.awt.Graphics2D;

import pacman.PacmanActor;
import pacman.PacmanGame;
import pacman.PacmanGame.State;

public class Initializer extends PacmanActor {

    public Initializer(PacmanGame game) {
        super(game);
    }

    @Override
    public void updateInitializing() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    instructionPointer = 2;
                case 2:
                    game.setState(State.PRESENTS);
                    break yield;
            }
        }
    }
    @Override
    public void draw(Graphics2D g){
    	super.draw(g);
    	if(game.state != State.TITLE){
    	game.drawText(g, "LOADING...", 78, 115);
    	}
    }
}
