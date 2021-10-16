public class InadequateLogInfoInCatchBlock {
	 public static void InadequateMain(String[] args) {
	        try {
	        	
	        }
	        catch(NullPointerException e) {
	        	System.out.println("Exception thrown");
	        }
	        catch(IllegalArgumentException e) {
	        	System.out.println("Exception thrown");
	        }
	        
	    }
	 
	
}
