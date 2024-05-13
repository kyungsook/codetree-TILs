import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

	static int N, M, K;
	static int x, y;
	static int[][] map;
	static int[] dx = {0, 0, -1, 1};	// 동, 서, 북, 남
	static int[] dy = {1, -1, 0, 0};
	static Dice dice;
	
	static class Dice {
		int x, y;
		int top, bottom, front, back, left, right;
		
		public Dice(int x, int y) {
			this.x = x;
			this.y = y;
			this.top = this.bottom = this.front = this.back = this.left = this.right = 0;
		}
		
		public void east() {
			int tmp = this.bottom;
			this.bottom = this.right;
			this.right = this.top;
			this.top =this.left;
			this.left = tmp;
		}
		
		public void west() {
			int tmp = this.bottom;
			this.bottom = this.left;
			this.left = this.top;
			this.top = this.right;
			this.right = tmp;
		}
		
		public void north() {
			int tmp = this.bottom;
			this.bottom = this.back;
			this.back = this.top;
			this.top = this.front;
			this.front = tmp;
		}
		
		public void south() {
			int tmp = this.bottom;
			this.bottom = this.front;
			this.front = this.top;
			this.top = this.back;
			this.back = tmp;
		}
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		x = Integer.parseInt(st.nextToken());
		y = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[N][M];
		
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<M; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		int[] move = new int[K];
		st = new StringTokenizer(br.readLine());
		for(int i=0; i<K; i++) {
			move[i] = Integer.parseInt(st.nextToken())-1;
		}
		
		dice = new Dice(x, y);
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<K; i++) {
			if(play(move[i])) {
				sb.append(dice.top).append("\n");
			}
		}
		
		System.out.println(sb.toString());
	}
	
	public static boolean play(int dir) {
		int nx = dice.x + dx[dir];
		int ny = dice.y + dy[dir];
		
		// 만약 범위를 벗어난다면 종료
		if(!isRange(nx, ny)) return false;
		
		switch(dir) {
		case 0:
			dice.east();
			break;
		case 1:
			dice.west();
			break;
		case 2:
			dice.north();
			break;
		case 3:
			dice.south();
			break;
		}
		
		if(map[nx][ny] == 0) {
			map[nx][ny] = dice.bottom;
		} else {
			dice.bottom = map[nx][ny];
			map[nx][ny] = 0;
		}
		
		dice.x = nx;
		dice.y = ny;
		
		return true;
	}
	
	public static boolean isRange(int x, int y) {
		return x >= 0 && x < N && y>=0 && y<M;
	}

}