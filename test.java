import java.util.ArrayList;

public class test {
    public static void main(String[] args){
        ArrayList<int[]> ls = new ArrayList<int[]>();
        int x = 1;
        int y = 2;
        // So the below works :)
        for(int i = 0;i<5;i++) { 
            int[] pos = {x,y};
            ls.add(pos);
            x += 1;
            y += 1;
        }
    
        for(int[] xy: ls)
        {
            String string = Integer.toString(xy[0]) + Integer.toString(xy[1]);
            System.out.println(string);
        }

    }
}
