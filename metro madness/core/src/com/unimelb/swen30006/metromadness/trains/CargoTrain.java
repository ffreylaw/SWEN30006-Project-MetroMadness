package com.unimelb.swen30006.metromadness.trains;

import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public abstract class CargoTrain extends Train {
	
	public int passengerCapacity;
	public int cargoCapacity;

	public CargoTrain(Line trainLine, Station start, boolean forward, String name, int passengerCapacity, int cargoCapacity) {
		super(trainLine, start, forward, name);
		this.passengerCapacity = passengerCapacity;
		this.cargoCapacity = cargoCapacity;
	}
	
	@Override
	public void embark(Passenger p) throws Exception {
		if(this.passengers.size() > passengerCapacity || this.getTotalCargoWeight() > cargoCapacity) {
			throw new Exception();
		}
		this.passengers.add(p);
	}
	
	

}
