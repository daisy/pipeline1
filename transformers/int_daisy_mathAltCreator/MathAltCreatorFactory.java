package int_daisy_mathAltCreator;

/**
 * Produce instances of {@link IMathAltCreator}.
 * @author Markus Gylling
 */
public class MathAltCreatorFactory {

	private static final String PROPERTY = "int_daisy_mathAltCreator.IMathAltCreator";
	
	private MathAltCreatorFactory() {
		
	}
	
	public static MathAltCreatorFactory newInstance() {
		return new MathAltCreatorFactory();
	}
	
	/**
	 * Create an instance of IMathAltCreator using system properties
	 * for identity retrieval.
	 * @return an IMathAltCreator instance or null if none could be found.
	 */
	public IMathAltCreator newMathAltCreator() {
		String name = System.getProperty(PROPERTY);
		if(name!=null) {
			try {
				Class<?> c = Class.forName(name);
				return (IMathAltCreator) c.newInstance();
			} catch (ClassNotFoundException e) {				
				
			} catch (InstantiationException e) {
				
			} catch (IllegalAccessException e) {
				
			}
			
			
		}
		return null;
	}
}
