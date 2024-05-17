import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
	static class Monster {
		int x, y;
		int dir;
		
		public Monster(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public Monster(int x, int y, int dir) {
			super();
			this.x = x;
			this.y = y;
			this.dir = dir;
		}

		@Override
		public String toString() {
			return "Monster [x=" + x + ", y=" + y + ", dir=" + dir + "]";
		}
	}
	
	static List<Integer>[][] map;
	static int isDead[][];
	static int M, T;
	static Monster pacman;
	static int[] dx = {-1, 0, 1, 0};	// 상, 좌, 하, 우
	static int[] dy = {0, -1, 0, 1};
	static int[] mx = {-1, -1, 0, 1, 1, 1, 0, -1};	// 상, 좌상, 좌, 좌하, 하, 우하, 우, 우상
	static int[] my = {0, -1, -1, -1, 0, 1, 1, 1};
	static List<Monster> monster;
	
	static int[] route;
	static int max;
	static boolean[][] visited;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		map = new ArrayList[4][4];
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				map[i][j] = new ArrayList<>();
			}
		}
		
		isDead = new int[4][4];
		
		M = Integer.parseInt(st.nextToken());
		T = Integer.parseInt(st.nextToken());
		
		st = new StringTokenizer(br.readLine());
		int r = Integer.parseInt(st.nextToken())-1;
		int c = Integer.parseInt(st.nextToken())-1;
		
		pacman = new Monster(r, c);
		monster = new ArrayList<>();
		
		for(int i=0; i<M; i++) {
			st = new StringTokenizer(br.readLine());
			r = Integer.parseInt(st.nextToken())-1;
			c = Integer.parseInt(st.nextToken())-1;
			int dir = Integer.parseInt(st.nextToken())-1;
			
			map[r][c].add(dir);
		}	
		
		for(int turn=1; turn<=T; turn++) {
			// 1. 몬스터 복제
			copyMonster();
			
			// 2. 몬스터 이동
			moveMonster();
			
			// 3. 팩맨 이동
			route = new int[] {-1, -1, -1};
			max = Integer.MIN_VALUE;
			visited = new boolean[4][4];
			findRoute(0, pacman.x, pacman.y, 0, new int[] {-1, -1, -1});
			eatMonster(turn);
			
			// 4. 몬스터 시체 소멸
			removeMonster(turn);
			
			// 5. 몬스터 복제 완성
			finishCopyMonster();
		}
		
		int total = 0;
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				total += map[i][j].size();
			}
		}
		
		System.out.println(total);
	}
	
	public static void copyMonster() {
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				for(Integer m : map[i][j]) {
					monster.add(new Monster(i, j, m));
				}
			}
		}
	}
	
	public static void moveMonster() {
		List<Integer>[][] newMap = new ArrayList[4][4];
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				newMap[i][j] = new ArrayList<>();
			}
		}
		
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				for(Integer m : map[i][j]) {
					boolean flag = false;
					for(int d=0; d<8; d++) {
						int nx = i + mx[(m+d)%8];
						int ny = j + my[(m+d)%8];
						
						if(!isRange(nx, ny) || isDead[nx][ny] > 0) continue;
						if(nx == pacman.x && ny == pacman.y) continue;
						
						newMap[nx][ny].add(m);
						flag = true;
						break;
					}
					
					if(!flag) newMap[i][j].add(m);
				}
			}
		}
		
		map = newMap;
		
	}
	
	public static void eatMonster(int turn) {
		for(int i=0; i<3; i++) {
			pacman.x += dx[route[i]];
			pacman.y += dy[route[i]];
			
			if(map[pacman.x][pacman.y].size() > 0) {
				map[pacman.x][pacman.y].clear();
				isDead[pacman.x][pacman.y] = turn;
			}
		}
	}
	
	public static void findRoute(int cur, int x, int y, int total, int[] r) {
		if(cur == 3) {
			if(total > max) {
				for(int i=0; i<3; i++) {
					route[i] = r[i];
				}
				max = total;
			}
			return;
		}
		
		for(int i=0; i<4; i++) {
			int nx = x + dx[i];
			int ny = y + dy[i];
			
			if(!isRange(nx, ny) || visited[nx][ny]) continue;
			
			visited[nx][ny] = true;
			r[cur] = i;
			findRoute(cur+1, nx, ny, total + map[nx][ny].size(), r);
			visited[nx][ny] = false;
			r[cur] = -1;
		}
	}
	
	public static void removeMonster(int turn) {
		for(int i=0; i<4; i++) {
			for(int j=0; j<4; j++) {
				if(turn - isDead[i][j] == 1) {
					isDead[i][j] = 0;
				}
			}
		}
	}
	
	public static void finishCopyMonster() {
		for(Monster m : monster) {
			map[m.x][m.y].add(m.dir);
		}
		monster.clear();
	}
	
	public static boolean isRange(int x, int y) {
		return x>=0 && x<4 && y>=0 & y<4;
	}

}