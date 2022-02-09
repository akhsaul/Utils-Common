package me.akhsaul.common.network

/*
@TestMethodOrder(OrderAnnotation::class)
class ConnectorTest {
    var req = Connector.Builder()
        .buildClient(Client.USE_COOKIE, Client.USE_DNS)
        .buildRequest("https://www.xhamster.com/".toHttpUrl(), HTTPMethod.GET)
        .setLogLevel(HttpLoggingInterceptor.Level.BODY)
        .build()

    @Test
    @Order(2)
    fun useCache(){
        // change dir cache to build/resources/
        val tmpCache = File(ClassLoader.getSystemResource("new").path).parent
        // start testing
        assertDoesNotThrow {
            req = req.newBuilder()
                .setCacheDirectory(tmpCache)
                .setCacheSize(200L * 1024 * 1024)
                .build()

            val response1Body = req.connect().use {
                assertTrue(it.isSuccessful)

                println("Response 1 response:          $it")
                println("Response 1 cache response:    ${it.cacheResponse}")
                println("Response 1 network response:  ${it.networkResponse}")
                return@use it.body!!.bytes()
            }

            val response2Body = req.connect().use {
                assertTrue(it.isSuccessful)

                println("Response 2 response:          $it")
                println("Response 2 cache response:    ${it.cacheResponse}")
                println("Response 2 network response:  ${it.networkResponse}")
                return@use it.body!!.bytes()
            }

            println("Response 2 equals Response 1? " + (response1Body.contentEquals(response2Body)))
        }
    }

    @Test
    @Order(1)
    fun useDns(){
        assertDoesNotThrow {
            val res = req.connect()
            println(res.body?.string())
            assertTrue(res.isSuccessful)
        }
    }

}*/