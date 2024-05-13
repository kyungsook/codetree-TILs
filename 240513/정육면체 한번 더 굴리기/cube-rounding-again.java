import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static class Dice {
		int x, y;
		int top, bottom, front, back, left, right;
		
		public Dice(int x, int y) {
			this.x = x;
			this.y = y;
			this.top = 1;
			this.front = 2;
			this.right = 3;
			this.left = 4;
			this.back = 5;
			this.bottom = 6;
		}
		
		public void right() {
			int tmp = this.bottom;
			this.bottom = this.right;
			this.right = this.top;
			this.top = this.left;
			this.left = tmp;
		}
		
		public void down() {
			int tmp = this.bottom;
			this.bottom = this.front;
			this.front = this.top;
			this.top = this.back;
			this.back = tmp;
		}
		
		public void left() {
			int tmp = this.bottom;
			this.bottom = this.left;
			this.left = this.top;
			this.top = this.right;
			this.right = tmp;
		}
		
		public void up() {
			int tmp = this.bottom;
			this.bottom = this.back;
			this.back = this.top;
			this.top = this.front;
			this.front = tmp;
		}
	}
	
	static class Point {
		int x, y;

		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Point [x=" + x + ", y=" + y + "]";
		}
		
	}
	
	static int N, M;
	static int[][] map;
	static int[] dx = {0, 1, 0, -1};	// 우, 하, 좌, 상
	static int[] dy = {1, 0, -1, 0};
	static int result = 0, dir = 0;
	static Dice dice;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		dice = new Dice(0, 0);
		
		for(int turn=0; turn<M; turn++) {
			simulate();
		}
		
		System.out.println(result);
	}
	
	public static void simulate() {
		int nx = dice.x + dx[dir];
		int ny = dice.y + dy[dir];
		
		if(!isRange(nx, ny)) {
			dir = (dir+2)%4;
			nx = dice.x + dx[dir];
			ny = dice.y + dy[dir];
		}
		
		switch(dir) {// 우, 하, 좌, 상
		case 0:
			dice.right();
			break;
		case 1:
			dice.down();
			break;
		case 2:
			dice.left();
			break;
		case 3:
			dice.up();
			break;
		}
		dice.x = nx;
		dice.y = ny;
		
		bfs(nx, ny);
		changeDir();
		
	}
	
	public static void bfs(int x, int y) {
		Queue<Point> q = new ArrayDeque<>();
		boolean[][] visited = new boolean[N][N];
		q.offer(new Point(x, y));
		visited[x][y] = true;
		result += map[x][y];
		
		Point cur;
		
		while(!q.isEmpty()) {
			cur = q.poll();
			
			for(int i=0; i<4; i++) {
				int nx = cur.x + dx[i];
				int ny = cur.y + dy[i];
				
				if(!isRange(nx, ny) || visited[nx][ny]) continue;
				if(map[cur.x][cur.y] != map[nx][ny]) continue;
				
				result += map[nx][ny];
				visited[nx][ny] = true;
				q.offer(new Point(nx, ny));
			}
		}
	}
	
	public static void changeDir() {
		if(dice.bottom > map[dice.x][dice.y]) {
			dir = (dir+1)%4;
		} else if(dice.bottom < map[dice.x][dice.y]) {
			dir = (dir-1 + 4) %4;
		}
	}
	
	public static boolean isRange(int x, int y) {
		return x>=0 && x<N && y>=0 && y<N;
	}

}