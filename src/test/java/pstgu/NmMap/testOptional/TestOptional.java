package pstgu.NmMap.testOptional;

import java.util.Optional;

public class TestOptional {

	public static Integer testMethodWithOptional(Optional<MyClass> myClass) {
		myClass.ifPresent(MyClass::myMethod);
		return myClass.map(MyClass::getSomeValue).orElse(null);
	}

	public static void main(String[] args) {
		MyClass myClass = new MyClass();

		int result2 = testMethodWithOptional(Optional.ofNullable(myClass));
		System.out.println(result2);
		
		Main.main(args);
	}
}

class MyClass {
	public void myMethod() {

	}

	public Integer getSomeValue() {
		return 2;
	}
}

class Main {
	static class A {
		public B getB() {
			return new B();
		}
	}

	static class B {
		public C getC() {
			return new C();
		}
	}

	static class C {
		public D getD() {
			return new D("from C");
		}
	}

	static class D {
		private String value;

		public D(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "D [value=" + value + "]";
		}
	}

	static class E {
	}

	public static void main(String[] args) {
		D d = Optional.ofNullable(new A()).map(A::getB).map(b -> b.getC()).map(C::getD).orElse(new D("from orElse"));
		System.out.println("d = " + d);
	}
}
