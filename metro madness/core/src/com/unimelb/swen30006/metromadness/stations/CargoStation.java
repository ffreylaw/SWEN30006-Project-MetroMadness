package com.unimelb.swen30006.metromadness.stations;

import com.unimelb.swen30006.metromadness.routers.PassengerRouter;

public class CargoStation extends ActiveStation {

	public CargoStation(float x, float y, PassengerRouter router, String name, float maxPax) {
		super(x, y, router, name, maxPax);
	}
		
}
