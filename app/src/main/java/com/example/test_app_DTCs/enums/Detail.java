package com.example.test_app_DTCs.enums;

public enum Detail {
    Fuel_and_Air_Metering(1),
    Fuel_and_Air_Metering_OR_Injector_Circuit(2),
    Ignition_System_Or_Misfire(3),
    Auxiliary_Emissions_Controls(4),
    Vehicle_Speed_Controls_and_Idle_Control_System(5),
    Computer_Output_Circuit(6),
    Transmission(7);

    private final int dcode;

    private Detail(int dcode2) {
        this.dcode = dcode2;
    }

    public static Detail getDetail(int dcode2) {
        if (dcode2 < 0 || dcode2 > 7) {
            throw new IndexOutOfBoundsException("Domain code is a 2 bits value, therefor can only vary between 0 and 3 (both included)");
        }
        for (Detail detail : values()) {
            if (detail.dcode == dcode2) {
                return detail;
            }
        }
        return null;
    }
}
