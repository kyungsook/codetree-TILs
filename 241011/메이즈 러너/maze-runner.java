import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
	static class Person {
		int x, y;
		int dist;
		
		public Person(int x, int y) {
			super();
			this.x = x;
			this.y = y;
			this.dist = 0;
		}

		@Override
		public String toString() {
			return "Person [x=" + x + ", y=" + y + ", dist=" + dist + "]";
		}
	}
	
	static class Map {
		List<Integer> pList;
		int wall;
		
		public Map() {
			this.wall = 0;
			this.pList = new ArrayList<>();
		}
		
		public Map(int wall) {
			super();
			this.wall = wall;
			this.pList = new ArrayList<>();
		}
		
		public void delete(int idx) {
			pList.remove(Integer.valueOf(idx));
		}

		@Override
		public String toString() {
			return "Map [pList=" + pList + ", wall=" + wall + "]";
		}
	}
	
	static int N, M, K;
	static Map[][] map;
	static Person[] person;
	static Person exit;
	static int finCnt;
	static boolean[] isFinished;
	static int[] dx = {-1, 1, 0, 0};
	static int[] dy = {0, 0, -1, 1};
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new Map[N][N];
		person = new Person[M+1];
		isFinished = new boolean[M+1];
		
		// map 정보 초기화
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<N; j++) {
				map[i][j] = new Map(Integer.parseInt(st.nextToken()));
			}
		}
		
		// 참가자 정보
		for(int i=1; i<=M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			
			person[i] = new Person(x, y);
			map[x][y].pList.add(i);
		}
		
		// 출구 정보
		st = new StringTokenizer(br.readLine());
		int x = Integer.parseInt(st.nextToken())-1;
		int y = Integer.parseInt(st.nextToken())-1;
		exit = new Person(x, y);
		
		for(int turn=0; turn<K; turn++) {
			// 모든 참가자 1칸씩 움직이기
			for(int i=1; i<=M; i++) {
				// 참가자 이동
				if(isFinished[i]) continue;
				movePeople(i);
			}
			if(finCnt == M) break;
			
			// 미로 회전
			rotateMaze();
		}
		
		StringBuilder sb = new StringBuilder();
		int sum = 0;
		for(int i=1; i<=M; i++) {
			sum += person[i].dist;
		}
		sb.append(sum + "\n");
		sb.append((exit.x+1) + " " + (exit.y+1));
		System.out.println(sb.toString());
	}
	
	private static void rotateMaze() {
		int[] res = getSquare();
		int x = res[0];
		int y = res[1];
		int size = res[2];
		
		// maze 회전
		Map[][] newMap = new Map[N][N];
		for(int i=x; i<x+size; i++) {
			for(int j=y; j<y+size; j++) {
				int ox = i-x, oy = j-y;
				int rx = oy, ry = size-1-ox;
				newMap[rx+x][ry+y] = new Map();
				newMap[rx+x][ry+y].wall = map[i][j].wall > 0 ? map[i][j].wall-1 : 0;
				copyList(newMap[rx+x][ry+y].pList, map[i][j].pList);
			}
		}
		
		// people 좌표 바꾸기
		for(int i=x; i<x+size; i++) {
			for(int j=y; j<y+size; j++) {
				map[i][j].wall = newMap[i][j].wall;
				copyList(map[i][j].pList, newMap[i][j].pList);
				// 사람들 좌표 바꾸기
				for(int idx : map[i][j].pList) {
					person[idx].x = i;
					person[idx].y = j;
				}
			}
		}
		
		// exit 회전
		int ox = exit.x - x, oy = exit.y - y;
		int rx = oy, ry = size - ox -1;
		exit.x = rx+x;
		exit.y = ry+y;
	}
	
	private static int[] getSquare() {
		int[] res = new int[3];
		
		boolean pFlag = false, eFlag = false;
		for(int size=2; size<=5; size++) {
			for(int xStart=0; xStart<=N-size; xStart++) {
				for(int yStart=0; yStart<N-size; yStart++) {
					eFlag = false;
					pFlag = false;
					for(int i=xStart, xMax = xStart+size; i<xMax; i++) {
						for(int j=yStart, yMax = yStart+size; j<yMax; j++) {
							if(exit.x == i && exit.y == j) eFlag = true;
							if(map[i][j].pList.size() > 0) pFlag = true;
						}
					}
					if(eFlag && pFlag) {
						res[0] = xStart;
						res[1] = yStart;
						res[2] = size;
						return res;
					}
				}
			}
		}
		return res;
	}
	
	private static void movePeople(int idx) {
		Person cur = person[idx];
		Person d = findNextBlank(cur.x, cur.y);
		
		// 움직일 수 없으면 안움직임
		if(d.x == 0 && d.y == 0) return;
		
		// 이동하기
		int nx = cur.x + d.x;
		int ny = cur.y + d.y;
		
		// 만약 이동할 곳이 출구라면 즉시 탈출
		if(nx == exit.x && ny == exit.y) {
			map[cur.x][cur.y].delete(idx);;
			isFinished[idx] = true;
			cur.dist++;
			finCnt++;
			return;
		}
		
		// 그렇지 않다면 이동
		map[cur.x][cur.y].delete(idx);
		cur.x = nx;
		cur.y = ny;
		cur.dist++;
		map[cur.x][cur.y].pList.add(idx);
	}
	
	private static Person findNextBlank(int x, int y) {
		int nx = 0, ny = 0;
		int curDist = dist(x, y);
		
		for(int i=0; i<4; i++) {
			int nnx = x + dx[i];
			int nny = y + dy[i];
			
			if(!isRange(nnx, nny) || map[nnx][nny].wall > 0) continue;
			int nDist = dist(nnx, nny);
			if(nDist < curDist) {
				nx = dx[i];
				ny = dy[i];
				break;
			}
		}
		
		return new Person(nx, ny);
	}
	
	private static List<Integer> copyList(List<Integer> dest, List<Integer> src){
		dest.clear();
		for(int num : src) {
			dest.add(num);
		}
		return dest;
	}
	
	private static int dist(int x, int y) {
		return Math.abs(x - exit.x) + Math.abs(y - exit.y);
	}
	
	private static boolean isRange(int x, int y) {
		return x>=0 && x<N && y>=0 && y<N;
 	}

}