package pstgu.NmMap.testOptional;

import java.util.Date;
import java.util.Optional;

public class Person {

	private Optional<String> firstName;
	
	private Optional<String> secondName;
	
	private Optional<Integer> age;
	
	private Optional<PersonAddress> address;
	
	public Optional<String> getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = Optional.ofNullable(firstName);
	}

	public Optional<String> getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = Optional.of(secondName);
	}

	public Optional<Integer> getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = Optional.ofNullable(age);
	}
	
	public Optional<PersonAddress> getAddress() {
		return address;
	}

	public void setAddress(PersonAddress address) {
		this.address = Optional.of(address);
	}
	
	public static void main(String[] args) {
		Person p = null;
		Optional<Person> person = Optional.ofNullable(p);
		String streetName = person.flatMap(Person::getAddress)
	               .flatMap(PersonAddress::getStreet)
	               .map(PersonAddressStreet::getStreetName)
	               .orElse("EMPTY");
	}
}