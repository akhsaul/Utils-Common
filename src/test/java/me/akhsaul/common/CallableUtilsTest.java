package me.akhsaul.common;

import me.akhsaul.common.tools.Builder;
import org.junit.jupiter.api.Test;

class CallableUtilsTest {
    @Test
    public void notNull() {
        new Builder.Strings();
        Util.buildString((s)->{
            s.append(1).append("b").append(' ');
            return null;
        });
    }
}