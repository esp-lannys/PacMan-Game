package front;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import back.ShortestPathFinder;
import pacman.PacmanActor;
import pacman.PacmanGame;
import pacman.PacmanGame.State;

public class Ghost extends PacmanActor {

	public Pacman pacman;
	public int type;
	public Point[] initialPositions = { new Point(18, 11), new Point(16, 14), new Point(18, 14), new Point(20, 14) };
	public int cageUpDownCount;

	public static enum Mode {
		CAGE, NORMAL, VULNERABLE, DIED
	}

	public Mode mode = Mode.CAGE;

	public int dx;
	public int dy;
	public int col;
	public int row;

	public int direction = 0;
	public int lastDirection;

	public List<Integer> desiredDirections = new ArrayList<Integer>();
	public int desiredDirection;
	public static final int[] backwardDirections = { 2, 3, 0, 1 };

	public long vulnerableModeStartTime;
	public boolean markAsVulnerable;

	// in this version, i'm using path finder just to return the ghost to the center
	// (cage)
	public ShortestPathFinder pathFinder;

	public Ghost(PacmanGame game, Pacman pacman, int type) {
		super(game);
		this.pacman = pacman;
		this.type = type;
		this.pathFinder = new ShortestPathFinder(game.maze);
	}

	private void setMode(Mode mode) {
		this.mode = mode;
		modeChanged();
	}

	@Override
	public void init() {
		String[] ghostFrameNames = new String[8 + 4 + 4];
		for (int i = 0; i < 8; i++) {
			ghostFrameNames[i] = "/res/ghost_" + type + "_" + i + ".png";
		}
		for (int i = 0; i < 4; i++) {
			ghostFrameNames[8 + i] = "/res/ghost_vulnerable_" + i + ".png";
		}
		for (int i = 0; i < 4; i++) {
			ghostFrameNames[12 + i] = "/res/ghost_died_" + i + ".png";
		}
		loadFrames(ghostFrameNames);
		collider = new Rectangle(0, 0, 8, 8);
		setMode(Mode.CAGE);
	}
	private void modeChanged() {
		instructionPointer = 0;
	}
}