package com.unimelb.swen30006.metromadness.trains;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public abstract class Train {	
	
	// Logger
	private static Logger logger = LogManager.getLogger();
	// The state that a train can be in 
	public enum State {
		IN_STATION, READY_DEPART, ON_ROUTE, WAITING_ENTRY, FROM_DEPOT
	}

	// Constants
	public static final int MAX_TRIPS=4;
	public static final Color FORWARD_COLOUR = Color.ORANGE;
	public static final Color BACKWARD_COLOUR = Color.VIOLET;
	public static final float TRAIN_WIDTH=4;
	public static final float TRAIN_LENGTH = 6;
	public static final float TRAIN_SPEED=50f;
	
	public TrainOperator operator;
	
	// The train's name
	public String name;

	// Passenger Information
	public ArrayList<Passenger> passengers;
	public float departureTimer;
	
	// Position information
	public Point2D.Float pos;

	
	public Train(Line trainLine, Station start, boolean forward, String name){
		this.operator = new TrainOperator(trainLine, start, forward);
		this.passengers = new ArrayList<Passenger>();
		this.name = name;
	}

	public void update(float delta){
		operator.update(this, delta, logger);
	}

	public void move(float delta){
		// Work out where we're going
		float angle = angleAlongLine(this.pos.x,this.pos.y,operator.station.position.x,operator.station.position.y);
		float newX = this.pos.x + (float)( Math.cos(angle) * delta * TRAIN_SPEED);
		float newY = this.pos.y + (float)( Math.sin(angle) * delta * TRAIN_SPEED);
		this.pos.setLocation(newX, newY);
	}

	public abstract void embark(Passenger p) throws Exception;
	
	public ArrayList<Passenger> disembark(){
		ArrayList<Passenger> disembarking = new ArrayList<Passenger>();
		Iterator<Passenger> iterator = this.passengers.iterator();
		while(iterator.hasNext()){
			Passenger p = iterator.next();
			if(operator.station.shouldLeave(p)){
				logger.info("Passenger "+p.id+" is disembarking at "+operator.station.name);
				disembarking.add(p);
				iterator.remove();
			}
		}
		return disembarking;
	}

	@Override
	public String toString() {
		return "Train [line=" + operator.trainLine.name +", departureTimer=" + departureTimer + ", pos=" + pos + ", forward=" + operator.forward + ", state=" + operator.state
				+ ", numTrips=" + operator.numTrips + ", disembarked=" + operator.disembarked + "]";
	}
	
	public float angleAlongLine(float x1, float y1, float x2, float y2){	
		return (float) Math.atan2((y2-y1),(x2-x1));
	}
	
	public int getTotalCargoWeight() {
		int weight = 0;
		for (Passenger p: passengers) {
			weight += p.getCargo().getWeight();
		}
		return weight;
	}

	public void render(ShapeRenderer renderer){
		if(!operator.inStation()){
			Color col = operator.forward ? FORWARD_COLOUR : BACKWARD_COLOUR;
			renderer.setColor(col);
			renderer.circle(this.pos.x, this.pos.y, TRAIN_WIDTH);
		}
	}
	
}
