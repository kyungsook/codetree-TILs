import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	
	static int N, M, P, C, D;
	static int[][] map;
	static int[] dx = {-1, 0, 1, 0};
	static int[] dy = {0, 1, 0, -1};
	static Deer deer;
	static Santa[] santa;
	static boolean[] isDead;
	static boolean flag;
	
	static class Santa implements Comparable<Santa> {
		int x, y;
		int point;
		int crash;
		
		public Santa(int x, int y, int point, int crash) {
			super();
			this.x = x;
			this.y = y;
			this.point = point;
			this.crash = crash;
		}
		
		@Override
		public String toString() {
			return "Santa [x=" + x + ", y=" + y + ", point=" + point + ", crash=" + crash + "]";
		}

		@Override
		public int compareTo(Santa o) {
			int d1 = dist(this.x, this.y);
			int d2 = dist(o.x, o.y);
			
			if(d1 == d2) {
				if(this.x == o.x)
					return o.y - this.y;
				
				return o.x - this.x;
			}
			return d1-d2;
		}
	}
	
	static class Deer {
		int x, y;

		public Deer(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Deer [x=" + x + ", y=" + y + "]";
		}
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
	
		map = new int[N+1][N+1];
		santa = new Santa[P+1];
		isDead = new boolean[P+1];
		
		st = new StringTokenizer(br.readLine());
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		
		deer = new Deer(x, y);
		map[x][y] = -1;
		
		for(int i=0; i<P; i++) {
			st = new StringTokenizer(br.readLine());
			int idx = Integer.parseInt(st.nextToken());
			x = Integer.parseInt(st.nextToken());
			y = Integer.parseInt(st.nextToken());
			
			santa[idx] = new Santa(x, y, 0, 0);
			map[x][y] = idx;
		}
		
		for(int turn=0; turn<M; turn++) {
			if(flag) break;
			
			moveDeer();
			moveSanta();
			
			// 보너스 점수
			addPoint();
			// crash 감소
			decreaseCrash();
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<=P; i++) {
			sb.append(santa[i].point + " ");
		}
		
		System.out.println(sb.toString());
	}
	
	public static void moveDeer() {
		// 1. 가장 가까운 산타 구하기
		Santa s = findSanta();
		
		if(s == null) {
			flag = true;
			return;
		}
		
		// 2. 그 방향으로 이동
		int moveX = 0;
		int moveY = 0;
		
		if(deer.x < s.x) {
			moveX = 1;
		} else if(deer.x > s.x) {
			moveX = -1;
		}
		
		if(deer.y < s.y) {
			moveY = 1;
		} else if(deer.y > s.y) {
			moveY = -1;
		}
		
		deer.x += moveX;
		deer.y += moveY;
		
		// 만약 움직인 곳에 산타가 있다면
		if(deer.x == s.x && deer.y == s.y) {
			int nx = s.x + moveX * C;
			int ny = s.y + moveY * C;
			
			s.crash = 2;
			s.point += C;
			
			interaction(map[s.x][s.y], nx, ny, moveX, moveY);
		}
		
		map[deer.x][deer.y] = -1;
	}
	
	public static void moveSanta() {
		for(int idx=1; idx<=P; idx++) {
			if(isDead[idx] || santa[idx].crash > 0) continue;
			
			Santa cur = santa[idx];
			
			int dir = -1;
			int min = dist(cur.x, cur.y);
			
			for(int i=0; i<4; i++) {
				int nx = cur.x + dx[i];
				int ny = cur.y + dy[i];
				
				if(!isRange(nx, ny) || map[nx][ny] > 0) continue;
				
				int nDist = dist(nx, ny);
				if(nDist < min) {
					min = nDist;
					dir = i;
				}
			}
			
			if(dir != -1) {
				map[cur.x][cur.y] = 0;
				
				cur.x += dx[dir];
				cur.y += dy[dir];
				
				// 만약 이동한 곳에 루돌프가 있다면
				if(deer.x == cur.x && deer.y == cur.y) {
					cur.point += D;
					cur.crash = 2;
					
					int nx = cur.x + (-dx[dir] * D);
					int ny = cur.y + (-dy[dir] * D);
					
					interaction(idx, nx, ny, -dx[dir], -dy[dir]);
				} else {
					map[cur.x][cur.y] = idx;
				}
			}
		}
	}
	
	public static Santa findSanta() {
		PriorityQueue<Santa> pq = new PriorityQueue<>();
		
		for(int i=1; i<=P; i++) {
			if(isDead[i]) continue;
			pq.offer(santa[i]);
		}
		
		return pq.poll();
	}
	
	public static void interaction(int idx, int x, int y, int moveX, int moveY) {
		if(isRange(x, y)) {
			if(map[x][y] > 0) {
				interaction(map[x][y], x+moveX, y+moveY, moveX, moveY);
			}
			map[x][y] = idx;
			santa[idx].x = x;
			santa[idx].y = y;
			
		} else {
			isDead[idx] = true;
		}
	}
	
	public static void addPoint() {
		for(int i=1; i<=P; i++) {
			if(isDead[i]) continue;
			santa[i].point++;
		}
	}
	
	public static void decreaseCrash() {
		for(int i=1; i<=P; i++) {
			if(santa[i].crash > 0) santa[i].crash--;
		}
	}
	
	public static boolean isRange(int x, int y) {
		return x >= 1 && x <= N && y >= 1 && y <= N;
	}
	
	public static int dist(int x, int y) {
		x = deer.x - x;
		y = deer.y - y;
		
		return x*x + y*y;
	}
	
}