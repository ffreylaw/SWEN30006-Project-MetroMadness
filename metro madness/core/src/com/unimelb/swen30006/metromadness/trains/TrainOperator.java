package com.unimelb.swen30006.metromadness.trains;

import java.awt.geom.Point2D;

import org.apache.logging.log4j.Logger;

import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.CargoStation;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;
import com.unimelb.swen30006.metromadness.tracks.Track;
import com.unimelb.swen30006.metromadness.trains.Train.State;

public class TrainOperator {
	
	// The line that this is traveling on
	public Line trainLine;
	
	// Current station and track
	public Station station; 
	public Track track;
	
	// State variables
	public boolean forward;
	public State state;
	public int numTrips;
	public boolean disembarked;
	public State previousState = null;
	
	public TrainOperator(Line trainLine, Station start, boolean forward) {
		this.trainLine = trainLine;
		this.station = start;
		this.forward = forward;
		this.state = State.FROM_DEPOT;
	}
		
	public void update(Train train, float delta, Logger logger){
		// Update all passengers
		for(Passenger p: train.passengers){
			p.update(delta);
		}
		boolean hasChanged = false;
		if(previousState == null || previousState != this.state){
			previousState = this.state;
			hasChanged = true;
		}
		
		// Update the state
		switch(this.state) {
		case FROM_DEPOT:
			if(hasChanged){
				logger.info(train.name+ " is travelling from the depot: "+this.station.name+" Station...");
			}
			
			// We have our station initialized we just need to retrieve the next track, enter the
			// current station officially and mark as in station
			try {
				if(this.station.canEnter(this.trainLine)){
					
					train.pos = (Point2D.Float) this.station.position.clone();
					
					if ((train instanceof CargoTrain && this.station instanceof CargoStation) || !(train instanceof CargoTrain)) {
						this.station.enter(train);
						this.state = State.IN_STATION;
						this.disembarked = false;
					} else {
						try {
							this.track = this.trainLine.nextTrack(this.station, this.forward);
							
							Station next = this.trainLine.nextStation(this.station, this.forward);
							this.station = next;
							
							this.track.enter(train);
							this.state = State.ON_ROUTE;
						} catch (Exception e){
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case IN_STATION:
			if(hasChanged){
				logger.info(train.name+" is in "+this.station.name+" Station.");
			}
			
			// When in station we want to disembark passengers 
			// and wait 10 seconds for incoming passengers
			if(!this.disembarked){
				train.disembark();
				train.departureTimer = this.station.getDepartureTime();
				this.disembarked = true;
			} else {
				// Count down if departure timer. 
				if(train.departureTimer>0){
					train.departureTimer -= delta;
				} else {
					// We are ready to depart, find the next track and wait until we can enter 
					try {
						boolean isStart = this.trainLine.isStartStation(this.station);
						boolean isEnd = this.trainLine.isEndStation(this.station);
						if (isStart) {
							this.forward = true;
						} else if (isEnd) {
							this.forward = false;
						}
						this.track = this.trainLine.nextTrack(this.station, this.forward);
						this.state = State.READY_DEPART;
						break;
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			break;
		case READY_DEPART:
			// When ready to depart, check that the track is clear and if
			// so, then occupy it if possible.
			if(this.track.canEnter(this.forward) && this.hasValidDestination(train)){
				try {
					// Find the next
					Station next = this.trainLine.nextStation(this.station, this.forward);
					// Depart our current station
					this.station.depart(train);
					this.station = next;

				} catch (Exception e) {
					e.printStackTrace();
				}
				this.track.enter(train);
				this.state = State.ON_ROUTE;
				
				if(hasChanged){
					logger.info(train.name+ " is ready to depart for "+this.station.name+" Station!");
				}
			}
			break;
		case ON_ROUTE:
			if(hasChanged){
				logger.info(train.name+ " enroute to "+this.station.name+" Station!");
			}
			
			// Checkout if we have reached the new station
			if(train.pos.distance(this.station.position) < 10 ){
				this.state = State.WAITING_ENTRY;
			} else {
				train.move(delta);
			}
			break;
		case WAITING_ENTRY:
			// Check if entering an invalid station
			if (train instanceof CargoTrain && !(this.station instanceof CargoStation)) {
				try {
					Station next = this.trainLine.nextStation(this.station, this.forward);
					this.station = next;
					
					boolean isStart = this.trainLine.isStartStation(this.station);
					boolean isEnd = this.trainLine.isEndStation(this.station);
					if (isStart) {
						this.forward = true;
					} else if (isEnd) {
						this.forward = false;
					}
					
					this.track = this.trainLine.nextTrack(this.station, this.forward);
					this.track.enter(train);
					
					this.state = State.ON_ROUTE;
				} catch (Exception e){
					e.printStackTrace();
				}
				return;
			}
			
			if(hasChanged){
				logger.info(train.name+ " is awaiting entry "+this.station.name+" Station..!");
			}
			
			// Waiting to enter, we need to check the station has room and if so
			// then we need to enter, otherwise we just wait
			try {
				if(this.station.canEnter(this.trainLine)){
					this.track.leave(train);
					train.pos = (Point2D.Float) this.station.position.clone();
					this.station.enter(train);
					this.state = State.IN_STATION;
					this.disembarked = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}


	}
	
	private boolean hasValidDestination(Train train) {
		if (!(train instanceof CargoTrain)) {
			return true;
		} else {
			boolean flag = false;
			if (this.forward) {
				for (int i = trainLine.stations.indexOf(station)+1; i < trainLine.stations.size(); i++) {
					if (trainLine.stations.get(i) instanceof CargoStation) {
						flag = true;
						break;
					}
				}
			} else {
				for (int i = trainLine.stations.indexOf(station)-1; i >= 0; i--) {
					if (trainLine.stations.get(i) instanceof CargoStation) {
						flag = true;
						break;
					}
				}
			}
			return flag;
		}
	}
	
	public boolean inStation(){
		return (this.state == State.IN_STATION || this.state == State.READY_DEPART);
	}

}
