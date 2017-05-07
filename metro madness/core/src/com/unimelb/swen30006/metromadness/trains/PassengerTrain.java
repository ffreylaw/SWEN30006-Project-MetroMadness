package com.unimelb.swen30006.metromadness.trains;

import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public abstract class PassengerTrain extends Train {

	public int passengerCapacity;

	public PassengerTrain(Line trainLine, Station start, boolean forward, String name, int passengerCapacity) {
		super(trainLine, start, forward, name);
		this.passengerCapacity = passengerCapacity;
	}
	
	@Override
	public void embark(Passenger p) throws Exception {
		if(this.passengers.size() > this.passengerCapacity){
			throw new Exception();
		}
		this.passengers.add(p);
	}

}
