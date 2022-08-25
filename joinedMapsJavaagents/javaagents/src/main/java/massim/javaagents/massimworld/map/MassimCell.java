package massim.javaagents.massimworld.map;


import massim.javaagents.massimworld.Coordinates;
import massim.javaagents.massimworld.game.Game;
import massim.javaagents.massimworld.map.things.*;

import java.util.Objects;

public class MassimCell {
	private final Coordinates coordinates;

	private int lastUpdatedStep;
	private boolean goalZone;
	private boolean roleZone;

	private Thing thing;


	public MassimCell(Coordinates coordinates, int currentStep) {
		this.coordinates = coordinates;
		this.lastUpdatedStep = currentStep;
	}

	public MassimCell(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public MassimCell(MassimCell other) {
		this.coordinates = other.getCoordinates();
		this.lastUpdatedStep = other.getLastUpdatedStep();
		this.goalZone = other.isGoalZone();
		this.roleZone = other.isRoleZone();
		this.thing = other.getThing();
	}

	public static MassimCell withCoordinates(Coordinates newCoordinates) {
		return new MassimCell(newCoordinates);
	}

	public MassimCell createCopyShiftedBy(Coordinates shift) {
		return copy(coordinates.withOffset(shift));
	}

	private MassimCell copy(Coordinates newCoordinates) {
		MassimCell newCell = new MassimCell(newCoordinates);
		newCell.lastUpdatedStep = lastUpdatedStep;
		newCell.goalZone = goalZone;
		newCell.roleZone = roleZone;
		newCell.thing = thing;
		return newCell;
	}

	public MassimCell update(MassimCell updateCell) {
		this.lastUpdatedStep = updateCell.getLastUpdatedStep();
		this.goalZone = updateCell.isGoalZone();
		this.roleZone = updateCell.isRoleZone();
		this.thing = updateCell.getThing();
		return this;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public int getLastUpdatedStep() {
		return lastUpdatedStep;
	}

	public void setLastUpdatedStep(int lastUpdatedStep) {
		this.lastUpdatedStep = lastUpdatedStep;
	}

	public boolean isGoalZone() {
		return goalZone;
	}

	public void setGoalZone(boolean goalZone) {
		this.goalZone = goalZone;
		modified();
	}

	public boolean isRoleZone() {
		return roleZone;
	}

	public void setRoleZone(boolean roleZone) {
		this.roleZone = roleZone;
		modified();
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
		modified();
	}

	private void modified() {
		lastUpdatedStep = Game.getCurrentStep();
	}

	public boolean isSame(MassimCell other) {
//		return goalZone == other.goalZone && roleZone == other.roleZone && coordinates.equals(other.coordinates) && Objects.equals(thing, other.thing);
		return goalZone == other.goalZone && roleZone == other.roleZone && coordinates.equals(other.coordinates) && Objects.equals(thing, other.thing);
	}

	public String printThing() {
		if (thing instanceof AgentThing) return "A";
		if (thing instanceof Obstacle) return "X";
		if (thing instanceof Block) return "B";
		if (thing instanceof Dispenser) return "D";
		return ".";
	}

	public void clear() {
		this.lastUpdatedStep = -1;
		this.goalZone = false;
		this.roleZone = false;
		this.thing = null;
	}

	public void updateWith(MassimCell update) {
		if (isOlderThan(update) && coordinates.equals(update.getCoordinates())) {
			clear();
			this.lastUpdatedStep = update.getLastUpdatedStep();
			this.goalZone = update.isGoalZone();
			this.roleZone = update.isRoleZone();
			this.thing = update.getThing();
			modified();
		}
	}

	public boolean isOlderThan(MassimCell update) {
		return lastUpdatedStep < update.getLastUpdatedStep();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MassimCell that = (MassimCell) o;
		return coordinates.equals(that.coordinates);
	}

}
