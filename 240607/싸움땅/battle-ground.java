import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class Main {
	static class Player {
		int idx;
		int x, y;
		int dir;
		int s;
		int g;
		int point;
		
		public Player(int idx, int x, int y, int dir, int s) {
			super();
			this.idx = idx;
			this.x = x;
			this.y = y;
			this.dir = dir;
			this.s = s;
			this.g = 0;
			this.point = 0;
		}

		@Override
		public String toString() {
			return "Player [idx = " + idx + ", x=" + x + ", y=" + y + ", dir=" + dir + ", s=" + s + ", g=" + g + ", point=" + point + "]";
		}
		
	}
	
	static class Map {
		int p;
		ArrayList<Integer> gun;
		
		public Map() {
			p = -1;
			gun = new ArrayList<>();
		}

		@Override
		public String toString() {
			return "Map [p=" + p + ", gun=" + gun + "]";
		}
		
	}
	
	static int N, M, K;
	static Player[] player;
	static Map[][] map;
	static int[] dx = {-1, 0, 1, 0};	// 상, 우, 하, 좌
	static int[] dy = {0, 1, 0, -1};
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		// map 초기화
		map = new Map[N][N];
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				map[i][j] = new Map();
			}
		}
		
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<N; j++) {
				int g = Integer.parseInt(st.nextToken());
				
				if(g>=0) {
					map[i][j].gun.add(g);
				}
			}
		}

		// player 초기화
		player = new Player[M];
		for(int i=0; i<M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			int d = Integer.parseInt(st.nextToken());
			int s = Integer.parseInt(st.nextToken());
			map[x][y].p = i;
			player[i] = new Player(i, x, y, d, s);
		}
		
		for(int turn=0; turn<K; turn++) {
			// 플레이어 순서대로 이동
			for(int idx=0; idx<M; idx++) {
				Player cur = player[idx];
				map[cur.x][cur.y].p = -1;
				
				int nx = cur.x + dx[cur.dir];
				int ny = cur.y + dy[cur.dir];
				
				// 만약 범위가 벗어나면 방향 반대로 해서 1칸 이동
				if(!isRange(nx, ny)) {
					cur.dir = (cur.dir+2)%4;
					nx = cur.x + dx[cur.dir];
					ny = cur.y + dy[cur.dir];
				}
				
				// 이동한 방향에 플레이어가 없을 때
				if(map[nx][ny].p == -1) {
					// 맵과 플레이어의 정보 업데이트
					map[nx][ny].p = idx;
					cur.x = nx;
					cur.y = ny;
					
					// 총이 있을 때
					if(!map[nx][ny].gun.isEmpty()) {
						changeGun(nx, ny, cur);
					}
				} else {	// 이동한 방향에 플레이어가 있을 때
					Player prev = player[map[nx][ny].p];
					Player winner, loser;
					
					// 승자 판별
					if(prev.s + prev.g > cur.s + cur.g) {
						winner = prev;
						loser = cur;
					} else if(prev.s + prev.g == cur.s + cur.g){
						if(prev.s > cur.s) {
							winner = prev;
							loser = cur;
						} else {
							winner = cur;
							loser = prev;
						}
					} else {
						winner = cur;
						loser = prev;
					}
					// 이긴 사람이 포인트 획득
					winner.point += ((winner.s + winner.g) - (loser.s + loser.g));
					
					// 진 플레이어는 총을 격자에 내려놓고 이동
					if(loser.g > 0) {
						map[nx][ny].gun.add(loser.g);
						loser.g = 0;
					}
					
					for(int i=0; i<4; i++) {
						int nnx = nx + dx[(loser.dir + i)%4];
						int nny = ny + dy[(loser.dir + i)%4];
						
						// 만약 이동한 곳이 격자 밖이거나 다른 플레이어가 존재한다면 무시
						if(!isRange(nnx, nny) || map[nnx][nny].p != -1) continue;
						
						map[nnx][nny].p = loser.idx;
						loser.x = nnx;
						loser.y = nny;
						loser.dir = (loser.dir+i)%4;
						
						// 이동한 곳에 총이 있다면
						if(!map[nnx][nny].gun.isEmpty()) {
							changeGun(nnx, nny, loser);
						}
						break;
					}
					
					// 이긴 플레이어는 있는 총 중 가장 높은걸로 바꾸기
					map[nx][ny].p = winner.idx;
					winner.x = nx;
					winner.y = ny;
					changeGun(nx, ny, winner);
				}
				
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<M; i++) {
			sb.append(player[i].point + " ");
		}
		
		System.out.println(sb.toString());
	}
	
	public static void changeGun(int x, int y, Player p) {
		if(map[x][y].gun.isEmpty()) return;
		Collections.sort(map[x][y].gun);
		int max = map[x][y].gun.get(map[x][y].gun.size()-1);
		if(p.g==0) {
			p.g = max;
			map[x][y].gun.remove(map[x][y].gun.size()-1);
		} else {
			if(p.g < max) {	// 놓여있는 총 중 가장 큰 거와 비교
				int tmp = p.g;
				p.g = max;
				map[x][y].gun.remove(map[x][y].gun.size()-1);
				map[x][y].gun.add(tmp);
			}
		}
	}
	
	public static boolean isRange(int x, int y) {
		return x>=0 && x<N && y>=0 && y<N;
	}

}