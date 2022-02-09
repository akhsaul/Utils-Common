package me.akhsaul.common.custom;

import kotlin.NotImplementedError;

import static oshi.util.Memoizer.memoize;
import java.util.function.Supplier;

public class Sistem {
    static Supplier<HardwareAbstraction> hardware = memoize(Sistem::createHardware);

    public static HardwareAbstraction createHardware(){
        throw new NotImplementedError();
    }
}
