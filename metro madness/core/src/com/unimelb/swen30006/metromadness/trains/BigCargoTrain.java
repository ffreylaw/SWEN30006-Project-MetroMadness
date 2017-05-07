package com.unimelb.swen30006.metromadness.trains;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;

public class BigCargoTrain extends CargoTrain {
	
	private final static int PASSENGER_CAPACITY = 80;
	private final static int CARGO_CAPACITY = 1000;
	
	public BigCargoTrain(Line trainLine, Station start, boolean forward, String name) {
		super(trainLine, start, forward, name, PASSENGER_CAPACITY, CARGO_CAPACITY);
		
	}
	
	@Override
	public void render(ShapeRenderer renderer){
		if(!operator.inStation()){
			Color col = operator.forward ? FORWARD_COLOUR : BACKWARD_COLOUR;
			float percentage = this.passengers.size()/20f;
			renderer.setColor(col.cpy().lerp(Color.LIGHT_GRAY, percentage));
			renderer.circle(this.pos.x, this.pos.y, TRAIN_WIDTH*(1+percentage));
		}
	}
	
}
