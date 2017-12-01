import java.util.Scanner;

public class MineSweeper
{
	static int flagCounter = 0;
	static int flagOrClear = -1;
	
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		boolean valid = false;
		
		int n = 0;
		
		//loop until valid input is given
		while(!valid)
		{
			System.out.print("The board size will be n*n with n bombs, choose n (4 <= n <= 100): ");
			n = scan.nextInt();
		
			System.out.println("Enter 0 to clear a space or 1 to flag a bomb");
			System.out.print(">");
			flagOrClear = scan.nextInt();
			if(4<=n && n<=100 && (flagOrClear == 0 || flagOrClear == 1))
			{
				valid = true;
			}
		}
		
		//blank board is created and printed to show the user
		//a board of the correct size before they have 
		//to choose coordinates
		char[][] blank = blankBoard(n);
		printBoard(blank);
		
		int[] firstCoords = initialInput(n);
		
		
		char[][] gameboard = initBoard(n, firstCoords);
		
		printBoard(gameboard);
		//printArray(gameboard);
		
		char[][] answerBoard = fillAnswerBoard(gameboard);
		boolean[][] visitBoard = fillVisitBoard(gameboard);
		
		//System.out.println("~~~~~~~~~~~");
		//printArray(answerBoard);
		
		
		boolean gameWon = false;
		boolean startingGame = true;
		
		while(!gameWon)
		{
			boolean validC = false;
			
			if(!startingGame)
			{
				while(!validC)
				{
					System.out.println("Enter 0 to clear a space, 1 to flag a bomb, or 2 to unflag a bomb");
					System.out.print(">");
					flagOrClear = scan.nextInt();
					if(4<=n && n<=100 && (flagOrClear == 0 || flagOrClear == 1 || (flagOrClear == 2 && flagCounter > 0)))
					{
						validC = true;
					}else
					{
						System.out.println("\nTry again!");
					}
				}
			
			}
			
			int[] coords = {-1,-1};
			if(startingGame)
			{
				coords = firstCoords;
			}else
			{
				coords = getInput(gameboard);
			}
			
			startingGame = false;
			
			
			if(flagOrClear == 1 || (flagOrClear == 2 && flagCounter > 0))
			{
				guessBomb(gameboard,coords);
				
			}else if(flagOrClear == 0)
			{
				gameboard = revealSpaceRecursion(coords, gameboard, answerBoard, visitBoard);
			}
			
			
			if(flagCounter == gameboard.length)
			{
				gameWon = checkWin(gameboard, answerBoard);
			}
			
			
			
			printBoard(gameboard);
			//printArray(gameboard);
			//System.out.println("~~~~~~~~~~~");
			//printArray(answerBoard);
		}
	}
	
	public static char[][] blankBoard(int n)
	{
		char[][] board = new char[n][n];
		for(int i = 0; i<n; i++)
		{
			for(int j = 0; j<n; j++)
			{
				board[i][j] = '#';
			}
		}
		return board;
	}
	
	public static char[][] initBoard(int n, int[] firstCoords)
	{
		//bombLocations keeps track of where bombs already are
		//to avoid randomly assigning two bombs to one spot
		//and ending up with less than n bombs
		boolean[][] bombLocations = new boolean[n][n];
		
		int x = firstCoords[0];
		int y = firstCoords[1];
		
		char[][] board = new char[n][n];
		int bombCounter = 0;
		boolean filledBombs = false;
		while(!filledBombs)
		{
			int rI, rJ;
			rI = (int)(Math.random()*n);
			rJ = (int)(Math.random()*n);
			
			
			//the following if statement doesn't let a bomb get placed 
			//anywhere surrounding the user's first coordinates
			//to prevent everything from instant death to slight frustration.
			//It basically assures a sweep for the first move.
			if( !((rI == x-1 && (rJ == y-1 || rJ == y || rJ == y+1)) ||
				(rI == x && (rJ == y-1 || rJ == y || rJ == y+1)) ||
				(rI == x+1 && (rJ == y-1 || rJ == y || rJ == y+1)) ))
			{
				if(bombLocations[rI][rJ]==false)
				{
					board[rI][rJ] = '@';
					bombLocations[rI][rJ] = true;
					bombCounter++;
				}
			}
			if(bombCounter==n)
			{
				filledBombs = true;
			}
		}
		
		//Fill every space not a bomb with a '#'
		for(int j = 0; j<n; j++)
		{
			for(int k = 0; k<n; k++)
			{
				if(board[j][k] != '@')
				{
					board[j][k] = '#';
				}
			}
		}
		return board;
	}
	
	
	public static boolean checkWin(char[][] gameBoard, char[][] answerBoard)
	{
		int counter = 0;
		int n = answerBoard.length;
		for(int i = 0; i<n; i++)
		{
			for(int j = 0; j<n; j++)
			{
				if(answerBoard[i][j] == '@' && gameBoard[i][j] == 'X')
				{
					//increment every time the user correctly placed an 'X'
					counter++; 
				}
				if(gameBoard[i][j] == '#')
				{
					//at any point, if there is a '#', the game isn't over
					return false;
				}
			}
		}
		
		//FINAL WIN CONDITION
		if(counter == n)
		{
			System.out.println("Congratulations, you won!");
			return true;
		}else 
		{
			return false;
		}
	}
	
	//This is a helper method to fill the visitBoard used for recursion
	public static boolean[][] fillVisitBoard(char[][] board)
	{
		boolean[][] visitBoard = new boolean[board.length][board.length];
		for(int i = 0; i<board.length; i++)
		{
			for(int j = 0; j<board.length; j++)
			{
				visitBoard[i][j] = false;
			}
		}
		
		return visitBoard;
	}
	
	//This is a helper method to fill the answerBoard 
	//which used to check what the user can't see, and 
	//specifically to give the program a base case to 
	//know when to stop recursing
	public static char[][] fillAnswerBoard(char[][] board)
	{
		
		char[][] answerBoard = new char[board.length][board.length];
		for(int i = 0; i<board.length; i++)
		{
			for(int j = 0; j<board.length; j++)
			{
				
				answerBoard[i][j] = board[i][j];
			}
		}
		for(int i = 0; i<board.length; i++)
		{
			for(int j = 0; j<board.length; j++)
			{
				if(board[i][j] != '@')
				{
					int[] coords = {i,j};
					revealSpaceSmall(coords,answerBoard);
				}
			}
		}
		return answerBoard;
	}
	
	
	//This method prints the contents of the board so I 
	//can debug the game while it runs
	/*public static void printArray(char[][] board)
	{
		int n = board.length;
		
		for(int i = 0; i<n; i++)
		{
			System.out.print("| ");
			for(int j = 0; j<n; j++)
			{
				System.out.print(board[i][j] + " | ");
			}
			System.out.println();
		}
	}*/
	
	
	//Used to flag and unflag boxes
	public static void guessBomb(char[][] board, int[] coords)
	{
		int x = coords[0];
		int y = coords[1];
		if(flagOrClear == 1)
		{
			flagCounter++;
			board[x][y] = 'X';
		}else
		{
			board[x][y] = '#';
			flagCounter--;
		}
	}
	
	//This method is used for the first move only. I implemented this
	//because getInput will call checkValidCoords() which will fail
	//if run before the board is initialized
	public static int[] initialInput(int n)
	{
		Scanner scan = new Scanner(System.in);
		boolean usableResponse = false;
		int n1 = -1;
		int n2 = -1;
		
		while(!usableResponse)
		{
			System.out.print("Enter the number on the x axis:\n>");
			n1 = scan.nextInt();
			System.out.print("Enter the number on the y axis:\n>");
			n2 = scan.nextInt();
			if(0 <= n1 && n1<n)
			{
				if(0<=n2 && n2<n)
				{
					usableResponse = true;	
				}else
				{
					System.out.println("That was not valid input, try again.");
				}
				
			}
		}
		
		//the array appears on the board the opposite way I anticipated,
		//that explains the seemingly reverse coordinate pair below
		int[] coords = {n2,n1};
		return coords;
	}
	
	
	public static int[] getInput(char[][] board)
	{
		int n = board.length;
		Scanner scan = new Scanner(System.in);
		boolean usableResponse = false;
		int n1 = -1;
		int n2 = -1;
		
		while(!usableResponse)
		{
			System.out.print("Enter the number on the x axis:\n>");
			n1 = scan.nextInt();
			System.out.print("Enter the number on the y axis:\n>");
			n2 = scan.nextInt();
			if(0 <= n1 && n1<n)
			{
				if(0<=n2 && n2<n)
				{
					
					if(checkValidCoord(n2,n1,board))
					{
						usableResponse = true;
					}else
					{
						System.out.println("That was not valid input, try again.");
					}
				}
			}
		}
		
		//the array appears on the board the opposite way I anticipated,
		//that explains the seemingly reverse coordinate pair below
		int[] coords = {n2,n1};
		return coords;
		
	}
	
	
	
	public static boolean checkValidCoord(int n1, int n2, char[][] board)
	{
		if(board[n1][n2] == '@' && flagOrClear == 0)//if box holds a bomb and attempting to clear
		{
			System.out.println("BOOM - Game Over");
			System.exit(0);//end game
			return false;//this will never be reached but without it I get an error
		}else if(board[n1][n2]=='#')//if user picks any unrevealed box
		{
			return true;
		}else if(board[n1][n2] == '@' && flagOrClear == 1)//if box holds a bomb and trying to flag
		{
			
			return true;
		}else if(board[n1][n2] == 'X' && flagOrClear == 2)//if box is flagged and trying to unflag
		{
			return true;
		}else{//if any other input has been input already
			return false;
		}
		
	}
	
	
	//This method is used to initialize the answerBoard so the number of
	//surrounding bombs can be placed into every square using a for loop
	//instead of recursion since it isn't necessary yet
	public static char[][] revealSpaceSmall(int[] coords, char[][] board)
	{
		int b = 0;
		int x = coords[0];
		int y = coords[1];
		int n = board.length;
		
		if(y-1>=0 && board[x][y-1] == '@')
			b++;
		if(y+1<n && board[x][y+1] == '@')
			b++;
		if(x-1 >=0 && y-1 >= 0 && board[x-1][y-1] == '@')
			b++;
		if(x-1 >= 0 && board[x-1][y] == '@')
			b++;
		if(x-1 >= 0 && y+1<n && board[x-1][y+1] == '@')
			b++;
		if(x+1 <n  && y-1>= 0 &&board[x+1][y-1] == '@')
			b++;
		if(x+1 <n && board[x+1][y] == '@')
			b++;
		if(x+1 <n && y+1 <n && board[x+1][y+1] == '@')
			b++;
			
			
		int asciiCharacter = 48+b;//ascii table '0' is the 48th character
		board[x][y] = (char)asciiCharacter; 
		 
		return board;
	}
	
	
	//This method calls reveals the coordinate chosen, and calls itself
	//repeatedly until every revealable box around it has been swept
	public static char[][] revealSpaceRecursion(int[] coords, char[][] board, char[][] answerBoard, boolean[][] visitBoard)
	{
		int b = 0;
		int x = coords[0];
		int y = coords[1];
		int n = board.length;
		
		//The following if statements follow the same structure so
		//I will only document the first one:
		
		//first true/false value checks if the surrounding box in 
		//question is in bounds...
		//after the && it checks to see if there is a bomb and increments 
		//the counter if there is
		if(y-1>=0 && answerBoard[x][y-1] == '@')
		{
			b++;
		//ELSE, if no bomb, again check to see if the specific surrounding
		//box is in bounds...
		//after &&,  see if the program has already revealed that box
		//with recursion...
		//after the second &&, check answerBoard to see if a 0 is present 
		//from(x,y) the coordinates the method was called with, the program
		//shouldn't recurse if the box in question is a 1,2,3, etc. because
		//it would just reveal the whole board instead of stopping at the 
		//numbered boundary...
		} else if(y-1>=0 && visitBoard[x][y-1] == false && answerBoard[x][y] == '0')
		{
			int[] tempCoords = {x,y-1};
			visitBoard[x][y-1] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
		
		
		if(y+1<n && answerBoard[x][y+1] == '@')
		{
			b++;
		} else if(y+1<n  && visitBoard[x][y+1] == false && answerBoard[x][y] == '0')
		{
			int[] tempCoords = {x,y+1};
			visitBoard[x][y+1] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
		
		
		if(x-1 >=0 && y-1 >= 0 && answerBoard[x-1][y-1] == '@')
		{
			b++;
		}else if(x-1 >=0 && y-1 >= 0  && visitBoard[x-1][y-1] == false && answerBoard[x][y] == '0')
		{
			int[] tempCoords = {x-1,y-1};
			visitBoard[x-1][y-1] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
		
		
		
		if(x-1 >= 0 && answerBoard[x-1][y] == '@')
		{
			b++;
		}else if(x-1 >= 0  && visitBoard[x-1][y] == false && answerBoard[x][y] == '0')
		{
			int[] tempCoords = {x-1,y};
			visitBoard[x-1][y] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
		
		
		
		if(x-1 >= 0 && y+1<n && answerBoard[x-1][y+1] == '@')
		{
			b++;
		}else if(x-1 >= 0 && y+1<n   && visitBoard[x-1][y+1] == false && answerBoard[x][y] == '0')
		{
			int[] tempCoords = {x-1,y+1};
			visitBoard[x-1][y+1] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
		
		
		
		if(x+1 <n  && y-1>= 0 && answerBoard[x+1][y-1] == '@')
		{
			b++;
		}else if(x+1 <n  && y-1>= 0  && visitBoard[x+1][y-1] == false && answerBoard[x][y] == '0')
		{
			int[] tempCoords = {x+1,y-1};
			visitBoard[x+1][y-1] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
		
		
		
		if(x+1 <n && answerBoard[x+1][y] == '@')
		{
			
			b++;
		}else if(x+1 <n  && visitBoard[x+1][y] == false && answerBoard[x][y] == '0')
		{	
			
			int[] tempCoords = {x+1,y};
			visitBoard[x+1][y] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
		
		
		
		if(x+1 <n && y+1 <n && answerBoard[x+1][y+1] == '@')
		{
			b++;
		}else if(x+1 <n && y+1 <n  && visitBoard[x+1][y+1] == false && answerBoard[x][y] == '0')
		{
			int[] tempCoords = {x+1,y+1};
			visitBoard[x+1][y+1] = true;
			revealSpaceRecursion(tempCoords, board, answerBoard, visitBoard);
		}
			
			
		//ascii table '0' is the 48th character	
		int asciiCharacter = 48+b;
		board[x][y] = (char)asciiCharacter; 
		if(answerBoard[x][y] == '0')
		{
			//use a blank space to make the game easier to see/play
			board[x][y] = ' ';
		}
		 
		return board;
	}
	
	
	public static void printBoard(char[][] gameboard)
	{
		int n = gameboard.length;
		
		char[][] tempBoard = new char[n][n];
		for(int x = 0; x<n; x++)
		{
			for(int y = 0; y<n; y++)
			{
				if(gameboard[x][y] == '@')
				{
					tempBoard[x][y] = '#';
				}else 
				{
					tempBoard[x][y] = gameboard[x][y];
				}
			}
		}
		
		
		System.out.print("   ");
		for(int k = 0; k<n; k++)
		{
			System.out.print("----");			 
		}
			
		for(int i = 0; i<n; i++)
		{
			int j = 0;
			
			
			System.out.println("");
			
			for(j=0; j<n; j++)
			{
				
				if(j==0)
				{
					
					System.out.print(i + " | " + tempBoard[i][j] + " | ");
					
				}else if(j!= 0 && j!= n-1)
				{
					
					System.out.print(tempBoard[i][j] + " | ");
					
				}else if(j==n-1)
				{
					
					System.out.print(tempBoard[i][j] + " |");
					
				}
			}
			
			System.out.println("");
			System.out.print("   ");
			for(int k = 0; k<n; k++)
			{
				System.out.print("----");
								 
			}
			
			if(i ==n-1)
			{
				System.out.println("");
				System.out.print("    ");
				for(int m = 0; m<n; m++)
				{
					//the following if statements prevent the numbers
					//from falling out of place with their columns
					//when n has different numbers of digits
					if(m<10)
					{
						System.out.print(m+ "   ");
					}else if(m<100)
					{
						System.out.print(m + "  ");
					}else if(m == 100)
					{
						System.out.print(m);
					}
				}
			}	
		}
		System.out.println("\n\n");	
	}
	
	
}