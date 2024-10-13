import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

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
	
	static class Santa implements Comparable<Santa> {
		int idx;
		int x, y;
		int point;
		int faint;
		public Santa(int idx, int x, int y) {
			super();
			this.idx = idx;
			this.x = x;
			this.y = y;
			this.point = 0;
			this.faint = -1;
		}
		@Override
		public String toString() {
			return "Santa [idx=" + idx + ", x=" + x + ", y=" + y + ", point=" + point + ", faint=" + faint + "]";
		}
		@Override
		public int compareTo(Santa o) {
			int dist1 = distance(this.x, this.y);
			int dist2 = distance(o.x, o.y);
			
			if(dist1 == dist2) {
				if(this.x == o.x)
					return o.y - this.y;
				return o.x - this.x;
			}
			return dist1 - dist2;
		}
	}
	
	static int N, M, P, C, D;
	static int[][] map;
	static Deer deer;
	static Santa[] santa;
	static boolean[] isDead;
	static final int DEER = -1;
	static int[] dx = {-1, 0, 1, 0};	// 상, 우, 하, 좌
	static int[] dy = {0, 1, 0, -1};
	static int deadCnt;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
		
		map = new int[N][N];
		santa = new Santa[P+1];
		isDead = new boolean[P+1];
		
		st = new StringTokenizer(br.readLine());
		int x = Integer.parseInt(st.nextToken())-1;
		int y = Integer.parseInt(st.nextToken())-1;
		deer = new Deer(x, y);
		map[x][y] = DEER;
		
		for(int i=1; i<=P; i++) {
			st = new StringTokenizer(br.readLine());
			int idx = Integer.parseInt(st.nextToken());
			x = Integer.parseInt(st.nextToken())-1;
			y = Integer.parseInt(st.nextToken())-1;
			
			santa[idx] = new Santa(idx, x, y);
			map[x][y] = idx;
		}
		
		for(int turn=1; turn<=M; turn++) {
			// 가장 가까운 산타 찾기
			Santa target = findSanta();
			if(target == null)
				break;
			
			// 루돌프 돌진
			moveDeer(turn, target);
			
			if(deadCnt==P) break;
			
			// 산타 움직임
			moveSanta(turn);
			if(deadCnt==P) break;
			
			// 탈락하지 않으면 1점 추가
			for(int i=1; i<=P; i++) {
				if(isDead[i]) continue;
				santa[i].point++;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i=1; i<=P; i++) {
			sb.append(santa[i].point+" ");
		}
		System.out.println(sb.toString());
	}
	
	private static void moveDeer(int turn, Santa target) {
		int moveX=0, moveY=0;
		
		if(deer.x > target.x) {
			moveX = -1;
		} else if(deer.x < target.x) {
			moveX = 1;
		}
		
		if(deer.y > target.y) {
			moveY = -1;
		} else if(deer.y < target.y) {
			moveY = 1;
		}
		
		int nx = deer.x + moveX;
		int ny = deer.y + moveY;
		
		map[deer.x][deer.y] = 0;

		// 이동하는 칸에 산타가 없다면
		if(map[nx][ny] == 0) {
			deer.x = nx;
			deer.y = ny;
			map[deer.x][deer.y] = DEER;
			return;
		} 
		
		// 이동하는 칸에 산타가 있다면
		if(map[nx][ny] > 0) {
			// 산타 돌진
			int idx = map[nx][ny];
			target = santa[idx];
			target.point += C;
			
			int nnx = nx + (C*moveX);
			int nny = ny + (C*moveY);
			
			// 튕겨져 나간 곳이 map 바깥이면 탈출
			if(!isRange(nnx, nny)) {
				isDead[idx] = true;
				deadCnt++;
				deer.x = nx;
				deer.y = ny;
				map[nx][ny] = DEER;
				return;
			}
			
			target.faint = turn;
			
			// 튕겨져 나간 곳에 아무도 없으면
			if(map[nnx][nny] == 0) {
				target.x = nnx;
				target.y = nny;
				map[nnx][nny] = idx;
				deer.x = nx;
				deer.y = ny;
				map[nx][ny] = DEER;
				return;
			}
			
			// 튕겨져 나간 곳에 또 다른 산타가 있으면 상호작용
			interaction(nnx, nny, moveX, moveY);
			map[nx][ny] = 0;
			map[nnx][nny] = idx;
			santa[idx].x = nnx;
			santa[idx].y = nny;
			
			// 루돌프 이동
			deer.x = nx;
			deer.y = ny;
			map[nx][ny] = DEER;
		}
	}
	
	private static void moveSanta(int turn) {
		for(int idx=1; idx<=P; idx++) {
			// 탈출했거나 기절해있으면 움직일 수 없다
			if(isDead[idx]) continue;
			if(santa[idx].faint > 0 && turn < santa[idx].faint + 2) continue;
			int dist = distance(santa[idx].x, santa[idx].y);
			int dir = -1;
			
			// 좌표 구하기
			for(int i=0; i<4; i++) {
				int nx = santa[idx].x + dx[i];
				int ny = santa[idx].y + dy[i];
				
				if(!isRange(nx, ny) || map[nx][ny] > 0) continue;
				int d = distance(nx, ny);
				if(d < dist) {
					dist = d;
					dir = i;
				}
			}
			
			// 움직일 수 없으면 안움직임
			if(dir == -1) continue;
			
			int nx = santa[idx].x + dx[dir];
			int ny = santa[idx].y + dy[dir];
			map[santa[idx].x][santa[idx].y] = 0;
			santa[idx].x = nx;
			santa[idx].y = ny;
			
			if(map[nx][ny] == 0) {
				map[nx][ny] = idx;
			} else if(map[nx][ny] == DEER) {	// 루돌프가 있으면
				dir = (dir+2)%4;
				int moveX = dx[dir];
				int moveY = dy[dir];
				
				santa[idx].point += D;
				santa[idx].faint = turn;
				nx = santa[idx].x + (D*moveX);
				ny = santa[idx].y + (D*moveY);
				
				if(!isRange(nx, ny)) {
					isDead[idx] = true;
					deadCnt++;
					continue;
				}
				if(map[nx][ny] == 0) {
					map[nx][ny] = idx;
					santa[idx].x = nx;
					santa[idx].y = ny;
				} else if(map[nx][ny] > 0) {
					interaction(nx, ny, moveX, moveY);
					santa[idx].x = nx;
					santa[idx].y = ny;
					map[nx][ny] = idx;
				}
			}
		}
	}
	
	private static Santa findSanta() {
		PriorityQueue<Santa> pq = new PriorityQueue<>();
		
		for(int i=1; i<=P; i++) {
			if(isDead[i]) continue;
			pq.offer(santa[i]);
		}
		
		return pq.poll();
	}
	
	private static void interaction(int x, int y, int moveX, int moveY) {
		int idx = map[x][y];
		int nx = x + moveX;
		int ny = y + moveY;
		
		// 다음 곳이 map 바깥이면 탈출
		if(!isRange(nx, ny)) {
			isDead[idx] = true;
			map[x][y] = 0;
			deadCnt++;
			return;
		}

		// 만약 다음 곳에 다른 산타가 있다면
		if(map[nx][ny] > 0) {
			interaction(nx, ny, moveX, moveY);
		}
		
		if(map[nx][ny] == 0) {
			map[x][y] = 0;
			santa[idx].x = nx;
			santa[idx].y = ny;
			map[nx][ny] = idx;
			return;
		}
	}
	
	private static boolean isRange(int x, int y) {
		return 0<=x && x<N && 0<=y && y<N;
	}
	
	private static int distance(int x, int y) {
		x = deer.x - x;
		y = deer.y - y;
		return x*x + y*y;
	}
}