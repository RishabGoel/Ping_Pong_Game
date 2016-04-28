package networking;

//	class containing the details of the any player
public class Paddle {
	int l,p;	//	variables of length and position of the players paddle
	int index;	//	variable containing the index of the players paddle
	Ball ball;	//	variable containing the absolute coordinates of the ball
	boolean pause = true;	//local pause variable used to sync the game
	int difficulty = 3;	//	local difficulty variable
	
	int lives = 15151515,score = 0,power = 0;	//Encoded variables containing the data of other local computer controlled players
	
	Paddle(int len,int pos){	//constructor
		l = len;	p = pos;
		index = -1;
		ball = new Ball();
	}
	
	Paddle(){	//constructor
		index = -1;
		l = 200;	p = 0;
		ball = new Ball();	
	}
	
}
