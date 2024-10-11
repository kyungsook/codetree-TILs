import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

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
	
	static class Person {
		int idx;
		int x, y;
		int dist;
		
		public Person(int idx, int x, int y, int dist) {
			super();
			this.idx = idx;
			this.x = x;
			this.y = y;
			this.dist = dist;
		}
		
		@Override
		public String toString() {
			return "Person [idx=" + idx + ", x=" + x + ", y=" + y + ", dist=" + dist + "]";
		}
	}
	
	static int N, M, K;
	static int[][] map;
	static List<Person>[][] pMap;
	static Person[] person;
	static Data exit;
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
		
		map = new int[N][N];
		pMap = new ArrayList[N][N];
		person = new Person[M+1];
		isFinished = new boolean[M+1];
		
		// pMap 초기화
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				pMap[i][j] = new ArrayList<>();
			}
		}
		
		// map 정보 초기화
		for(int i=0; i<N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=0; j<N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		// 참가자 정보
		for(int i=1; i<=M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			
			person[i] = new Person(i, x, y, 0);
			pMap[x][y].add(person[i]);
		}
		
		// 출구 정보
		st = new StringTokenizer(br.readLine());
		int x = Integer.parseInt(st.nextToken())-1;
		int y = Integer.parseInt(st.nextToken())-1;
		exit = new Data(x, y);
		
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
		int xStart = res[0];
		int yStart = res[1];
		int size = res[2];
		
		int[][] newMap = new int[N][N];
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				newMap[i][j] = map[i][j];
			}
		}
		
		List<Person>[][] newPMap = new ArrayList[N][N];
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				newPMap[i][j] = new ArrayList<>();
				for(Person p  : pMap[i][j])
					newPMap[i][j].add(p);
			}
		}
		
		// maze 회전
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				// 미로 회전, 내구성--
				map[j+xStart][size-1-i+yStart] = newMap[i+xStart][j+yStart] > 0 ? 
						newMap[i+xStart][j+yStart]-1 : 0;
				
				// pMap 회전
				pMap[j+xStart][size-1-i+yStart] = newPMap[i+xStart][j+yStart];
			}
		}
		
		// people 좌표 바꾸기
		for(int i=xStart, xLen=xStart+size; i<xLen; i++) {
			for(int j=yStart, yLen=yStart+size; j<yLen; j++) {
				for(Person p : pMap[i][j]){
					p.x = i;
					p.y = j;
				}
			}
		}
		
		// exit 회전
		int nx = exit.y - yStart + xStart;
		int ny = size - 1 - exit.x + xStart + yStart;
		exit.x = nx;
		exit.y = ny;
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
							if(pMap[i][j].size() > 0) pFlag = true;
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
		Data d = findNextBlank(cur.x, cur.y);
		
		// 움직일 수 없으면 안움직임
		if(d.x == 0 && d.y == 0) return;
		
		// 이동하기
		int nx = cur.x + d.x;
		int ny = cur.y + d.y;
		
		// 만약 이동할 곳이 출구라면 즉시 탈출
		if(nx == exit.x && ny == exit.y) {
			pMap[cur.x][cur.y].remove(cur);
			isFinished[idx] = true;
			cur.dist++;
			finCnt++;
			return;
		}
		
		// 그렇지 않다면 이동
		pMap[cur.x][cur.y].remove(cur);
		cur.x = nx;
		cur.y = ny;
		cur.dist++;
		pMap[nx][ny].add(cur);
	}
	
	private static Data findNextBlank(int x, int y) {
		int nx = 0, ny = 0;
		int curDist = dist(x, y);
		
		for(int i=0; i<4; i++) {
			int nnx = x + dx[i];
			int nny = y + dy[i];
			
			if(!isRange(nnx, nny) || map[nnx][nny] > 0) continue;
			int nDist = dist(nnx, nny);
			if(nDist < curDist) {
				nx = dx[i];
				ny = dy[i];
				break;
			}
		}
		
		return new Data(nx, ny);
	}
	
	private static int dist(int x, int y) {
		return Math.abs(x - exit.x) + Math.abs(y - exit.y);
	}
	
	private static boolean isRange(int x, int y) {
		return x>=0 && x<N && y>=0 && y<N;
 	}

}