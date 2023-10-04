package com.example.test_app_DTCs;

import com.example.test_app_DTCs.enums.Description;
import com.example.test_app_DTCs.enums.Detail;

import java.io.IOException;
import java.math.BigInteger;

public class TroubleCode {
    public static final String UNKNOWN_TROUBLE_CODE = "Unknown DTC";
    private final String code;
    private final Domain domain;
    private final Type type;

    public TroubleCode(Type type2, Domain domain2, String code2) {
        this.type = type2;
        this.domain = domain2;
        this.code = code2;
    }

    public static TroubleCode createFromString(String line) {
        return new TroubleCode(Type.getType(Character.valueOf(line.charAt(0))), Domain.getDomain(Integer.parseInt(String.valueOf(line.charAt(1)))), line.substring(2));
    }

    public static TroubleCode createFromHex(String hex) {
        return createFromBin(String.format("%16s", new Object[]{new BigInteger(hex.substring(0, 4), 16).toString(2)}).replace(' ', '0'));
    }

    public static TroubleCode createFromBin(String bin) {
        return new TroubleCode(Type.getType(Integer.parseInt(bin.substring(0, 2), 2)), Domain.getDomain(Integer.parseInt(bin.substring(2, 4), 2)), Integer.toHexString(Integer.parseInt(bin.substring(4, 16), 2)));
    }

    public Type getType() {
        return this.type;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public String getDescription(String line) throws IOException {
        System.out.println(line);
        System.out.println(Description.getDescription(line));
        return Description.getDescription(line);
    }

    public Detail getDetail() {
        return Detail.getDetail(Integer.parseInt(this.code.substring(0, 1)));
    }

    public String toString() {
        return this.type.letter.toString() + Integer.toString(this.domain.code) + this.code;
    }

    public boolean isValid() {
        return (this.type == null || this.domain == null) ? false : true;
    }

    public enum Type {
        Powertrain('P', 0),
        Body('B', 2),
        Chassis('C', 1),
        UserNetwork('U', 3);

        private final int code;
        /* access modifiers changed from: private */
        public final Character letter;

        private Type(Character letter2, int code2) {
            this.letter = letter2;
            this.code = code2;
        }

        public static Type getType(Character letter2) {
            for (Type type : values()) {
                if (type.letter.equals(letter2)) {
                    return type;
                }
            }
            return null;
        }

        public static Type getType(int code2) {
            if (code2 < 0 || code2 > 3) {
                throw new IndexOutOfBoundsException("Type code is a 2 bits value, therefor can only vary between 0 and 3 (both included)");
            }
            for (Type type : values()) {
                if (type.code == code2) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum Domain {
        Standard(0),
        Manufacturer(1),
        Custom2(2),
        Custom3(3);

        /* access modifiers changed from: private */
        public final int code;

        private Domain(int code2) {
            this.code = code2;
        }

        public static Domain getDomain(int code2) {
            if (code2 < 0 || code2 > 3) {
                throw new IndexOutOfBoundsException("Domain code is a 2 bits value, therefor can only vary between 0 and 3 (both included)");
            }
            for (Domain domain : values()) {
                if (domain.code == code2) {
                    return domain;
                }
            }
            return null;
        }
    }
}
