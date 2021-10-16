public class EqualsWithoutNullArgument {

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		return super.equals(obj);
	}
}


