package com.unimelb.swen30006.metromadness.trains;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public class SmallCargoTrain extends CargoTrain {
	
	private final static int PASSENGER_CAPACITY = 10;
	private final static int CARGO_CAPACITY = 200;

	public SmallCargoTrain(Line trainLine, Station start, boolean forward, String name) {
		super(trainLine, start, forward, name, PASSENGER_CAPACITY, CARGO_CAPACITY);
	}
	
	@Override
	public void render(ShapeRenderer renderer){
		if(!operator.inStation()){
			Color col = operator.forward ? FORWARD_COLOUR : BACKWARD_COLOUR;
			float percentage = this.passengers.size()/10f;
			renderer.setColor(col.cpy().lerp(Color.MAROON, percentage));
			// We also get slightly bigger with passengers
			renderer.circle(this.pos.x, this.pos.y, TRAIN_WIDTH*(1+percentage));
		}
	}

}
