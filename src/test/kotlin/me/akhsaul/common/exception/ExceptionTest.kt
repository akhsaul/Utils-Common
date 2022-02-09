package me.akhsaul.common.exception

import me.akhsaul.common.tools.Data
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.nio.file.Path

internal class ExceptionTest{
    val path = Path.of(".")
    val file = File(".")
    val datas = Data(path)
    @Test
    fun requirement(){
        /*
        assertThrows<RequirementNotMeetException> {
            throw RequirementNotMeetException(true, false)
        }.println()*/
        assertThrows<RequirementNotMeetException> {
            throw RequirementNotMeetException(path, true, false)
        }.println()
    }
}

fun Any.println(){
    println(this)
}