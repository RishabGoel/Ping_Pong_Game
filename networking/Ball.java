package networking;

// Class of the ball
public class Ball {
	double x,y,r,vcx,vcy;	//variables storing the positions, radius and the 2d-velocity
	
	Ball (int rr,int xx,int yy,int vx,int vy){
		 x=xx;	y=yy;	r = rr;
		 vcx = vx;	vcy = vy;
	}
	
	Ball(){
		r = 20;
		x = 50;
		y = 40;
		vcx = 1;
		vcy = 2;
	}
	
	// fuction to convert the ball into the perspective of any player given the absolute position
	
	void changeFrame(int idx){
		double temp;
		switch(idx){
			
			case 0:	break;
			case 1:		// rotation by 90 degree
				temp = vcx;
				vcx = -vcy;
				vcy = temp;
				temp = x;
				x = 600-y;
				y = temp;
				break;
			case 2:	//rotation by 180 degree
				vcx = -vcx;
				vcy = -vcy;
				x = 600-x;
				y = 600-y;
				break;
			case 3:	//rotation by 270 degree
				temp = vcx;
				vcx = vcy;
				vcy = -temp;
				temp = x;
				x = y;
				y = 600-temp;
				break;
			default: break;
		}
	}
}
