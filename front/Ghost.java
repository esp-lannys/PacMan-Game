package front;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import back.ShortestPathFinder;
import front.Ghost.Mode;
import front.Ghost.PacmanCatchedAction;
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

	private int getTargetX(int col) {
		return col * 8 - 3 - 32;
	}

	private int getTargetY(int row) {
		return (row + 3) * 8 - 2;
	}

	public void updatePosition() {
		x = getTargetX(col);
		y = getTargetY(row);
	}

	private void updatePosition(int col, int row) {
		this.col = col;
		this.row = row;
		updatePosition();
	}

	private boolean moveToTargetPosition(int targetX, int targetY, int velocity) {
		int sx = (int) (targetX - x);
		int sy = (int) (targetY - y);
		int vx = Math.abs(sx) < velocity ? Math.abs(sx) : velocity;
		int vy = Math.abs(sy) < velocity ? Math.abs(sy) : velocity;
		int idx = vx * (sx == 0 ? 0 : sx > 0 ? 1 : -1);
		int idy = vy * (sy == 0 ? 0 : sy > 0 ? 1 : -1);
		x += idx;
		y += idy;
		return sx != 0 || sy != 0;
	}

	private boolean moveToGridPosition(int col, int row, int velocity) {
		int targetX = getTargetX(col);
		int targetY = getTargetY(row);
		return moveToTargetPosition(targetX, targetY, velocity);
	}

	private void adjustHorizontalOutsideMovement() {
		if (col == 1) {
			col = 34;
			x = getTargetX(col);
		} else if (col == 34) {
			col = 1;
			x = getTargetX(col);
		}
	}

	@Override
	public void updateTitle() {
		int frameIndex = 0;
		x = pacman.x + 17 + 17 * type;
		y = 200;
		if (pacman.direction == 0) {
			frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
		} else if (pacman.direction == 2) {
			frameIndex = 2 * pacman.direction + (int) (System.nanoTime() * 0.00000001) % 2;
		}
		frame = frames[frameIndex];
	}

	@Override
	public void updatePlaying() {
		switch (mode) {
		case CAGE:
			updateGhostCage();
			break;
		case NORMAL:
			updateGhostNormal();
			break;
		case VULNERABLE:
			updateGhostVulnerable();
			break;
		case DIED:
			updateGhostDied();
			break;
		}
		updateAnimation();
	}

	public void updateAnimation() {
		int frameIndex = 0;
		switch (mode) {
		case CAGE:
		case NORMAL:
			frameIndex = 2 * direction + (int) (System.nanoTime() * 0.00000001) % 2;
			if (!markAsVulnerable) {
				break;
			}
		case VULNERABLE:
			if (System.currentTimeMillis() - vulnerableModeStartTime > 5000) {
				frameIndex = 8 + (int) (System.nanoTime() * 0.00000002) % 4;
			} else {
				frameIndex = 8 + (int) (System.nanoTime() * 0.00000001) % 2;
			}
			break;
		case DIED:
			frameIndex = 12 + direction;
			break;
		}
		frame = frames[frameIndex];
	}

	private void updateGhostCage() {
		yield: while (true) {
			switch (instructionPointer) {
			case 0:
				Point initialPosition = initialPositions[type];
				updatePosition(initialPosition.x, initialPosition.y);
				x -= 4;
				cageUpDownCount = 0;
				if (type == 0) {
					instructionPointer = 6;
					break;
				} else if (type == 2) {
					instructionPointer = 2;
					break;
				}
				instructionPointer = 1;
			case 1:
				if (moveToTargetPosition((int) x, 134 + 4, 1)) {
					break yield;
				}
				instructionPointer = 2;
			case 2:
				if (moveToTargetPosition((int) x, 134 - 4, 1)) {
					break yield;
				}
				cageUpDownCount++;
				if (cageUpDownCount <= type * 2) {
					instructionPointer = 1;
					break yield;
				}
				instructionPointer = 3;
			case 3:
				if (moveToTargetPosition((int) x, 134, 1)) {
					break yield;
				}
				instructionPointer = 4;
			case 4:
				if (moveToTargetPosition((int) 105, 134, 1)) {
					break yield;
				}
				instructionPointer = 5;
			case 5:
				if (moveToTargetPosition((int) 105, 110, 1)) {
					break yield;
				}
				if ((int) (2 * Math.random()) == 0) {
					instructionPointer = 7;
					continue yield;
				}
				instructionPointer = 6;
			case 6:
				if (moveToTargetPosition((int) 109, 110, 1)) {
					break yield;
				}
				desiredDirection = 0;
				lastDirection = 0;
				updatePosition(18, 11);
				instructionPointer = 8;
				continue yield;
			case 7:
				if (moveToTargetPosition((int) 101, 110, 1)) {
					break yield;
				}
				desiredDirection = 2;
				lastDirection = 2;
				updatePosition(17, 11);
				instructionPointer = 8;
			case 8:
				setMode(Mode.NORMAL);
				break yield;
			}
		}
	}

	private PacmanCatchedAction pacmanCatchedAction = new PacmanCatchedAction();

	private void modeChanged() {
		instructionPointer = 0;
	}
}