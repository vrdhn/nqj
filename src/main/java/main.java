package net.vardhan.nq;

/* N Queen Problem 
 *
 *  Data Structure:
 *   byte [], length equal to size of board, each value is offset from base
 *           whether index/value is x/y coordinate is immaterial.
 *           (yes, we are toast if two types of pieces are to be placed )
 * 
 *  Since 'Board' isn't a proper class, a BoardOps is static class 
 *    which implements several operations.
 * 
 *  All in all, the stuff is concurrency enabled, so parallel/stream can be 
 *  deployed here.
 *
 * 
 *      
 *    
 * 
 */
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger ;


class BoardSupplier implements Supplier<byte[]>
{
    private byte[] current;
    
    public BoardSupplier(int size) {
	// java set this to zero
	current = new byte[size]; 
    }
    
    public byte[] get() {
	// all zeros generate as last value, BoardFilter::is_allzero
	int sz = current.length;
	for( int j = 0 ; j < sz; j ++ ) {
	    if ( current[j] != sz -1 ) {
		current[j]++;
		break;
	    } else {
		current[j] = 0;
	    }
	}       
	return current;
    }
}

class BoardFilters
{

    /** Repeat Check **/
    private byte[] seen;

    public boolean is_notlast( final byte[] thing ) {
	if ( seen == null ) { // actually the first ?
	    seen = thing.clone();
	    return true;
	} else {
	    for (int j= 0 ; j < seen.length ; j++ )
		if ( thing[j] != seen[j] ) return true;
	    return false;
	}	
    }
    
    
    public boolean is_solution(final byte[] thing) {
	for( byte x1 = 0 ; x1 < thing.length ; x1 ++ )
	    for( byte x2 = 0 ; x2 < x1 ; x2 ++ ) {
		byte y1 = thing[x1];
		byte y2 = thing[x2];
		
		int dx1x2 = x1 - x2;
		int dy1y2 = y1 - y2;
		// horizontally same ?
		if  ( dy1y2 == 0 )
		    return false;
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

    // generate all equivalents and return the smallest one.
    // save some memory as well
    public byte[] canonical(final byte[] thing ) {
    	// 11 out of 12 .. ret will be different than arg.
    	byte[] ret = thing.clone();
    	byte[] alt = thing.clone();
    	
    	byte[] tmp;		// For swapping
    
    	if ( try_rot90( ret, alt) ) { tmp = ret; ret = alt ; alt = tmp;}
    	if ( try_rot180( ret, alt) ) { tmp = ret; ret = alt ; alt = tmp;}	
    	if ( try_rot270( ret, alt) ) { tmp = ret; ret = alt ; alt = tmp;}	
    	if ( try_ref_ver( ret, alt) ) { tmp = ret; ret = alt ; alt = tmp;}
    	if ( try_ref_hor( ret, alt) ) { tmp = ret; ret = alt ; alt = tmp;}
    
    	return ret;
    }

    public void print(final byte[] thing) {
	for ( int j = 0 ; j < 8 ; j ++ ) {
	    for ( int k = 0 ; k < thing[j] ; k ++ ) System.out.print('.');
	    System.out.print('Q');
	    for( int k = thing[j] + 1 ; k < 8 ; k ++ ) System.out.print('.');
	    System.out.print('\n');
	}
    }
}

 
class Main
{


    public static void main (String[] args) {

	int size = 8;		// TODO parse from command line, limit to 127
	AtomicInteger cnt = new AtomicInteger();
	
	BoardSupplier bs = new BoardSupplier(8);
	BoardFilters   bf = new BoardFilters(); // Only to avoid static functions in it.

	Stream.generate(bs)
	    .filter( e -> bf.is_solution(e)  )
	    .peek( e-> bf.print(e) )
	    .peek( e -> System.out.printf("#%d : %s\n",cnt.addAndGet(1),Arrays.toString(e)))
	    // bad way to stop inf stream; java 9/10 has 
	    .allMatch( e->  bf.is_notlast(e));
	;
	
	
	System.out.println("Yeah1");
    }
}
