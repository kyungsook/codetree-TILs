import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static class Data {
		int x, y;
		int num;
		public Data(int x, int y, int num) {
			super();
			this.x = x;
			this.y = y;
			this.num = num;
		}
		@Override
		public String toString() {
			return "Data [x=" + x + ", y=" + y + ", num=" + num + "]";
		}
	}
	
	static int K, M;
	static int[][] map;
	static int[][] rotateMap;
	static int[] newNum;
	static int newNumIdx;
	static int maxValue;
	static boolean[][] visited;
	static int[] dx = {-1, 1, 0, 0};
	static int[] dy = {0, 0, -1, 1};
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[5][5];
		newNum = new int[M];
		rotateMap = new int[5][5];
		
		for(int i=0; i<5; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<5; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		st = new StringTokenizer(br.readLine());
		for(int i=0; i<M; i++) {
			newNum[i] = Integer.parseInt(st.nextToken());
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(int turn=0; turn<K; turn++) {
			maxValue = 0;
			// 1차 획득 가치를 최대화하는 회전 찾기
			findRotate();
			
			if(maxValue == 0) break;
			
			// 1차 유물 획득하기
			for(int i=0; i<5; i++) {
				for(int j=0; j<5; j++) {
					map[i][j] = rotateMap[i][j];
				}
			}
			
			// 1차 획득 후 새로운 유물 채우기
			fillMap();
			
			// 연쇄 획득하기
			while(true) {
				visited = new boolean[5][5];
				int cnt = 0;
				for(int i=0; i<5; i++) {
					for(int j=0; j<5; j++) {
						if(visited[i][j]) continue;
						int res = bfs(map, i, j);
						cnt += res >= 3 ? res : 0;
					}
				}
				
				if(cnt == 0) break;
				maxValue += cnt;
				fillMap();
			}
			
			sb.append(maxValue + " ");
		}
		
		System.out.println(sb.toString());
	}
	
	private static void findRotate() {
		for(int k=1; k<=3; k++) {
			for(int c=0; c<3; c++) {
				for(int r=0; r<3; r++) {
					int[][] newMap = rotate(k, r, c);
					
					visited = new boolean[5][5];
					int cnt = 0;
					for(int i=0; i<5; i++) {
						for(int j=0; j<5; j++) {
							if(visited[i][j]) continue;
							int res = bfs(newMap, i, j);
							cnt += res >= 3 ? res : 0;
						}
					}
					
					if(cnt > maxValue) {
						maxValue = cnt;
						copyMap(newMap);
					}
				}
			}
		}
	}
	
	private static void fillMap() {
		for(int j=0; j<5; j++) {
			for(int i=4; i>=0; i--) {
				if(map[i][j] == 0) {
					map[i][j] = newNum[newNumIdx];
					newNumIdx++;
				}
			}
		}
	}
	
	private static int[][] rotate(int cnt, int x, int y) {
		int[][] newMap = new int[5][5];
		
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				newMap[i][j] = map[i][j];
			}
		}
		
		for(int k=0; k<cnt; k++) {
			for(int i=x; i<x+3; i++) {
				for(int j=y; j<y+3; j++) {
					int ox = i-x, oy = j-y;
					int rx = oy, ry = 2-ox;
					newMap[rx+x][ry+y] = map[i][j];
				}
			}
		}
		return newMap;
	}
	
	private static void copyMap(int[][] src) {
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				rotateMap[i][j] = src[i][j];
			}
		}
	}
	
	private static int bfs(int[][] newMap, int x, int y) {
		Queue<Data> q = new ArrayDeque<>();
		Queue<Data> res = new ArrayDeque<>();
		q.offer(new Data(x, y, newMap[x][y]));
		res.offer(new Data(x, y, newMap[x][y]));
		visited[x][y] = true;
		
		int cnt = 1;
		Data cur;
		while(!q.isEmpty()) {
			cur = q.poll();
			
			for(int i=0; i<4; i++) {
				int nx = cur.x + dx[i];
				int ny = cur.y + dy[i];
				
				if(!isRange(nx, ny) || visited[nx][ny]) continue;
				if(newMap[nx][ny] == cur.num) {
					cnt++;
					q.offer(new Data(nx, ny, cur.num));
					res.offer(new Data(nx, ny, cur.num));
					visited[nx][ny] = true;
				}
			}
		}
		
		if(cnt >= 3) {
			while(!res.isEmpty()) {
				cur = res.poll();
				newMap[cur.x][cur.y] = 0;
			}
		}
		
		return cnt;
	}
	
	private static boolean isRange(int x, int y) {
		return 0<=x && x<5 && 0<=y && y<5;
	}
	
}