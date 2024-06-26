import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		int n = Integer.parseInt(br.readLine());
		int[] store = new int[n];
		
		st = new StringTokenizer(br.readLine());
		for(int i=0; i<n; i++) {
			store[i] = Integer.parseInt(st.nextToken());
		}
		
		st = new StringTokenizer(br.readLine());
		int boss = Integer.parseInt(st.nextToken());
		int sub = Integer.parseInt(st.nextToken());
		
		long result = n;
		
		for(int i=0; i<n; i++) {
			store[i] -= boss;
			if(store[i] <= 0) continue;
			
			result += (long)(Math.ceil((double)store[i]/sub));
		}
		
		System.out.println(result);
	}

}