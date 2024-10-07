import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static class Ship {
		int idx;
		int x, y;
		int dir;
		public Ship(int idx, int x, int y, int dir) {
			super();
			this.idx = idx;
			this.x = x;
			this.y = y;
			this.dir = dir;
		}
		@Override
		public String toString() {
			return "Ship [idx=" + idx + ", x=" + x + ", y=" + y + ", dir=" + dir + "]";
		}
	}
	
	static class Data {
		int x, y;

		public Data(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Data [x=" + x + ", y=" + y + "]";
		}
	}	
	
	static int R, C, K;
	static int[][] map;
	static boolean[][] isExit;
	static int[] dx = {-1, 0, 1, 0};	// 북, 동, 남, 서
	static int[] dy = {0, 1, 0, -1};
	static Ship[] ship;
	static int answer = 0;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		R = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[R+3][C];
		isExit = new boolean[R+3][C];
		ship = new Ship[K];
		
		for(int i=0; i<K; i++) {
			st = new StringTokenizer(br.readLine());
			int c = Integer.parseInt(st.nextToken())-1;
			int dir = Integer.parseInt(st.nextToken());
			
			ship[i] = new Ship(i+1, 1, c, dir);
		}
		
		for(int i=0; i<K; i++) {
			move(ship[i]);
		}
		System.out.println(answer);
	}
	
	public static void move(Ship cur) {
		// 1. 아래로 갈 수 있으면
		if(isDown(cur.x, cur.y)) {
			cur.x++;
			move(cur);
		} else if(isLeft(cur.x, cur.y)) {	// 왼쪽으로 갈 수 있다면
			cur.x++;
			cur.y--;
			cur.dir = (cur.dir+3)%4;
			move(cur);
		} else if(isRight(cur.x, cur.y)) {	// 오른쪽으로 갈 수 있다면
			cur.x++;
			cur.y++;
			cur.dir = (cur.dir+1)%4;
			move(cur);
		} else {
			// 골룸이 마법의 숲 안에 있는지 판별하기
			if(!isRange(cur.x, cur.y)) {	// 숲 안에 없으면 reset
				map = new int[R+3][C];
				isExit = new boolean[R+3][C];
				return;
			}
			
			// 마법의 숲에 있으면 골룸 set
			setShip(cur);
			
			// 정령 이동
			bfs(cur.x, cur.y);
		}
		
	}
	
	private static boolean isDown(int x, int y) {
		boolean flag = 0 < x && x < R+1 && 0 < y && y< C-1;
		flag = flag && map[x+1][y-1] == 0;
		flag = flag && map[x+1][y+1] == 0;
		flag = flag && map[x+2][y] == 0;
		return flag;
	}
	
	private static boolean isLeft(int x, int y) {
		boolean flag = 0 < x && x < R+1 && 1 < y && y< C-1;
		flag = flag && map[x-1][y-1] == 0;
		flag = flag && map[x][y-2] == 0;
		flag = flag && map[x+1][y-1] == 0;
		flag = flag && map[x+1][y-2] == 0;
		flag = flag && map[x+2][y-1] == 0;
		return flag;
	}
	
	private static boolean isRight(int x, int y) {
		boolean flag = 0 < x && x < R+1 && 0 < y && y< C-2;
		flag = flag && map[x-1][y+1] == 0;
		flag = flag && map[x][y+2] == 0;
		flag = flag && map[x+1][y+1] == 0;
		flag = flag && map[x+1][y+2] == 0;
		flag = flag && map[x+2][y+1] == 0;
		return flag;
	}
	
	private static boolean isRange(int x, int y) {
		return 3 <= x && x<= R+1 && 1 <= y && y<=C-2;
	}
	
	private static boolean isBfsRange(int x, int y) {
		return 0 <= x && x < R+3 && 0<= y && y<C;
	}
	
	private static void setShip(Ship cur) {
		map[cur.x-1][cur.y] = cur.idx;
		map[cur.x][cur.y] = cur.idx;
		map[cur.x][cur.y-1] = map[cur.x][cur.y+1] = cur.idx;
		map[cur.x+1][cur.y] = cur.idx;
		isExit[cur.x + dx[cur.dir]][cur.y + dy[cur.dir]] = true;
	}
	
	private static void bfs(int x, int y) {
		Queue<Data> q = new ArrayDeque<>();
		boolean[][] visited = new boolean[R+3][C];
		q.offer(new Data(x, y));
		visited[x][y] = true;
		
		Data cur;
		int result = x;
		
		while(!q.isEmpty()) {
			cur = q.poll();
			
			for(int i=0; i<4; i++) {
				int nx = cur.x + dx[i];
				int ny = cur.y + dy[i];
				
				if(!isBfsRange(nx, ny) || visited[nx][ny]) continue;
				
				if(map[cur.x][cur.y] == map[nx][ny] || (map[nx][ny] != 0 && isExit[cur.x][cur.y])) {
					q.offer(new Data(nx, ny));
					visited[nx][ny] = true;
					result = Math.max(result, nx);
				}
			}
		}
		
		answer += (result-2);
	}
}