package coloring;

import java.awt.Color;
import java.util.Random;

import inra.ijpb.util.ColorMaps;

public class Coloring {
	private int numOfColor;
	private double transpaency;
	private String colors[];
	
	public Coloring( int numOfColor , double transparency ){
		this.numOfColor = numOfColor;
		this.transpaency = transparency;
		colors = new String[ numOfColor ];
		
		generateColors();
	}

	private void generateColors() {
		byte color[][] = ColorMaps.createGoldenAngleLut( numOfColor );
		for( int i = 0 ; i < numOfColor ; i ++){
			int red = color[ i ][ 0 ] + 256 / 2;
			int green = color[ i ][ 1 ] + 256 / 2;
			int blue = color[ i ][ 2 ] + 256 / 2;
			colors[ i ] = new String("rgba(" + red + "," + green + "," + blue + "," + transpaency + ")" );
		}
	}
	public String getColor( int number){
		try{
			return colors[ number ];
		}catch( ArrayIndexOutOfBoundsException ex){
			ex.printStackTrace();
			return null;
		}
	}
}
