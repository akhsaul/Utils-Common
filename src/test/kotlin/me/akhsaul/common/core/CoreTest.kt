package me.akhsaul.common.core

/*
@Suppress("all")
internal class CoreTest {
    private var counter = 0
    private val data = arrayListOf<String>()
    private val total = 100 * 1000

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun shouldThrowException() {
        try {
            runBlocking {
                Runner(
                    Dispatchers.IO, Core.makeExceptionHandler("", false)
                ).asyncAwait {
                    throw ApiException(Error()).addSuppresses(IOException(), IllegalCallerException())
                }
            }
        } catch (e: Throwable) {
            println("TRY CATCH")
            e.printStackTrace()
        }
    }

    @Test
    fun shouldSynchronize() {
        assertDoesNotThrow {
            data.clear()

        }
    }

    @Test
    fun forImmediateThreadTask() {
        assertDoesNotThrow {
            data.clear()
            runBlocking {
                Core.forImmediateTask().asyncAwait {
                    Global.massiveRun {
                        val t = Thread.currentThread()
                        data.add("[n = ${t.name} p = ${t.priority}]")
                        counter++
                    }
                }
            }
            //println("Counter = $counter")
            assertEquals(total, counter)
            println(data.distinct().toString())
        }
    }

    @Test
    fun forNetworkThreadTask() {
        assertDoesNotThrow {
            data.clear()
            runBlocking {
                Core.forNetworkTask().launch {
                    Global.massiveRun {
                        val t = Thread.currentThread()
                        data.add("[n = ${t.name} p = ${t.priority}]")
                        counter++
                    }
                }
                //println("Counter = $counter")
                assertEquals(total, counter)
                println(data.distinct().toString())
            }
        }
    }

    @Test
    fun forIOThreadTask() {
        assertDoesNotThrow {
            data.clear()
            runBlocking {
                Core.forIOTask().launch {
                    Global.massiveRun {
                        val t = Thread.currentThread()
                        data.add("[n = ${t.name} p = ${t.priority}]")
                        counter++
                    }
                }
                //println("Counter = $counter")
                assertEquals(total, counter)
                println(data.distinct().toString())
            }
        }
    }

    @Test
    fun forMainThreadTask() {
        assertDoesNotThrow {
            data.clear()
            runBlocking {
                Core.forMainTask().launch {
                    Global.massiveRun {
                        val t = Thread.currentThread()
                        data.add("[n = ${t.name} p = ${t.priority}]")
                        counter++
                    }
                }
                //println("Counter = $counter")
                assertEquals(total, counter)
                println(data.distinct().toString())
            }
        }
    }

    @Test
    fun forUIThreadTask() {
        assertDoesNotThrow {
            data.clear()
            runBlocking {
                Core.forUITask().launchJoin {
                    Global.massiveRun {
                        val t = Thread.currentThread()
                        data.add("[n = ${t.name} p = ${t.priority}]")
                        counter++
                    }
                }
                //println("Counter = $counter")
                assertEquals(total, counter)
                println(data.distinct().toString())
            }
        }
    }
}
 */