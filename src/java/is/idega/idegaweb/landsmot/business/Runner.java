package is.idega.idegaweb.landsmot.business;


import java.sql.Date;

import com.idega.core.location.data.Country;
import com.idega.user.data.Gender;
import com.idega.user.data.User;

public class Runner {
	
	private User user;

	private boolean rentChip;
	private boolean ownChip;
	private boolean buyChip;
	private String chipNumber;
	private boolean transportOrdered;
	private boolean noTransportOrdered;
	private String shirtSize;

	private String name;
	private String personalID;
	private Date dateOfBirth;
	private String address;
	private String city;
	private String postalCode;
	private Country country;
	private Gender gender;
	private Country nationality;
	private String email;
	private String homePhone;
	private String mobilePhone;
	private boolean agree;
	private float amount;

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isRentChip() {
		return this.rentChip;
	}

	public void setRentChip(boolean rentChip) {
		this.rentChip = rentChip;
		if (rentChip) {
			setOwnChip(false);
			setBuyChip(false);
		}
	}

	public boolean isOwnChip() {
		return this.ownChip;
	}

	public void setOwnChip(boolean ownChip) {
		this.ownChip = ownChip;
		if (ownChip) {
			setRentChip(false);
			setBuyChip(false);
		}
	}

	public boolean isBuyChip() {
		return this.buyChip;
	}

	public void setBuyChip(boolean buyChip) {
		this.buyChip = buyChip;
		if (buyChip) {
			setOwnChip(false);
			setRentChip(false);
		}
	}

	public String getChipNumber() {
		return this.chipNumber;
	}

	public void setChipNumber(String chipNumber) {
		this.chipNumber = chipNumber;
	}
	
	public boolean isTransportOrdered() {
		return this.transportOrdered;
	}

	public void setTransportOrdered(boolean transportOrdered) {
		this.noTransportOrdered = !transportOrdered;
		this.transportOrdered = transportOrdered;
	}

	public boolean isNoTransportOrdered() {
		return this.noTransportOrdered;
	}

	public void setNoTransportOrdered(boolean noTransportOrdered) {
		this.transportOrdered = !noTransportOrdered;
		this.noTransportOrdered = noTransportOrdered;
	}

	public String getShirtSize() {
		return this.shirtSize;
	}

	public void setShirtSize(String shirtSize) {
		this.shirtSize = shirtSize;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHomePhone() {
		return this.homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getMobilePhone() {
		return this.mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Country getNationality() {
		return this.nationality;
	}

	public void setNationality(Country nationality) {
		this.nationality = nationality;
	}

	public String getPostalCode() {
		return this.postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPersonalID() {
		return this.personalID;
	}

	public void setPersonalID(String personalID) {
		this.personalID = personalID;
	}
	
	public Gender getGender() {
		return this.gender;
	}
	
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	
	public boolean isAgree() {
		return this.agree;
	}

	
	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	
	public float getAmount() {
		return this.amount;
	}

	
	public void setAmount(float amount) {
		this.amount = amount;
	}
}
