package com.unimelb.swen30006.metromadness.passengers;

import java.util.ArrayList;
import java.util.Random;

import com.unimelb.swen30006.metromadness.stations.CargoStation;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public class PassengerGenerator {
	
	// Random number generator
	static final private Random random = new Random(30006);
	
	// Passenger id generator
	static private int idGen = 1;
	
	
	// The station that passengers are getting on
	public Station s;
	// The line they are travelling on
	public ArrayList<Line> lines;
	
	// The max volume
	public float maxVolume;
	
	public PassengerGenerator(Station s, ArrayList<Line> lines, float max){
		this.s = s;
		this.lines = lines;
		this.maxVolume = max;
	}
	
	public ArrayList<Passenger> generatePassengers(){
		int count = random.nextInt(4)+1;
		ArrayList<Passenger> passengers = new ArrayList<Passenger>();
		for(int i=0; i<count; i++){
			Passenger p = generatePassenger(random);
			if (p != null) {
				passengers.add(p);
			}
		}
		return passengers;
	}
	
	public Passenger generatePassenger(Random random){
		// Pick a random station from the line
		Line l = this.lines.get(random.nextInt(this.lines.size()));
		int current_station = l.stations.indexOf(this.s);
		boolean forward = random.nextBoolean();
		
		boolean isCargoStation = false;
		if (this.s instanceof CargoStation) {
			isCargoStation = true;
		}
		
		// If we are the end of the line then set our direction forward or backward
		if(current_station == 0){
			forward = true;
		} else if (current_station == l.stations.size()-1){
			forward = false;
		}
		
		boolean hasCargoDestination = false;
		if (isCargoStation) {
			if (forward) {
				for (int i = current_station+1; i < l.stations.size(); i++) {
					if (l.stations.get(i) instanceof CargoStation) {
						hasCargoDestination = true;
						break;
					}
				}
				if (!hasCargoDestination) {
					return null;
				}
			} else {
				for (int i = current_station-1; i >= 0; i--) {
					if (l.stations.get(i) instanceof CargoStation) {
						hasCargoDestination = true;
						break;
					}
				}
				if (!hasCargoDestination) {
					return null;
				}
			}
			
		}
		
		// Find the station
		int index = 0;
		while (true) {
			if (forward){
				index = random.nextInt(l.stations.size()-1-current_station) + current_station + 1;
			} else {
				index = current_station - 1 - random.nextInt(current_station);
			}
			if (isCargoStation && hasCargoDestination && l.stations.get(index) instanceof CargoStation) {
				break;
			} else if (!isCargoStation) {
				break;
			} else {
				continue;
			}
		}
		
		Station s = l.stations.get(index);
		
		return new Passenger(idGen++, random, this.s, s);
	}
	
}
