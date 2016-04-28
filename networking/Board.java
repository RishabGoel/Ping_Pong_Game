package networking;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.Timer;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Board extends JPanel implements ActionListener, KeyListener{
	
	
	/*	
	 * 1: hold power
	 * 2: long board
	 * 3: life+=3
	 */
	
	boolean firstrun = true;			// Variable to show whether its the first game on the board
	boolean hold1 = false,hold2 = false;	// two different variables for ball holding super-power
	//hold1 determines whether the hold button is pressed and hold2 determines whether the ball is in the range of ball holding field
	boolean paused = true;	//variable to check whether the game is finished or not

	Timer tm = new Timer(3,this);	// Timer loop
	
	int height = 600, width = 600;	//size of the game board
	int[] health ;	//array to store each player
    
    int cx=400,cy=400; double vcx=2,vcy=3;
    int rad = 20;	//radius of ball
    int v_ball = 5;int v_paddle = 0;
    int v_max_paddle = 10;	//velocity of paddle
    
    Ball ball = new Ball();	//object of class ball
    public static Paddle[] paddle = new Paddle[4];	//array for all paddles
    
    int flag=0;
    public static int p_idx;	//index of the current player
    
    int[] lives_left;	//array containing the lives left of all the players
    int players_left=4;	//array giving number of players left (players with positive power) at an instant
    int[] cur_score;	//array containing current scores of all the players
    int[] pow_used;	//array containing powers of all the players
    int enable_hold = 0;	//	enabling holding power to the controlling player
    int difficulty = 3;	//difficulty level variable
    
    /*public static void slp() throws InterruptedException{	
		Thread.sleep(2000);
	}*/
    
    public void paintComponent(Graphics g){	//	definition of the interface method used to draw images on the screen
    	super.paintComponent(g);	
    	
    	g.setColor(Color.BLACK);	//setting the colour = black
    	//	making the boaders
    	g.fillRect(10, height, width, 10);	
    	g.fillRect(10, 10, width, 10);
    	g.fillRect(10, 10, 10, height);
    	g.fillRect(width, 10, 10, height);
    	
    	//	drawing the player's paddle
    	g.setColor(Color.RED);
    	g.fillRect(paddle[(p_idx+0)%4].p, height, paddle[p_idx].l, 10);
    	//	drawing the paddle on the right to the player
    	g.setColor(Color.BLUE);
    	g.fillRect(width, height - paddle[(p_idx+1)%4].p - paddle[(p_idx+1)%4].l, 10, paddle[(p_idx+1)%4].l);
    	//drawing the paddle opposite to the player
    	g.setColor(Color.GREEN);
    	g.fillRect(width-paddle[(p_idx+2)%4].p-paddle[(p_idx+2)%4].l, 10, paddle[(p_idx+2)%4].l, 10);
    	//drawing the paddle on the left to the player
    	g.setColor(Color.ORANGE);
    	g.fillRect(10, paddle[(p_idx+3)%4].p, 10, paddle[(p_idx+3)%4].l);
    	//	drawing the bars representing the powers left with all the players
    	g.setColor(Color.MAGENTA);
    	g.fillRect(10, width+10,lives_left[(p_idx)%4]*20 , 10);
    	g.fillRect(width+10, 0 , 10,lives_left[(p_idx+1)%4]*20);
    	g.fillRect(0,0,lives_left[(p_idx+2)%4]*20 , 10);
    	g.fillRect(0, 0 ,10,lives_left[(p_idx+3)%4]*20);
    	
    	//drawing the ball
    	g.setColor(Color.BLACK);
    	//System.out.print(ball.vcx+" "+ball.vcy+" ");
    	ball.changeFrame(p_idx);	//	changing the ball's co-ordinates in terms of the perspective of the player
    	//System.out.print(ball.vcx+" "+ball.vcy+"\n");
    	g.fillOval((int)ball.x,(int)ball.y,(int)ball.r,(int)ball.r);	//drawing of ball with radius r and at position ball.x,ball.y
    	
    	ball.changeFrame((4-p_idx)%4);	//reverting the operation of changing the perspective of the ball
    	
    	//printing the scores, lives left and other relevant details
    	g.drawString("Scores:", 75, 640);
    	g.drawString("Player 0:"+" "+cur_score[0], 75, 660);
    	g.drawString("Player 1:"+" "+cur_score[1], 75, 680);
    	g.drawString("Player 2:"+" "+cur_score[2], 75, 700);
    	g.drawString("Player 3:"+" "+cur_score[3], 75, 720);
    	
    	g.drawString("lives left:", 375, 640);
    	g.drawString("Player 0:"+" "+lives_left[0], 375, 660);
    	g.drawString("Player 1:"+" "+lives_left[1], 375, 680);
    	g.drawString("Player 2:"+" "+lives_left[2], 375, 700);
    	g.drawString("Player 3:"+" "+lives_left[3], 375, 720);
    	
    	//	Difficulty menu
    	if(paused)	{	//difficulty menu available only when the game is stopped
    		g.drawString("default difficulty : Medium ", 250, 300);
    		g.drawString("Set difficulty ", 250, 320);
    		g.drawString("Press 1 for Easy ", 250, 340);
    		g.drawString("Press 2 for Medium", 250, 360);
    		g.drawString("Press 3 for Hard", 250, 380);
    		g.drawString("Press S to start the game ", 250, 420);
    	}
    	
    	int sss = 0;	//number of active players
    	for(int i=0;i<4;i++)	if(lives_left[i]>0)sss++;
    	//displaying the winner only if there is a single player with positive lives
    	if(sss==1)	for(int i=0;i<4;i++)	if(lives_left[i]>0&&paused)	g.drawString("Player "+i+" has won!",250,280);
    	
    	
    	//starting the timer(), i.e. the game loop
    	tm.start();
    }
    
    
    public Board() throws IOException{	//constructor of the class Board
    	health = new int[4];	//	array containing the current health of each element
    	
        lives_left = new int[4];	//	initializing the array containing the no of lives left for each player
        players_left=4;	//	initializing the array of no of players left at any instant
        cur_score = new int[4];	//	initializing the score arrays
        pow_used = new int[4];	
        
        //initializing the variables to default values
    	for(int i=0;i<4;i++)	{	
    		paddle[i] = new Paddle();	
    		health[i] = width;	
    		cur_score[i]=0;	
    		lives_left[i] = 15;	
    		pow_used[i] = 0;
    	}
    	
    	
    	Main main=new Main();	//Calling the constructor of the file that setups the networking
    	main.startNetworking();
    	tm.start();
    	
    	addKeyListener(this);	//
    	setFocusable(true);	//
    	setFocusTraversalKeysEnabled(false);	//
    }
    
    Ball do_collision(int idx,Paddle p,Ball b){		//function to check the collision of the ball with either the wall or the paddle of player with index idx
    	b.changeFrame(idx);	//converting the coordinates of the ball in the perspective of the player with index idx
    	
    	double phi = Math.atan((b.x-p.p-p.l/2)/(double)p.l);	//angle at which the ball is incident on the board
		double deg_phi = phi*180/3.14159;	
		
		double vcx = b.vcx;
		double vcy = b.vcy;
    	
		/*Checking whether the ball hits the paddle
		 * and updating coresponding values.
		 * 
		 */
		if(b.x>p.p&&b.x<p.p+p.l){
			double h = p.l*(1-Math.cos(Math.PI/6));
			if(vcy>0&&b.y>=(height-h)){
				vcx = vcx*Math.cos(2*phi)+vcy*Math.sin(2*phi);
				vcy =-vcy*Math.cos(2*phi)+vcx*Math.sin(2*phi);
				if(lives_left[idx]>0)	cur_score[idx]++;
				
				Random randomGenerator = new Random();
			      int randomInt = randomGenerator.nextInt(100);
				
				
			//	Assigning the power if the score crosses the threshold	
				if(cur_score[idx]>10&&pow_used[idx]==0&&vcy<0&&lives_left[idx]>0){
					
					pow_used[idx] = 1+randomInt%3;
					if(pow_used[idx]==1)	lives_left[idx] +=15;
					else if(pow_used[idx]==2)	paddle[idx].l = (int)(1.5*paddle[idx].l);
					else if(pow_used[p_idx]==3)	enable_hold = 1;
					//System.out.println(idx+" "+pow_used[idx]);
				}
			}
			
			
			
			//	checking whether hold is possible or not
			if(b.y>=(height-h-20)&&idx==p_idx&&enable_hold==1)	hold2 = true;
			else if(b.y<(height-3*h)&&idx==p_idx)	hold2 = false;
		}
		
		
		if(idx==p_idx&&(!(b.x>p.p&&b.x<p.p+p.l))){
			hold2 = false;
		}
		
		//	checking the collision with the wall
		if(b.y>height-b.r && vcy >0){
			vcy = -vcy;
			if(lives_left[idx]>0)	{	
				lives_left[idx]--;	
				if(lives_left[idx]==0)	players_left--;	
				if(players_left==1)	{	paddle[p_idx].pause = true;	firstrun = false;	}
			}
			//health[idx]-=10;
		}
		
		// the condition which wont allow the velocity of the ball to decrease
		if(vcx!=0)	b.vcx = v_ball*Math.sqrt(vcx*vcx/(vcx*vcx+vcy*vcy));			
		if(vcx<0)b.vcx = -b.vcx;
		if(vcy!=0)	b.vcy = v_ball*Math.sqrt(vcy*vcy/(vcx*vcx+vcy*vcy));
		if(vcy<0)b.vcy = -b.vcy;
		
    	b.changeFrame((4-idx)%4);
    	return b;
    }
    
    int randomInt1=0;
    int ctr = 0;
    int mod = 40;
    
    
    //	function for the AI of the automatic paddles
    public void get_pos(int idx,Paddle p,Ball b){
    	b.changeFrame(idx);
    		//Random randomGenerator = new Random();
    		ctr++;
	    //if(ctr%mod==0)	randomInt1 = randomGenerator.nextInt(100);
	    	//randomInt1 = (randomInt1)%(difficulty+1);
    	if(p.p-randomInt1*randomInt1*randomInt1<b.x)	p.p+=(v_max_paddle*v_max_paddle)/(v_max_paddle+difficulty);
    	else if(p.p+p.l+randomInt1*randomInt1*randomInt1>b.x)	p.p-=v_max_paddle;
    	b.changeFrame((4-idx)%4);
    }
    
    
    boolean prev_p = true;	//variable checking the previously paused condition
	@Override
	public void actionPerformed(ActionEvent e) {	
		
		//if(paused==false){
		
		
		paused = false;
		//	Sync of paused condition
		for(int i=0;i<4;i++)if(paddle[i].index>=0)	{
				if(paddle[i].pause)	paused = true;
		}
		
		
		paddle[p_idx].p = paddle[p_idx].p+v_paddle;
		
		//	Sync of the values of ball and other paddles with other players
		for(int i=0;i<4;i++)	if(paddle[i].index>=0){
		
			ball = paddle[i].ball;
			int s = paddle[i].score;
			int p = paddle[i].power;
			int l = paddle[i].lives;
			
			//	retriving the values of automatic paddle's variables
			for(int j=0;j<4;j++){
				lives_left[j] = l%100;
				pow_used[j] = p%100;
				cur_score[j] = s%100;
				l/=100;p/=100;s/=100;
				if(pow_used[j]==2)	paddle[j].l = 300;
			}

			if(paddle[i].index>=0)break;
		}
		
		//	Sync the start of games when other players join in between
		if(paused==false&&prev_p==true){
			paddle[p_idx].pause = false;		
			for(int i=0;i<4;i++)	
			{	
				paddle[i].l = 200;
				paddle[i].p = 0;
				health[i] = width;	
				cur_score[i]=0;	
				lives_left[i] = 15;	
				pow_used[i] = 0;	
				cur_score[i] = 0;
				players_left = 4;
			}
		}
		
		
		difficulty = 2;
		
		//	 Sync the difficulty of the players
		for(int i=0;i<4;i++)	if(paddle[i].index>=0){
			if(paddle[i].difficulty>difficulty)	difficulty = paddle[i].difficulty;
		}
		
		//	changing the balls perspective in terms of player to apply the hold operation
		ball.changeFrame(p_idx);
		if(hold1&&hold2)	ball.x += v_paddle;
		if(ball.x+ball.r>width)ball.x = width-ball.r;
		if(ball.x<ball.r)	ball.x = ball.r;
		
		ball.changeFrame((4-p_idx)%4);	//	reversing the earlier frame change operation
		
		//	Checking the user controlled paddles and automatically controlling the non controlled paddles
		for(int i=0;i<4;i++)	if(paddle[i].index<0&&i!=p_idx)	{
			get_pos(i,paddle[i],ball);
		}
		
		//	Limiting the motion of the paddles
		for(int i=0;i<4;i++){
			if(paddle[i].p<10)	paddle[i].p = 10;
			if(paddle[i].p>width-paddle[i].l)	paddle[i].p = width - paddle[i].l;
		}
		
		//	moving the ball if hold is not there
		if(!(hold1&&hold2))
		{
			ball.x+=ball.vcx;
			ball.y+=ball.vcy;
		
			ball = do_collision(0,paddle[0],ball);
			ball = do_collision(1,paddle[1],ball);
			ball = do_collision(2,paddle[2],ball);
			ball = do_collision(3,paddle[3],ball);
			
		}
		
		paddle[p_idx].ball = ball;
		
		repaint();	//painting the screen again and again
		prev_p = paused;
		//}
		
		//	Encoding the variables into an state integer to reduce the data transferred
		paddle[p_idx].score = 0;
		paddle[p_idx].power = 0;
		paddle[p_idx].lives = 0;
		for(int i=0;i<4;i++){
			paddle[p_idx].score = 100*paddle[p_idx].score+cur_score[3-i];
			paddle[p_idx].power = 100*paddle[p_idx].power+pow_used[3-i];
			paddle[p_idx].lives = 100*paddle[p_idx].lives+lives_left[3-i];
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {	//	Event listener corresponding to the pressing of the keys
		// TODO Auto-generated method stub
		int c = e.getKeyCode();
		if(c==KeyEvent.VK_LEFT){	//	left movement of the paddle
			v_paddle = -5;
		}
		else if(c==KeyEvent.VK_RIGHT){	//right movement of the paddle
			v_paddle = 5;
		}
		else if(c==KeyEvent.VK_H){	//	hold button
			hold1 = true;
			//System.out.println(hold1);
		}
		else if(c==KeyEvent.VK_S&&paused)	{	//starting the game from stopped state
			paddle[p_idx].pause = false;		
			for(int i=0;i<4;i++)	//resetting the values of all variables
			{	
				paddle[i].l = 200;
				paddle[i].p = 0;
				health[i] = width;	
				cur_score[i]=0;	
				lives_left[i] = 15;	
				pow_used[i] = 0;	
				cur_score[i] = 0;
				players_left = 4;
			}
		}
		if(paused){
			if(c==KeyEvent.VK_1)	{	paddle[p_idx].difficulty = 5;	mod = 50;	v_ball = 3;v_max_paddle = 2;}
			else if(c==KeyEvent.VK_2)	{paddle[p_idx].difficulty = 3;	mod = 30;	v_ball = 5;	v_max_paddle = 4;}
			else if(c==KeyEvent.VK_3)	{paddle[p_idx].difficulty = 2;	mod = 20;	v_ball = 7;v_max_paddle = 6;}
		}
		if(c==KeyEvent.VK_P)	flag = flag^1;
	}
	


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		v_paddle = 0;
		int c = e.getKeyCode();
		if(c==KeyEvent.VK_H){
			hold1 = false;
			//System.out.println(hold1);
		}
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		int c = e.getKeyCode();
	}
}