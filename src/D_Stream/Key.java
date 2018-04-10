package D_Stream;

import java.util.Arrays;

public class Key {
	
	public int[] attr;
	public long tm;
	
	public Key(int[] attr){
		this.attr = attr;
		this.tm = 0;
	}
	
	@Override
    public boolean equals(Object obj) {
        if(obj instanceof Key){
            Key key = (Key) obj;
            if(Arrays.equals(attr,key.attr))
                return true;
        }
        return false;
    }

    private int intsHashCode(int[] attr) {
        int result = 17;
//        System.out.println(attr.length);
        for (int i = 0; i < attr.length; i++)
            result = 37 * result + attr[i];
        return result;
    }

    @Override
    public int hashCode() {
        return intsHashCode(attr);
    }
}
