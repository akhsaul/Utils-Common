package me.akhsaul.common.custom;

import java.time.LocalDate;

public interface Power {

    String getName();
    String getDeviceName();

    double getRemainingCapacityPercent();

    double getTimeRemainingEstimated();

    double getTimeRemainingInstant();

    double getPowerUsageRate();

    double getVoltage();

    double getAmperage();

    boolean isPowerOnLine();

    boolean isCharging();

    boolean isDischarging();

    int getCurrentCapacity();

    int getMaxCapacity();

    int getDesignCapacity();

    int getCycleCount();

    String getChemistry();

    LocalDate getManufactureDate();

    String getManufacturer();

    String getSerialNumber();

    double getTemperature();

    boolean updateAttributes();
}
