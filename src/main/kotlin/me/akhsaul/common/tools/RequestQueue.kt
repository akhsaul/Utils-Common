package me.akhsaul.common.tools

object RequestQueue {
    private val requests = mutableSetOf<Request>()

    fun add(request: Request): Request {
        requests.add(request)
        request.method
        return request
    }
}