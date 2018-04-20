package net.vardhan.nq;

/* N Queen Problem 
 *
 *  Data Structure:
 *   byte [], length equal to size of board, each value is offset from base
 *           whether index/value is x/y coordinate is immaterial.
 *           (yes, we are toast if two types of pieces are to be placed )
 *      
 *    
 * 
 */
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;
import java.util.ArrayList;


/*
 * Generate n! boards
 *   
 */
class BoardSupplier implements Supplier<byte[]>
{
    private byte[] current;
    
    public BoardSupplier(int size) {
	// java set this to zero
	current = new byte[size]; 
    }
    
    public byte[] get() {
	int sz = current.length;
	boolean horizontal_check = true;
	while ( horizontal_check == true ) {
	    for( int j = 0 ; j < sz; j ++ ) {
		if ( current[j] != sz -1 ) {
		    current[j]++;
		    break;
		} else {
		    current[j] = 0;
		}		
	    }
	    // do horizontal check
	    horizontal_check = false;
	    for( int j = 0 ; j < sz; j ++ )
		for( int k = 0 ; k < j ; k ++ )
		    if ( current[j] == current[k] )
			horizontal_check = true;
	}
	return current.clone();
    }
}

class BoardFilters
{

        
    public boolean is_solution(final byte[] thing) {
	for( byte x1 = 0 ; x1 < thing.length ; x1 ++ )
	    for( byte x2 = 0 ; x2 < x1 ; x2 ++ ) {
		byte y1 = thing[x1];
		byte y2 = thing[x2];
		
		int dx1x2 = x1 - x2;
		int dy1y2 = y1 - y2;
		
		// horizontally same : not needed already done by generate
		//if  ( dy1y2 == 0 )
		//    return false;
		
		// diagonally same ?
		if ( dy1y2 == dx1x2 || dy1y2 == -dx1x2 )
		    return false;
		// three queen in same line.
		// (x1-x2)/(x2-x3) == (y1-y2)/(y2-y3)
		// =>(x1-x2)*(y2-y3) == (y1-y2)*/(x2-x3)
		for( byte x3 = 0 ; x3 < x2 ; x3 ++ ) {		    
		    byte y3 = thing[x3];

		    int dx2x3 = x2 - x3;
		    int dy2y3 = y2 - y3;

		    if ( dx1x2 * dy2y3 == dx2x3 * dy1y2 )
			return false;
		}
	    }
	return true;
    }


    /* rotate by 90 degrees */
    public byte[] rot(final byte[] in) {
	
	byte max = (byte)(in.length -1);	
	byte [] ret = new byte[in.length];

	for( int j  = 0 ; j < in.length ; j ++ )
	    ret [ max-in[j] ] = (byte)j;

	// assert that it's still a solution..
	if ( false == is_solution(ret) ) {
	    System.out.println("rot Fails");
	    System.exit(1);
	}
	
	return ret;
    }
    /* reflect horizontally */
    public byte[] rfl(final byte[] in) {
	
	byte max = (byte)(in.length -1);	
	byte [] ret = new byte[in.length];

	for( int j  = 0 ; j < in.length ; j ++ )
	    ret [ j ] = in[max-j];

	// assert that it's still a solution..
	if ( false == is_solution(ret) ) {
	    System.out.println("refl0 Fails");
	    System.exit(1);
	}
	
	return ret;
    }
    
    public boolean less_than( final byte[] a, final byte [] b) {
	for ( int j = 0; j < a.length ; j ++ ) {
	    if ( a[j] < b[j] ) return false;
	    if ( a[j] > b[j] ) return true;
	}
	return false;
    }
    // generate all equivalents and return the smallest one.
    // TODO: reduce garbage generation, reuse byte[]
    // TODO: merge less_than with rot/rfl for lesser garbage & speed
    public byte[] canonical(final byte[] orig ) {

	
	byte[] rot0 = orig.clone();
	byte[] rot1 = rot(rot0);
	byte[] rot2 = rot(rot1);
	byte[] rot3 = rot(rot2);
	
	byte[] rfl0 = rfl(rot0);
	byte[] rfl1 = rfl(rot1);
	byte[] rfl2 = rfl(rot2);
	byte[] rfl3 = rfl(rot3);

	
	byte[] ret = rot0;
	if( less_than(ret, rot1) ) ret = rot1;	
	if( less_than(ret, rot2) ) ret = rot2;	
	if( less_than(ret, rot3) ) ret = rot3;
	if( less_than(ret, rfl0) ) ret = rfl0;
	if( less_than(ret, rfl1) ) ret = rfl1;
	if( less_than(ret, rfl2) ) ret = rfl2;
	if( less_than(ret, rfl3) ) ret = rfl3;


	//System.out.println("<===");
	//System.out.println(Arrays.toString(rot0));
	//System.out.println(Arrays.toString(rfl0));
	//
	//System.out.println(Arrays.toString(rot1));
	//System.out.println(Arrays.toString(rfl1));
	//
	//System.out.println(Arrays.toString(rot2));
	//System.out.println(Arrays.toString(rfl2));
	//
	//System.out.println(Arrays.toString(rot3));
	//System.out.println(Arrays.toString(rfl3));
	//
	//System.out.println(Arrays.toString(ret));
	//System.out.println("===>");
       
    	return ret;
    }

}

 
class Main
{

    // HashSet need this to work properly!
    private static ArrayList<Byte> asALB(final byte[] arr) {
	ArrayList<Byte> ret = new ArrayList<Byte>();
	for ( byte b : arr )
	    ret.add(b);
	return ret;
    }

    // print it out.
    private static void print(final ArrayList<Byte> thing) {
	for ( int j = 0 ; j < 8 ; j ++ ) {
	    for ( int k = 0 ; k < thing.get(j) ; k ++ ) System.out.print('.');
	    System.out.print('Q');
	    for( int k = thing.get(j) + 1 ; k < 8 ; k ++ ) System.out.print('.');
	    System.out.print('\n');
	}
    }

    // need to limit the infinite generator
    private static int fact(int sz) {
	int pro = 1;
	for( ; sz > 1 ; sz-- )
	    pro *= sz;
	return  pro;
    }
    
    public static void main (String[] args) {
	
	final int size = 8;		// TODO parse from command line, limit to 127
	
	BoardSupplier bs = new BoardSupplier(size);
	BoardFilters   bf = new BoardFilters(); // Only to avoid static functions

	// byte[] can't be added to HashSet !
	HashSet<ArrayList<Byte>> result = Stream.generate(bs)
	    .limit(fact(size))	
	    .filter( e -> bf.is_solution(e)  )
	    .map( e-> bf.canonical(e) )
	    .reduce(new HashSet<ArrayList<Byte>>(),
		    (hs,e) -> {hs.add(asALB(e)); return hs ; },
		    (hs1,hs2) -> {hs1.addAll(hs2); return hs1;})
	    ;
	
	int count = 0;
	for( ArrayList<Byte> e : result ) {
	    System.out.printf("#%d : %s\n",count++,e);
	    print(e);
	}
	
	System.out.println("Yeah!");
    }
}
