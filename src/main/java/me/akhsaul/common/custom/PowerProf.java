package me.akhsaul.common.custom;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

import java.util.Arrays;

public interface PowerProf extends com.sun.jna.platform.win32.PowrProf {
    PowerProf INSTANCE = Native.load("PowrProf", PowerProf.class);

    @FieldOrder({"acOnLine", "batteryPresent", "charging", "discharging", "spare1", "tag", "maxCapacity",
            "remainingCapacity", "rate", "estimatedTime", "defaultAlert1", "defaultAlert2"})
    public class BatteryState extends Structure {
        public byte acOnLine;
        public byte batteryPresent;
        public byte charging;
        public byte discharging;
        public byte[] spare1 = new byte[3];
        public byte tag;
        public int maxCapacity;
        public int remainingCapacity;
        public int rate;
        public int estimatedTime;
        public int defaultAlert1;
        public int defaultAlert2;

        public BatteryState(Pointer p) {
            super(p);
            read();
        }

        @Override
        public String toString() {
            return "BatteryState{" +
                    "acOnLine=" + acOnLine +
                    ", batteryPresent=" + batteryPresent +
                    ", charging=" + charging +
                    ", discharging=" + discharging +
                    ", spare1=" + Arrays.toString(spare1) +
                    ", tag=" + tag +
                    ", maxCapacity=" + maxCapacity +
                    ", remainingCapacity=" + remainingCapacity +
                    ", rate=" + rate +
                    ", estimatedTime=" + estimatedTime +
                    ", defaultAlert1=" + defaultAlert1 +
                    ", defaultAlert2=" + defaultAlert2 +
                    '}';
        }

        public BatteryState() {
            super();
        }
    }
}
