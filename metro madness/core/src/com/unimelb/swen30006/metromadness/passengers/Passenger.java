package com.unimelb.swen30006.metromadness.passengers;

import java.util.Random;

import com.unimelb.swen30006.metromadness.stations.CargoStation;
import com.unimelb.swen30006.metromadness.stations.Station;

public class Passenger {

	final public int id;
	public Station beginning;
	public Station destination;
	public float travelTime;
	public boolean reachedDestination;
	public Cargo cargo;
	
	public Passenger(int id, Random random, Station start, Station end){
		this.id = id;
		this.beginning = start;
		this.destination = end;
		this.reachedDestination = false;
		this.travelTime = 0;
		this.cargo = generateCargo(random);
		if (!(start instanceof CargoStation)) {
			this.cargo.setWeight(0);
		}
	}
	
	public void update(float time){
		if(!this.reachedDestination){
			this.travelTime += time;
		}
	}
	public Cargo getCargo(){
		return cargo;
	}
	public Cargo generateCargo(Random random){
		return new Cargo(random.nextInt(51));
	}
	
	public class Cargo{
		private int weight;
		
		public Cargo(int weight){
			this.setWeight(weight);
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}
	}

	
	
}
