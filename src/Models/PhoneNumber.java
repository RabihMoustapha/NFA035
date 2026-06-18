package Models;

import java.io.Serializable;
import java.util.Objects;

public class PhoneNumber implements Serializable {
    private static final long serialVersionUID = 1L;

    private String regionCode;
    private String number;

    public PhoneNumber(String regionCode, String number) {
        this.regionCode = regionCode;
        this.number = number;
    }

    public String getRegionCode() { return regionCode; }
    public String getNumber() { return number; }

    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public void setNumber(String number) { this.number = number; }

    @Override
    public String toString() {
        return regionCode + " " + number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber)) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(regionCode, that.regionCode) &&
               Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionCode, number);
    }
}